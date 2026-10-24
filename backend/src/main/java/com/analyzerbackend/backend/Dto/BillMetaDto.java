package com.analyzerbackend.backend.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BillMetaDto {
    @JsonProperty("pharmacy_name")
    private String pharmacyName;

    @JsonProperty("bill_date")
    private String billDate;

    public BillMetaDto() {
    }

    public BillMetaDto(String pharmacyName, String billDate) {
        this.pharmacyName = pharmacyName;
        this.billDate = billDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String pharmacyName;
        private String billDate;

        public Builder pharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; return this; }
        public Builder billDate(String billDate) { this.billDate = billDate; return this; }

        public BillMetaDto build() {
            return new BillMetaDto(pharmacyName, billDate);
        }
    }

    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }

    public String getBillDate() { return billDate; }
    public void setBillDate(String billDate) { this.billDate = billDate; }
}
