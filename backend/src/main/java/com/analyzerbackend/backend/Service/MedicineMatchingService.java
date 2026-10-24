package com.analyzerbackend.backend.Service;

import com.analyzerbackend.backend.Dto.InputMedicineDto;
import com.analyzerbackend.backend.Entity.Medicine;

import java.util.Optional;

public interface MedicineMatchingService {

    class MatchResult {
        private final Medicine medicine;
        private final double confidence;
        private final boolean needsReview;
        private final String matchMethod;

        public MatchResult(Medicine medicine, double confidence, boolean needsReview, String matchMethod) {
            this.medicine = medicine;
            this.confidence = confidence;
            this.needsReview = needsReview;
            this.matchMethod = matchMethod;
        }

        public Optional<Medicine> getMedicine() {
            return Optional.ofNullable(medicine);
        }

        public double getConfidence() {
            return confidence;
        }

        public boolean isNeedsReview() {
            return needsReview;
        }

        public String getMatchMethod() {
            return matchMethod;
        }
    }

    MatchResult matchMedicine(InputMedicineDto input);
    MatchResult matchByName(String medicineName);
}
