package com.analyzerbackend.backend.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class BillItemDto {
    private String id;

    @JsonProperty("extracted_name")
    private String extractedName;

    @JsonProperty("matched_name")
    private String matchedName;

    private String strength;
    private String form;
    private Integer quantity;

    @JsonProperty("billed_unit_price")
    private Double billedUnitPrice;

    @JsonProperty("mrp_unit_price")
    private Double mrpUnitPrice;

    @JsonProperty("ceiling_unit_price")
    private Double ceilingUnitPrice;

    @JsonProperty("is_scheduled")
    private Boolean isScheduled;

    private String status;

    @JsonProperty("overcharge_amount")
    private Double overchargeAmount;

    private Double confidence;
    private GenericDto generic;
    private List<MedicineAlternativeDto> alternatives = new ArrayList<>();

    public BillItemDto() {
    }

    public BillItemDto(String id, String extractedName, String matchedName, String strength,
                       String form, Integer quantity, Double billedUnitPrice, Double mrpUnitPrice,
                       Double ceilingUnitPrice, Boolean isScheduled, String status,
                       Double overchargeAmount, Double confidence, GenericDto generic,
                       List<MedicineAlternativeDto> alternatives) {
        this.id = id;
        this.extractedName = extractedName;
        this.matchedName = matchedName;
        this.strength = strength;
        this.form = form;
        this.quantity = quantity;
        this.billedUnitPrice = billedUnitPrice;
        this.mrpUnitPrice = mrpUnitPrice;
        this.ceilingUnitPrice = ceilingUnitPrice;
        this.isScheduled = isScheduled;
        this.status = status;
        this.overchargeAmount = overchargeAmount;
        this.confidence = confidence;
        this.generic = generic;
        this.alternatives = alternatives != null ? alternatives : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String extractedName;
        private String matchedName;
        private String strength;
        private String form;
        private Integer quantity;
        private Double billedUnitPrice;
        private Double mrpUnitPrice;
        private Double ceilingUnitPrice;
        private Boolean isScheduled;
        private String status;
        private Double overchargeAmount;
        private Double confidence;
        private GenericDto generic;
        private List<MedicineAlternativeDto> alternatives = new ArrayList<>();

        public Builder id(String id) { this.id = id; return this; }
        public Builder extractedName(String extractedName) { this.extractedName = extractedName; return this; }
        public Builder matchedName(String matchedName) { this.matchedName = matchedName; return this; }
        public Builder strength(String strength) { this.strength = strength; return this; }
        public Builder form(String form) { this.form = form; return this; }
        public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public Builder billedUnitPrice(Double billedUnitPrice) { this.billedUnitPrice = billedUnitPrice; return this; }
        public Builder mrpUnitPrice(Double mrpUnitPrice) { this.mrpUnitPrice = mrpUnitPrice; return this; }
        public Builder ceilingUnitPrice(Double ceilingUnitPrice) { this.ceilingUnitPrice = ceilingUnitPrice; return this; }
        public Builder isScheduled(Boolean isScheduled) { this.isScheduled = isScheduled; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder overchargeAmount(Double overchargeAmount) { this.overchargeAmount = overchargeAmount; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
        public Builder generic(GenericDto generic) { this.generic = generic; return this; }
        public Builder alternatives(List<MedicineAlternativeDto> alternatives) { this.alternatives = alternatives != null ? alternatives : new ArrayList<>(); return this; }

        public BillItemDto build() {
            return new BillItemDto(id, extractedName, matchedName, strength, form, quantity,
                    billedUnitPrice, mrpUnitPrice, ceilingUnitPrice, isScheduled, status,
                    overchargeAmount, confidence, generic, alternatives);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getExtractedName() { return extractedName; }
    public void setExtractedName(String extractedName) { this.extractedName = extractedName; }

    public String getMatchedName() { return matchedName; }
    public void setMatchedName(String matchedName) { this.matchedName = matchedName; }

    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }

    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getBilledUnitPrice() { return billedUnitPrice; }
    public void setBilledUnitPrice(Double billedUnitPrice) { this.billedUnitPrice = billedUnitPrice; }

    public Double getMrpUnitPrice() { return mrpUnitPrice; }
    public void setMrpUnitPrice(Double mrpUnitPrice) { this.mrpUnitPrice = mrpUnitPrice; }

    public Double getCeilingUnitPrice() { return ceilingUnitPrice; }
    public void setCeilingUnitPrice(Double ceilingUnitPrice) { this.ceilingUnitPrice = ceilingUnitPrice; }

    public Boolean getIsScheduled() { return isScheduled; }
    public void setIsScheduled(Boolean isScheduled) { this.isScheduled = isScheduled; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getOverchargeAmount() { return overchargeAmount; }
    public void setOverchargeAmount(Double overchargeAmount) { this.overchargeAmount = overchargeAmount; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public GenericDto getGeneric() { return generic; }
    public void setGeneric(GenericDto generic) { this.generic = generic; }

    public List<MedicineAlternativeDto> getAlternatives() { return alternatives; }
    public void setAlternatives(List<MedicineAlternativeDto> alternatives) { this.alternatives = alternatives; }
}
