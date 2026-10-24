package com.analyzerbackend.backend.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class BillAnalysisResponseDto {
    private List<BillItemDto> items = new ArrayList<>();

    @JsonProperty("bill_meta")
    private BillMetaDto billMeta;

    private List<AnalysisResponseDto> medicineGroups = new ArrayList<>();
    private List<String> notes = new ArrayList<>();
    private String disclaimer;
    private String dataSource;

    public BillAnalysisResponseDto() {
    }

    public BillAnalysisResponseDto(List<BillItemDto> items, BillMetaDto billMeta,
                                   List<AnalysisResponseDto> medicineGroups,
                                   List<String> notes, String disclaimer, String dataSource) {
        this.items = items != null ? items : new ArrayList<>();
        this.billMeta = billMeta;
        this.medicineGroups = medicineGroups != null ? medicineGroups : new ArrayList<>();
        this.notes = notes != null ? notes : new ArrayList<>();
        this.disclaimer = disclaimer;
        this.dataSource = dataSource;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<BillItemDto> items = new ArrayList<>();
        private BillMetaDto billMeta;
        private List<AnalysisResponseDto> medicineGroups = new ArrayList<>();
        private List<String> notes = new ArrayList<>();
        private String disclaimer;
        private String dataSource;

        public Builder items(List<BillItemDto> items) { this.items = items != null ? items : new ArrayList<>(); return this; }
        public Builder billMeta(BillMetaDto billMeta) { this.billMeta = billMeta; return this; }
        public Builder medicineGroups(List<AnalysisResponseDto> medicineGroups) { this.medicineGroups = medicineGroups != null ? medicineGroups : new ArrayList<>(); return this; }
        public Builder notes(List<String> notes) { this.notes = notes != null ? notes : new ArrayList<>(); return this; }
        public Builder disclaimer(String disclaimer) { this.disclaimer = disclaimer; return this; }
        public Builder dataSource(String dataSource) { this.dataSource = dataSource; return this; }

        public BillAnalysisResponseDto build() {
            return new BillAnalysisResponseDto(items, billMeta, medicineGroups, notes, disclaimer, dataSource);
        }
    }

    public List<BillItemDto> getItems() { return items; }
    public void setItems(List<BillItemDto> items) { this.items = items; }

    public BillMetaDto getBillMeta() { return billMeta; }
    public void setBillMeta(BillMetaDto billMeta) { this.billMeta = billMeta; }

    public List<AnalysisResponseDto> getMedicineGroups() { return medicineGroups; }
    public void setMedicineGroups(List<AnalysisResponseDto> medicineGroups) { this.medicineGroups = medicineGroups; }

    public List<String> getNotes() { return notes; }
    public void setNotes(List<String> notes) { this.notes = notes; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
}
