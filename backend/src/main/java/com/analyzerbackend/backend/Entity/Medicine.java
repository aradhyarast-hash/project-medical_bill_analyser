package com.analyzerbackend.backend.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medicines", indexes = {
        @Index(name = "idx_brand_name_norm", columnList = "brand_name_normalized"),
        @Index(name = "idx_salt_norm", columnList = "salt_normalized"),
        @Index(name = "idx_match_key", columnList = "match_key"),
        @Index(name = "idx_salt_strength_form", columnList = "salt_normalized, strength, dosage_form")
})
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @Column(name = "brand_name_normalized", nullable = false)
    private String brandNameNormalized;

    @Column(name = "salt_composition")
    private String saltComposition;

    @Column(name = "salt_normalized", nullable = false)
    private String saltNormalized;

    @Column(name = "strength", nullable = false)
    private String strength;

    @Column(name = "dosage_form", nullable = false)
    private String dosageForm;

    @Column(name = "release_type")
    private String releaseType;

    @Column(name = "match_key")
    private String matchKey;

    @Column(name = "therapeutic_group")
    private String therapeuticGroup;

    @Column(name = "pack_size_label")
    private String packSizeLabel;

    @Column(name = "units_per_pack")
    private Integer unitsPerPack;

    @Column(name = "mrp_inr")
    private Double mrpInr;

    @Column(name = "price_per_unit_inr")
    private Double pricePerUnitInr;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "medicine_type")
    private String medicineType;

    @Column(name = "prescription_required")
    private Boolean prescriptionRequired;

    @Column(name = "is_discontinued")
    private Boolean isDiscontinued;

    @Column(name = "is_jan_aushadhi")
    private Boolean isJanAushadhi;

    @Column(name = "rating_excellent_pct")
    private Double ratingExcellentPct;

    @Column(name = "source")
    private String source;

    @Column(name = "price_as_of")
    private String priceAsOf;

    @Column(name = "is_sample_data")
    private Boolean isSampleData;

    public Medicine() {
    }

    public Medicine(Long id, String brandName, String brandNameNormalized, String saltComposition,
                    String saltNormalized, String strength, String dosageForm, String releaseType,
                    String matchKey, String therapeuticGroup, String packSizeLabel, Integer unitsPerPack,
                    Double mrpInr, Double pricePerUnitInr, String manufacturer, String medicineType,
                    Boolean prescriptionRequired, Boolean isDiscontinued, Boolean isJanAushadhi,
                    Double ratingExcellentPct, String source, String priceAsOf, Boolean isSampleData) {
        this.id = id;
        this.brandName = brandName;
        this.brandNameNormalized = brandNameNormalized;
        this.saltComposition = saltComposition;
        this.saltNormalized = saltNormalized;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.releaseType = releaseType;
        this.matchKey = matchKey;
        this.therapeuticGroup = therapeuticGroup;
        this.packSizeLabel = packSizeLabel;
        this.unitsPerPack = unitsPerPack;
        this.mrpInr = mrpInr;
        this.pricePerUnitInr = pricePerUnitInr;
        this.manufacturer = manufacturer;
        this.medicineType = medicineType;
        this.prescriptionRequired = prescriptionRequired;
        this.isDiscontinued = isDiscontinued;
        this.isJanAushadhi = isJanAushadhi;
        this.ratingExcellentPct = ratingExcellentPct;
        this.source = source;
        this.priceAsOf = priceAsOf;
        this.isSampleData = isSampleData;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String brandName;
        private String brandNameNormalized;
        private String saltComposition;
        private String saltNormalized;
        private String strength;
        private String dosageForm;
        private String releaseType;
        private String matchKey;
        private String therapeuticGroup;
        private String packSizeLabel;
        private Integer unitsPerPack;
        private Double mrpInr;
        private Double pricePerUnitInr;
        private String manufacturer;
        private String medicineType;
        private Boolean prescriptionRequired;
        private Boolean isDiscontinued;
        private Boolean isJanAushadhi;
        private Double ratingExcellentPct;
        private String source;
        private String priceAsOf;
        private Boolean isSampleData;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder brandName(String brandName) { this.brandName = brandName; return this; }
        public Builder brandNameNormalized(String brandNameNormalized) { this.brandNameNormalized = brandNameNormalized; return this; }
        public Builder saltComposition(String saltComposition) { this.saltComposition = saltComposition; return this; }
        public Builder saltNormalized(String saltNormalized) { this.saltNormalized = saltNormalized; return this; }
        public Builder strength(String strength) { this.strength = strength; return this; }
        public Builder dosageForm(String dosageForm) { this.dosageForm = dosageForm; return this; }
        public Builder releaseType(String releaseType) { this.releaseType = releaseType; return this; }
        public Builder matchKey(String matchKey) { this.matchKey = matchKey; return this; }
        public Builder therapeuticGroup(String therapeuticGroup) { this.therapeuticGroup = therapeuticGroup; return this; }
        public Builder packSizeLabel(String packSizeLabel) { this.packSizeLabel = packSizeLabel; return this; }
        public Builder unitsPerPack(Integer unitsPerPack) { this.unitsPerPack = unitsPerPack; return this; }
        public Builder mrpInr(Double mrpInr) { this.mrpInr = mrpInr; return this; }
        public Builder pricePerUnitInr(Double pricePerUnitInr) { this.pricePerUnitInr = pricePerUnitInr; return this; }
        public Builder manufacturer(String manufacturer) { this.manufacturer = manufacturer; return this; }
        public Builder medicineType(String medicineType) { this.medicineType = medicineType; return this; }
        public Builder prescriptionRequired(Boolean prescriptionRequired) { this.prescriptionRequired = prescriptionRequired; return this; }
        public Builder isDiscontinued(Boolean isDiscontinued) { this.isDiscontinued = isDiscontinued; return this; }
        public Builder isJanAushadhi(Boolean isJanAushadhi) { this.isJanAushadhi = isJanAushadhi; return this; }
        public Builder ratingExcellentPct(Double ratingExcellentPct) { this.ratingExcellentPct = ratingExcellentPct; return this; }
        public Builder source(String source) { this.source = source; return this; }
        public Builder priceAsOf(String priceAsOf) { this.priceAsOf = priceAsOf; return this; }
        public Builder isSampleData(Boolean isSampleData) { this.isSampleData = isSampleData; return this; }

        public Medicine build() {
            return new Medicine(id, brandName, brandNameNormalized, saltComposition, saltNormalized, strength,
                    dosageForm, releaseType, matchKey, therapeuticGroup, packSizeLabel, unitsPerPack, mrpInr,
                    pricePerUnitInr, manufacturer, medicineType, prescriptionRequired, isDiscontinued,
                    isJanAushadhi, ratingExcellentPct, source, priceAsOf, isSampleData);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getBrandNameNormalized() { return brandNameNormalized; }
    public void setBrandNameNormalized(String brandNameNormalized) { this.brandNameNormalized = brandNameNormalized; }

    public String getSaltComposition() { return saltComposition; }
    public void setSaltComposition(String saltComposition) { this.saltComposition = saltComposition; }

    public String getSaltNormalized() { return saltNormalized; }
    public void setSaltNormalized(String saltNormalized) { this.saltNormalized = saltNormalized; }

    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }

    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }

    public String getReleaseType() { return releaseType; }
    public void setReleaseType(String releaseType) { this.releaseType = releaseType; }

    public String getMatchKey() { return matchKey; }
    public void setMatchKey(String matchKey) { this.matchKey = matchKey; }

    public String getTherapeuticGroup() { return therapeuticGroup; }
    public void setTherapeuticGroup(String therapeuticGroup) { this.therapeuticGroup = therapeuticGroup; }

    public String getPackSizeLabel() { return packSizeLabel; }
    public void setPackSizeLabel(String packSizeLabel) { this.packSizeLabel = packSizeLabel; }

    public Integer getUnitsPerPack() { return unitsPerPack; }
    public void setUnitsPerPack(Integer unitsPerPack) { this.unitsPerPack = unitsPerPack; }

    public Double getMrpInr() { return mrpInr; }
    public void setMrpInr(Double mrpInr) { this.mrpInr = mrpInr; }

    public Double getPricePerUnitInr() { return pricePerUnitInr; }
    public void setPricePerUnitInr(Double pricePerUnitInr) { this.pricePerUnitInr = pricePerUnitInr; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getMedicineType() { return medicineType; }
    public void setMedicineType(String medicineType) { this.medicineType = medicineType; }

    public Boolean getPrescriptionRequired() { return prescriptionRequired; }
    public void setPrescriptionRequired(Boolean prescriptionRequired) { this.prescriptionRequired = prescriptionRequired; }

    public Boolean getIsDiscontinued() { return isDiscontinued; }
    public void setIsDiscontinued(Boolean isDiscontinued) { this.isDiscontinued = isDiscontinued; }

    public Boolean getIsJanAushadhi() { return isJanAushadhi; }
    public void setIsJanAushadhi(Boolean isJanAushadhi) { this.isJanAushadhi = isJanAushadhi; }

    public Double getRatingExcellentPct() { return ratingExcellentPct; }
    public void setRatingExcellentPct(Double ratingExcellentPct) { this.ratingExcellentPct = ratingExcellentPct; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getPriceAsOf() { return priceAsOf; }
    public void setPriceAsOf(String priceAsOf) { this.priceAsOf = priceAsOf; }

    public Boolean getIsSampleData() { return isSampleData; }
    public void setIsSampleData(Boolean isSampleData) { this.isSampleData = isSampleData; }
}
