package com.analyzerbackend.backend.Service.Impl;

import com.analyzerbackend.backend.Dto.MedicineAlternativeDto;
import com.analyzerbackend.backend.Entity.Medicine;
import com.analyzerbackend.backend.Repository.MedicineRepository;
import com.analyzerbackend.backend.Service.AlternativeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlternativeServiceImpl implements AlternativeService {

    private static final Logger log = LoggerFactory.getLogger(AlternativeServiceImpl.class);

    private final MedicineRepository medicineRepository;

    public AlternativeServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    private static final Set<String> ESTABLISHED_MANUFACTURERS = new HashSet<>(Arrays.asList(
            "glaxosmithkline", "cipla", "dr reddys laboratories", "sun pharma",
            "micro labs", "usv", "alkem laboratories", "alembic pharmaceuticals",
            "zydus lifesciences", "glenmark pharmaceuticals", "mankind pharma"
    ));

    @Override
    public List<MedicineAlternativeDto> findAlternatives(
            Medicine prescribedMedicine,
            String parsedSalt,
            String parsedStrength,
            String parsedForm,
            String parsedReleaseType,
            Double inputPrice
    ) {
        String salt = prescribedMedicine != null ? prescribedMedicine.getSaltNormalized() : parsedSalt;
        String strength = prescribedMedicine != null ? prescribedMedicine.getStrength() : parsedStrength;
        String dosageForm = prescribedMedicine != null ? prescribedMedicine.getDosageForm() : parsedForm;
        String releaseType = prescribedMedicine != null ? prescribedMedicine.getReleaseType() : parsedReleaseType;

        if (salt == null || salt.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch all medicines with same salt
        List<Medicine> sameSaltList = medicineRepository.findBySaltNormalizedIgnoreCase(salt.trim());

        // Strictly match: same active ingredient AND same strength AND same dosage form AND same release type
        List<Medicine> validCandidates = sameSaltList.stream()
                .filter(m -> strength == null || strength.trim().isEmpty() || m.getStrength().equalsIgnoreCase(strength.trim()))
                .filter(m -> dosageForm == null || dosageForm.trim().isEmpty() || m.getDosageForm().equalsIgnoreCase(dosageForm.trim()))
                .filter(m -> isReleaseTypeCompatible(releaseType, m.getReleaseType()))
                .collect(Collectors.toList());

        if (validCandidates.isEmpty()) {
            return Collections.emptyList();
        }

        // Determine reference unit price for savings
        Double referenceUnitPrice = null;
        if (inputPrice != null && inputPrice > 0.0) {
            referenceUnitPrice = inputPrice;
        } else if (prescribedMedicine != null && prescribedMedicine.getPricePerUnitInr() != null) {
            referenceUnitPrice = prescribedMedicine.getPricePerUnitInr();
        }

        boolean isPrescribedUnavailable = prescribedMedicine == null ||
                Boolean.TRUE.equals(prescribedMedicine.getIsDiscontinued());

        // Find cheapest unit price among active candidates
        double minPrice = validCandidates.stream()
                .filter(m -> !Boolean.TRUE.equals(m.getIsDiscontinued()))
                .mapToDouble(m -> m.getPricePerUnitInr() != null ? m.getPricePerUnitInr() : Double.MAX_VALUE)
                .min()
                .orElse(Double.MAX_VALUE);

        // Find "BEST": highest rating if present, else established manufacturer
        Long bestCandidateId = identifyBestOption(validCandidates, prescribedMedicine);

        // Map to DTOs and tag
        List<MedicineAlternativeDto> dtoList = new ArrayList<>();
        for (Medicine m : validCandidates) {
            // If medicine is discontinued and NOT the prescribed medicine, skip it
            if (Boolean.TRUE.equals(m.getIsDiscontinued()) && (prescribedMedicine == null || !m.getId().equals(prescribedMedicine.getId()))) {
                continue;
            }

            List<String> tags = new ArrayList<>();
            tags.add("SAME_SALT");

            if (isPrescribedUnavailable && (prescribedMedicine == null || !m.getId().equals(prescribedMedicine.getId()))) {
                tags.add("ALTERNATE");
            }

            if (Boolean.TRUE.equals(m.getIsJanAushadhi())) {
                tags.add("JAN_AUSHADHI");
                tags.add("GENERIC");
            }

            if (m.getPricePerUnitInr() != null && Math.abs(m.getPricePerUnitInr() - minPrice) < 0.001) {
                tags.add("CHEAPEST");
            }

            if (bestCandidateId != null && m.getId().equals(bestCandidateId)) {
                tags.add("BEST");
            }

            // Savings calculation
            Double savingsAmount = null;
            Double savingsPercent = null;
            if (referenceUnitPrice != null && m.getPricePerUnitInr() != null) {
                double diff = referenceUnitPrice - m.getPricePerUnitInr();
                if (diff > 0.01) {
                    savingsAmount = Math.round(diff * 100.0) / 100.0;
                    savingsPercent = Math.round(((diff / referenceUnitPrice) * 100.0) * 10.0) / 10.0;
                } else {
                    savingsAmount = 0.0;
                    savingsPercent = 0.0;
                }
            }

            String reason = buildReason(m, prescribedMedicine, isPrescribedUnavailable, tags);

            dtoList.add(MedicineAlternativeDto.builder()
                    .name(m.getBrandName())
                    .salt(m.getSaltComposition())
                    .strength(m.getStrength())
                    .dosageForm(m.getDosageForm())
                    .releaseType(m.getReleaseType())
                    .manufacturer(m.getManufacturer())
                    .packSize(m.getPackSizeLabel())
                    .price(m.getMrpInr())
                    .unitPrice(m.getPricePerUnitInr())
                    .tags(tags)
                    .savingsAmount(savingsAmount)
                    .savingsPercent(savingsPercent)
                    .reason(reason)
                    .build());
        }

        // Ordering:
        // 1. Matched/prescribed medicine first if active
        // 2. Cheapest
        // 3. Lowest unit price
        dtoList.sort((a, b) -> {
            boolean aIsPrescribed = prescribedMedicine != null && a.getName().equalsIgnoreCase(prescribedMedicine.getBrandName());
            boolean bIsPrescribed = prescribedMedicine != null && b.getName().equalsIgnoreCase(prescribedMedicine.getBrandName());
            if (aIsPrescribed && !bIsPrescribed) return -1;
            if (bIsPrescribed && !aIsPrescribed) return 1;

            boolean aCheapest = a.getTags().contains("CHEAPEST");
            boolean bCheapest = b.getTags().contains("CHEAPEST");
            if (aCheapest && !bCheapest) return -1;
            if (bCheapest && !aCheapest) return 1;

            double pA = a.getUnitPrice() != null ? a.getUnitPrice() : Double.MAX_VALUE;
            double pB = b.getUnitPrice() != null ? b.getUnitPrice() : Double.MAX_VALUE;
            return Double.compare(pA, pB);
        });

        // Return at most 5 results (do not pad with unrelated medicines)
        return dtoList.stream().limit(5).collect(Collectors.toList());
    }

    private boolean isReleaseTypeCompatible(String r1, String r2) {
        if (r1 == null || r2 == null) return true;
        String s1 = r1.trim().toLowerCase();
        String s2 = r2.trim().toLowerCase();
        if (s1.equals(s2)) return true;

        boolean s1Prolonged = s1.contains("prolonged") || s1.contains("sr") || s1.contains("er");
        boolean s2Prolonged = s2.contains("prolonged") || s2.contains("sr") || s2.contains("er");
        if (s1Prolonged || s2Prolonged) {
            return s1Prolonged && s2Prolonged;
        }

        boolean s1Gastro = s1.contains("gastro") || s1.contains("enteric");
        boolean s2Gastro = s2.contains("gastro") || s2.contains("enteric");
        if (s1Gastro || s2Gastro) {
            return s1Gastro && s2Gastro;
        }

        return true;
    }

    private Long identifyBestOption(List<Medicine> candidates, Medicine prescribed) {
        // 1. If any candidate has explicit rating, pick highest
        Optional<Medicine> rated = candidates.stream()
                .filter(m -> !Boolean.TRUE.equals(m.getIsDiscontinued()))
                .filter(m -> m.getRatingExcellentPct() != null && m.getRatingExcellentPct() > 0)
                .max(Comparator.comparingDouble(Medicine::getRatingExcellentPct));
        if (rated.isPresent()) {
            return rated.get().getId();
        }

        // 2. If prescribed brand is in candidate list and from established manufacturer
        if (prescribed != null && !Boolean.TRUE.equals(prescribed.getIsDiscontinued())) {
            return prescribed.getId();
        }

        // 3. Pick established manufacturer
        for (Medicine m : candidates) {
            if (Boolean.TRUE.equals(m.getIsDiscontinued())) continue;
            if (m.getManufacturer() != null &&
                ESTABLISHED_MANUFACTURERS.contains(m.getManufacturer().toLowerCase().trim())) {
                return m.getId();
            }
        }

        // 4. Default to first active candidate
        return candidates.stream()
                .filter(m -> !Boolean.TRUE.equals(m.getIsDiscontinued()))
                .findFirst()
                .map(Medicine::getId)
                .orElse(null);
    }

    private String buildReason(Medicine candidate, Medicine prescribed, boolean prescribedUnavailable, List<String> tags) {
        if (prescribed != null && candidate.getId().equals(prescribed.getId())) {
            if (Boolean.TRUE.equals(candidate.getIsDiscontinued())) {
                return "Prescribed medicine is listed as discontinued. Consider switching to one of the suggested safe alternatives below.";
            }
            return "Exact prescribed medicine found in reference database.";
        }

        if (prescribedUnavailable) {
            if (prescribed != null && Boolean.TRUE.equals(prescribed.getIsDiscontinued())) {
                return String.format("Suggested alternative: Prescribed medicine '%s' is discontinued in reference data. Matches same active salt, strength and form.", prescribed.getBrandName());
            }
            return "Suggested alternative with matching active ingredient, strength and dosage form.";
        }

        if (tags.contains("CHEAPEST") && tags.contains("JAN_AUSHADHI")) {
            return "Government Jan Aushadhi generic alternative with lowest price per unit and identical active salt.";
        }
        if (tags.contains("CHEAPEST")) {
            return "Cheapest verified option with identical active salt, strength and dosage form.";
        }
        if (tags.contains("BEST")) {
            return "Reputable brand from established manufacturer with identical active formulation.";
        }
        return "Therapeutically identical alternative with matching salt, strength and dosage form.";
    }
}
