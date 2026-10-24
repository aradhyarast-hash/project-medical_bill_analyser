package com.analyzerbackend.backend.Service.Impl;

import com.analyzerbackend.backend.Dto.InputMedicineDto;
import com.analyzerbackend.backend.Entity.Medicine;
import com.analyzerbackend.backend.Repository.MedicineRepository;
import com.analyzerbackend.backend.Service.MedicineMatchingService;
import com.analyzerbackend.backend.Utility.MedicineNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineMatchingServiceImpl implements MedicineMatchingService {

    private static final Logger log = LoggerFactory.getLogger(MedicineMatchingServiceImpl.class);

    private final MedicineRepository medicineRepository;

    public MedicineMatchingServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public MatchResult matchByName(String medicineName) {
        InputMedicineDto dto = InputMedicineDto.builder()
                .rawText(medicineName)
                .matchedName(medicineName)
                .strength(MedicineNormalizer.extractStrength(medicineName))
                .dosageForm(MedicineNormalizer.extractDosageForm(medicineName))
                .confidence(0.95)
                .needsReview(false)
                .build();
        return matchMedicine(dto);
    }

    @Override
    public MatchResult matchMedicine(InputMedicineDto input) {
        if (input == null || (input.getMatchedName() == null && input.getRawText() == null)) {
            return new MatchResult(null, 0.0, true, "NOT_FOUND");
        }

        String searchName = input.getMatchedName() != null ? input.getMatchedName().trim() : input.getRawText().trim();
        String normalizedSearch = MedicineNormalizer.normalizeName(searchName);

        // 1. Exact match on brand name
        Optional<Medicine> exactMatch = medicineRepository.findFirstByBrandNameIgnoreCase(searchName);
        if (exactMatch.isPresent()) {
            Medicine m = exactMatch.get();
            double conf = input.getConfidence() != null ? input.getConfidence() : 0.95;
            return new MatchResult(m, Math.min(0.98, conf), false, "EXACT_BRAND");
        }

        // 2. Exact match on normalized brand name
        Optional<Medicine> normMatch = medicineRepository.findFirstByBrandNameNormalizedIgnoreCase(normalizedSearch);
        if (normMatch.isPresent()) {
            Medicine m = normMatch.get();
            double conf = (input.getConfidence() != null ? input.getConfidence() : 0.95) * 0.98;
            return new MatchResult(m, Math.round(conf * 100.0) / 100.0, false, "NORMALIZED_BRAND");
        }

        // 3. Match key lookup if strength and form can be determined
        String extractedStrength = input.getStrength() != null && !input.getStrength().isEmpty()
                ? input.getStrength()
                : MedicineNormalizer.extractStrength(searchName);
        String extractedForm = input.getDosageForm() != null && !input.getDosageForm().isEmpty()
                ? input.getDosageForm()
                : MedicineNormalizer.extractDosageForm(searchName);
        String releaseType = MedicineNormalizer.extractReleaseType(searchName);

        // Check if searchName is actually a salt name (e.g. "Paracetamol")
        List<Medicine> saltMatches = medicineRepository.findBySaltNormalizedIgnoreCase(normalizedSearch);
        if (!saltMatches.isEmpty()) {
            // Find best matching strength/form among the salt matches
            Medicine bestSalt = saltMatches.stream()
                    .filter(m -> extractedStrength.isEmpty() || m.getStrength().equalsIgnoreCase(extractedStrength))
                    .findFirst()
                    .orElse(saltMatches.get(0));
            return new MatchResult(bestSalt, 0.90, false, "SALT_NAME_MATCH");
        }

        // 4. Fuzzy match against all medicines
        List<Medicine> allMedicines = medicineRepository.findAll();
        Medicine bestCandidate = null;
        double bestScore = 0.0;

        for (Medicine m : allMedicines) {
            double brandSim = MedicineNormalizer.computeSimilarity(normalizedSearch, m.getBrandNameNormalized());
            double score = brandSim;

            // Give a boost if strength also matches
            if (!extractedStrength.isEmpty() && m.getStrength().equalsIgnoreCase(extractedStrength)) {
                score += 0.05;
            }

            if (score > bestScore) {
                bestScore = score;
                bestCandidate = m;
            }
        }

        if (bestCandidate != null && bestScore >= 0.70) {
            double finalConf = (input.getConfidence() != null ? input.getConfidence() : 0.85) * Math.min(1.0, bestScore);
            boolean needsReview = bestScore < 0.85 || (input.getNeedsReview() != null && input.getNeedsReview());
            return new MatchResult(bestCandidate, Math.round(finalConf * 100.0) / 100.0, needsReview, "FUZZY_BRAND");
        }

        // 5. Not found in database
        return new MatchResult(null, 0.40, true, "NOT_FOUND");
    }
}
