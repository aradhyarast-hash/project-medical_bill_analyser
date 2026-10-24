package com.analyzerbackend.backend.Dto;

import java.util.ArrayList;
import java.util.List;

public class MedicineAlternativeDto {
    private String name;
    private String salt;
    private String strength;
    private String dosageForm;
    private String releaseType;
    private String manufacturer;
    private String packSize;
    private Double price;
    private Double unitPrice;
    private List<String> tags = new ArrayList<>();
    private Double savingsAmount;
    private Double savingsPercent;
    private String reason;

    public MedicineAlternativeDto() {
    }

    public MedicineAlternativeDto(String name, String salt, String strength, String dosageForm,
                                  String releaseType, String manufacturer, String packSize,
                                  Double price, Double unitPrice, List<String> tags,
                                  Double savingsAmount, Double savingsPercent, String reason) {
        this.name = name;
        this.salt = salt;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.releaseType = releaseType;
        this.manufacturer = manufacturer;
        this.packSize = packSize;
        this.price = price;
        this.unitPrice = unitPrice;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.savingsAmount = savingsAmount;
        this.savingsPercent = savingsPercent;
        this.reason = reason;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String salt;
        private String strength;
        private String dosageForm;
        private String releaseType;
        private String manufacturer;
        private String packSize;
        private Double price;
        private Double unitPrice;
        private List<String> tags = new ArrayList<>();
        private Double savingsAmount;
        private Double savingsPercent;
        private String reason;

        public Builder name(String name) { this.name = name; return this; }
        public Builder salt(String salt) { this.salt = salt; return this; }
        public Builder strength(String strength) { this.strength = strength; return this; }
        public Builder dosageForm(String dosageForm) { this.dosageForm = dosageForm; return this; }
        public Builder releaseType(String releaseType) { this.releaseType = releaseType; return this; }
        public Builder manufacturer(String manufacturer) { this.manufacturer = manufacturer; return this; }
        public Builder packSize(String packSize) { this.packSize = packSize; return this; }
        public Builder price(Double price) { this.price = price; return this; }
        public Builder unitPrice(Double unitPrice) { this.unitPrice = unitPrice; return this; }
        public Builder tags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); return this; }
        public Builder savingsAmount(Double savingsAmount) { this.savingsAmount = savingsAmount; return this; }
        public Builder savingsPercent(Double savingsPercent) { this.savingsPercent = savingsPercent; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }

        public MedicineAlternativeDto build() {
            return new MedicineAlternativeDto(name, salt, strength, dosageForm, releaseType,
                    manufacturer, packSize, price, unitPrice, tags, savingsAmount, savingsPercent, reason);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }

    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }

    public String getReleaseType() { return releaseType; }
    public void setReleaseType(String releaseType) { this.releaseType = releaseType; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getPackSize() { return packSize; }
    public void setPackSize(String packSize) { this.packSize = packSize; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Double getSavingsAmount() { return savingsAmount; }
    public void setSavingsAmount(Double savingsAmount) { this.savingsAmount = savingsAmount; }

    public Double getSavingsPercent() { return savingsPercent; }
    public void setSavingsPercent(Double savingsPercent) { this.savingsPercent = savingsPercent; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
