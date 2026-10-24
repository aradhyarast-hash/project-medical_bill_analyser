package com.analyzerbackend.backend.Dto;

public class SearchMedicineDto {
    private Long id;
    private String brandName;
    private String saltComposition;
    private String strength;
    private String dosageForm;
    private String releaseType;
    private String manufacturer;
    private Double pricePerUnitInr;
    private Double mrpInr;
    private Boolean isJanAushadhi;
    private Boolean isDiscontinued;

    public SearchMedicineDto() {
    }

    public SearchMedicineDto(Long id, String brandName, String saltComposition, String strength,
                             String dosageForm, String releaseType, String manufacturer,
                             Double pricePerUnitInr, Double mrpInr, Boolean isJanAushadhi, Boolean isDiscontinued) {
        this.id = id;
        this.brandName = brandName;
        this.saltComposition = saltComposition;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.releaseType = releaseType;
        this.manufacturer = manufacturer;
        this.pricePerUnitInr = pricePerUnitInr;
        this.mrpInr = mrpInr;
        this.isJanAushadhi = isJanAushadhi;
        this.isDiscontinued = isDiscontinued;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String brandName;
        private String saltComposition;
        private String strength;
        private String dosageForm;
        private String releaseType;
        private String manufacturer;
        private Double pricePerUnitInr;
        private Double mrpInr;
        private Boolean isJanAushadhi;
        private Boolean isDiscontinued;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder brandName(String brandName) { this.brandName = brandName; return this; }
        public Builder saltComposition(String saltComposition) { this.saltComposition = saltComposition; return this; }
        public Builder strength(String strength) { this.strength = strength; return this; }
        public Builder dosageForm(String dosageForm) { this.dosageForm = dosageForm; return this; }
        public Builder releaseType(String releaseType) { this.releaseType = releaseType; return this; }
        public Builder manufacturer(String manufacturer) { this.manufacturer = manufacturer; return this; }
        public Builder pricePerUnitInr(Double pricePerUnitInr) { this.pricePerUnitInr = pricePerUnitInr; return this; }
        public Builder mrpInr(Double mrpInr) { this.mrpInr = mrpInr; return this; }
        public Builder isJanAushadhi(Boolean isJanAushadhi) { this.isJanAushadhi = isJanAushadhi; return this; }
        public Builder isDiscontinued(Boolean isDiscontinued) { this.isDiscontinued = isDiscontinued; return this; }

        public SearchMedicineDto build() {
            return new SearchMedicineDto(id, brandName, saltComposition, strength, dosageForm,
                    releaseType, manufacturer, pricePerUnitInr, mrpInr, isJanAushadhi, isDiscontinued);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getSaltComposition() { return saltComposition; }
    public void setSaltComposition(String saltComposition) { this.saltComposition = saltComposition; }

    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }

    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }

    public String getReleaseType() { return releaseType; }
    public void setReleaseType(String releaseType) { this.releaseType = releaseType; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public Double getPricePerUnitInr() { return pricePerUnitInr; }
    public void setPricePerUnitInr(Double pricePerUnitInr) { this.pricePerUnitInr = pricePerUnitInr; }

    public Double getMrpInr() { return mrpInr; }
    public void setMrpInr(Double mrpInr) { this.mrpInr = mrpInr; }

    public Boolean getIsJanAushadhi() { return isJanAushadhi; }
    public void setIsJanAushadhi(Boolean isJanAushadhi) { this.isJanAushadhi = isJanAushadhi; }

    public Boolean getIsDiscontinued() { return isDiscontinued; }
    public void setIsDiscontinued(Boolean isDiscontinued) { this.isDiscontinued = isDiscontinued; }
}
