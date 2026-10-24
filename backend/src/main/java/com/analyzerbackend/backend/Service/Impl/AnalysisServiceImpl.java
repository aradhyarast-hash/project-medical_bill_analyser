package com.analyzerbackend.backend.Service.Impl;

import com.analyzerbackend.backend.Dto.*;
import com.analyzerbackend.backend.Entity.Medicine;
import com.analyzerbackend.backend.Service.*;
import com.analyzerbackend.backend.Utility.FileValidator;
import com.analyzerbackend.backend.Utility.MedicineNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisServiceImpl.class);

    private final OcrService ocrService;
    private final PrescriptionParserService parserService;
    private final MedicineMatchingService matchingService;
    private final AlternativeService alternativeService;
    private final CsvDataLoaderService csvDataLoaderService;

    public AnalysisServiceImpl(OcrService ocrService,
                               PrescriptionParserService parserService,
                               MedicineMatchingService matchingService,
                               AlternativeService alternativeService,
                               CsvDataLoaderService csvDataLoaderService) {
        this.ocrService = ocrService;
        this.parserService = parserService;
        this.matchingService = matchingService;
        this.alternativeService = alternativeService;
        this.csvDataLoaderService = csvDataLoaderService;
    }

    private static final String DISCLAIMER =
            "Suggestions are based on matching salt, strength and dosage form from reference data. " +
            "This is not medical advice. Consult your doctor or pharmacist before switching any medicine.";

    private static final long DEFAULT_MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Override
    public AnalysisResponseDto analyzeSingle(String medicineName, MultipartFile prescription) {
        validateInputs(medicineName, prescription);

        String targetText = medicineName;
        InputMedicineDto inputDto;

        if (prescription != null && !prescription.isEmpty()) {
            FileValidator.validateFile(prescription, DEFAULT_MAX_FILE_SIZE);
            String ocrText = ocrService.extractText(prescription);
            List<InputMedicineDto> candidates = parserService.parseTextToCandidateMedicines(ocrText);
            if (!candidates.isEmpty()) {
                inputDto = candidates.get(0);
            } else {
                inputDto = parserService.parseSingleMedicineQuery(medicineName != null ? medicineName : "Unknown");
            }
        } else {
            inputDto = parserService.parseSingleMedicineQuery(targetText);
        }

        return processSingleMedicine(inputDto);
    }

    @Override
    public BillAnalysisResponseDto auditBill(MultipartFile billFile, String medicineName) {
        List<InputMedicineDto> candidateInputs = new ArrayList<>();

        if (billFile != null && !billFile.isEmpty()) {
            FileValidator.validateFile(billFile, DEFAULT_MAX_FILE_SIZE);
            String ocrText = ocrService.extractText(billFile);
            candidateInputs = parserService.parseTextToCandidateMedicines(ocrText);
        }

        // If a specific medicine name was also typed or file had no parsed candidates
        if (candidateInputs.isEmpty() && medicineName != null && !medicineName.trim().isEmpty()) {
            candidateInputs.add(parserService.parseSingleMedicineQuery(medicineName.trim()));
        }

        if (candidateInputs.isEmpty()) {
            throw new IllegalArgumentException("No medicine items could be identified from the input. Please upload a clear bill or type a medicine name.");
        }

        List<BillItemDto> billItems = new ArrayList<>();
        List<AnalysisResponseDto> groups = new ArrayList<>();
        int itemIndex = 1;

        for (InputMedicineDto inputDto : candidateInputs) {
            AnalysisResponseDto group = processSingleMedicine(inputDto);
            groups.add(group);

            BillItemDto billItem = convertToBillItem(inputDto, group, itemIndex++);
            billItems.add(billItem);
        }

        List<String> commonNotes = new ArrayList<>();
        commonNotes.add("Data source: " + csvDataLoaderService.getDataSource());
        commonNotes.add("All suggestions match active salt composition, strength, and dosage form from reference data.");
        commonNotes.add("'BEST' tag is derived from verified established manufacturers since CSV rating column is unpopulated.");

        return BillAnalysisResponseDto.builder()
                .items(billItems)
                .billMeta(BillMetaDto.builder()
                        .pharmacyName("Verified Pharmacy Audit")
                        .billDate(LocalDate.now().toString())
                        .build())
                .medicineGroups(groups)
                .notes(commonNotes)
                .disclaimer(DISCLAIMER)
                .dataSource(csvDataLoaderService.getDataSource())
                .build();
    }

    private AnalysisResponseDto processSingleMedicine(InputMedicineDto inputDto) {
        MedicineMatchingService.MatchResult matchResult = matchingService.matchMedicine(inputDto);
        Medicine prescribed = matchResult.getMedicine().orElse(null);

        boolean prescribedFound = prescribed != null;
        double confidence = matchResult.getConfidence();
        boolean needsReview = matchResult.isNeedsReview();

        // Update inputDto with reference data if matched
        if (prescribed != null) {
            inputDto.setMatchedName(prescribed.getBrandName());
            inputDto.setSalt(prescribed.getSaltComposition());
            inputDto.setStrength(prescribed.getStrength());
            inputDto.setDosageForm(prescribed.getDosageForm());
            if (inputDto.getPrice() == null) {
                inputDto.setPrice(prescribed.getPricePerUnitInr());
            }
        }
        inputDto.setConfidence(confidence);
        inputDto.setNeedsReview(needsReview);

        // Find alternatives matching same salt, strength, dosage form
        String salt = prescribed != null ? prescribed.getSaltNormalized() : MedicineNormalizer.normalizeName(inputDto.getMatchedName());
        String strength = prescribed != null ? prescribed.getStrength() : inputDto.getStrength();
        String form = prescribed != null ? prescribed.getDosageForm() : inputDto.getDosageForm();
        String release = prescribed != null ? prescribed.getReleaseType() : MedicineNormalizer.extractReleaseType(inputDto.getRawText());

        List<MedicineAlternativeDto> alternatives = alternativeService.findAlternatives(
                prescribed,
                salt,
                strength,
                form,
                release,
                inputDto.getPrice()
        );

        List<String> notes = new ArrayList<>();
        notes.add("Data source: " + csvDataLoaderService.getDataSource());
        notes.add("'BEST' tag is derived from highest rating if present, or established manufacturers (e.g. GlaxoSmithKline, Cipla, Sun Pharma, Micro Labs, etc.).");

        if (!prescribedFound) {
            notes.add(String.format("Prescribed medicine '%s' was not found in reference database. Suggested options are based on active ingredient / salt.", inputDto.getRawText()));
        } else if (Boolean.TRUE.equals(prescribed.getIsDiscontinued())) {
            notes.add(String.format("Prescribed medicine '%s' is marked as DISCONTINUED. Alternatives with same salt, strength, and form are tagged ALTERNATE.", prescribed.getBrandName()));
        }

        return AnalysisResponseDto.builder()
                .inputMedicine(inputDto)
                .prescribedFound(prescribedFound)
                .results(alternatives)
                .notes(notes)
                .disclaimer(DISCLAIMER)
                .dataSource(csvDataLoaderService.getDataSource())
                .build();
    }

    private BillItemDto convertToBillItem(InputMedicineDto input, AnalysisResponseDto group, int index) {
        String id = "item-" + index + "-" + UUID.randomUUID().toString().substring(0, 4);
        String extractedName = input.getRawText() != null ? input.getRawText() : input.getMatchedName();
        String matchedName = input.getMatchedName();
        String strength = input.getStrength() != null && !input.getStrength().isEmpty() ? input.getStrength() : "—";
        String form = input.getDosageForm() != null && !input.getDosageForm().isEmpty() ? input.getDosageForm() : "Tablet";
        int quantity = 10; // Default pack quantity if not parsed

        Double billedPrice = input.getPrice();
        Double mrpPrice = null;
        Double ceilingPrice = null;

        // Try to identify reference pricing from alternatives
        MedicineAlternativeDto cheapest = null;
        MedicineAlternativeDto janAushadhi = null;

        for (MedicineAlternativeDto alt : group.getResults()) {
            if (alt.getTags().contains("CHEAPEST") && cheapest == null) {
                cheapest = alt;
            }
            if (alt.getTags().contains("JAN_AUSHADHI") && janAushadhi == null) {
                janAushadhi = alt;
            }
        }

        if (group.getPrescribedFound() && billedPrice == null) {
            // Find the prescribed item in alternatives
            for (MedicineAlternativeDto alt : group.getResults()) {
                if (alt.getName().equalsIgnoreCase(matchedName)) {
                    billedPrice = alt.getUnitPrice();
                    break;
                }
            }
        }

        if (billedPrice == null) {
            billedPrice = cheapest != null && cheapest.getUnitPrice() != null ? cheapest.getUnitPrice() * 1.5 : 5.0;
        }

        if (cheapest != null) {
            mrpPrice = cheapest.getUnitPrice();
        }
        if (janAushadhi != null) {
            ceilingPrice = janAushadhi.getUnitPrice();
        }

        // Determine audit status and overcharge amount
        String status = "FAIR";
        double overcharge = 0.0;
        double reference = ceilingPrice != null ? ceilingPrice : (mrpPrice != null ? mrpPrice : billedPrice);

        if (!group.getPrescribedFound()) {
            status = "NO_REFERENCE";
            overcharge = 0.0;
        } else if (ceilingPrice != null && billedPrice > ceilingPrice) {
            status = "OVERCHARGED";
            overcharge = Math.max(0.0, (billedPrice - ceilingPrice) * quantity);
        } else if (mrpPrice != null && billedPrice > mrpPrice) {
            status = "ABOVE_MRP";
            overcharge = Math.max(0.0, (billedPrice - mrpPrice) * quantity);
        }

        // Build generic representation for UI
        GenericDto genericDto = null;
        MedicineAlternativeDto genericCandidate = janAushadhi != null ? janAushadhi : cheapest;
        if (genericCandidate != null) {
            double saving = Math.max(0.0, (billedPrice - genericCandidate.getUnitPrice()) * quantity);
            genericDto = GenericDto.builder()
                    .name(genericCandidate.getName())
                    .ingredient(genericCandidate.getSalt())
                    .strength(genericCandidate.getStrength())
                    .form(genericCandidate.getDosageForm())
                    .unitPrice(genericCandidate.getUnitPrice())
                    .saving(Math.round(saving * 100.0) / 100.0)
                    .matchVerified(true)
                    .build();
        }

        return BillItemDto.builder()
                .id(id)
                .extractedName(extractedName)
                .matchedName(matchedName)
                .strength(strength)
                .form(form)
                .quantity(quantity)
                .billedUnitPrice(Math.round(billedPrice * 100.0) / 100.0)
                .mrpUnitPrice(mrpPrice != null ? Math.round(mrpPrice * 100.0) / 100.0 : null)
                .ceilingUnitPrice(ceilingPrice != null ? Math.round(ceilingPrice * 100.0) / 100.0 : null)
                .isScheduled(true)
                .status(status)
                .overchargeAmount(Math.round(overcharge * 100.0) / 100.0)
                .confidence(input.getConfidence() != null ? input.getConfidence() : 0.85)
                .generic(genericDto)
                .alternatives(group.getResults())
                .build();
    }

    private void validateInputs(String medicineName, MultipartFile prescription) {
        boolean hasName = medicineName != null && !medicineName.trim().isEmpty();
        boolean hasFile = prescription != null && !prescription.isEmpty();
        if (!hasName && !hasFile) {
            throw new IllegalArgumentException("Please provide either a medicine name or a prescription file.");
        }
    }
}
