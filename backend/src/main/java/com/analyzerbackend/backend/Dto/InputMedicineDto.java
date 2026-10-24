package com.analyzerbackend.backend.Dto;

public class InputMedicineDto {
    private String rawText;
    private String matchedName;
    private String salt;
    private String strength;
    private String dosageForm;
    private Double price;
    private Double confidence;
    private Boolean needsReview;

    public InputMedicineDto() {
    }

    public InputMedicineDto(String rawText, String matchedName, String salt, String strength,
                            String dosageForm, Double price, Double confidence, Boolean needsReview) {
        this.rawText = rawText;
        this.matchedName = matchedName;
        this.salt = salt;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.price = price;
        this.confidence = confidence;
        this.needsReview = needsReview;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String rawText;
        private String matchedName;
        private String salt;
        private String strength;
        private String dosageForm;
        private Double price;
        private Double confidence;
        private Boolean needsReview;

        public Builder rawText(String rawText) { this.rawText = rawText; return this; }
        public Builder matchedName(String matchedName) { this.matchedName = matchedName; return this; }
        public Builder salt(String salt) { this.salt = salt; return this; }
        public Builder strength(String strength) { this.strength = strength; return this; }
        public Builder dosageForm(String dosageForm) { this.dosageForm = dosageForm; return this; }
        public Builder price(Double price) { this.price = price; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
        public Builder needsReview(Boolean needsReview) { this.needsReview = needsReview; return this; }

        public InputMedicineDto build() {
            return new InputMedicineDto(rawText, matchedName, salt, strength, dosageForm, price, confidence, needsReview);
        }
    }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public String getMatchedName() { return matchedName; }
    public void setMatchedName(String matchedName) { this.matchedName = matchedName; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }

    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public Boolean getNeedsReview() { return needsReview; }
    public void setNeedsReview(Boolean needsReview) { this.needsReview = needsReview; }
}
