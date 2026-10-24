package com.analyzerbackend.backend.Dto;

import java.util.ArrayList;
import java.util.List;

public class AnalysisResponseDto {
    private InputMedicineDto inputMedicine;
    private Boolean prescribedFound;
    private List<MedicineAlternativeDto> results = new ArrayList<>();
    private List<String> notes = new ArrayList<>();
    private String disclaimer;
    private String dataSource;

    public AnalysisResponseDto() {
    }

    public AnalysisResponseDto(InputMedicineDto inputMedicine, Boolean prescribedFound,
                               List<MedicineAlternativeDto> results, List<String> notes,
                               String disclaimer, String dataSource) {
        this.inputMedicine = inputMedicine;
        this.prescribedFound = prescribedFound;
        this.results = results != null ? results : new ArrayList<>();
        this.notes = notes != null ? notes : new ArrayList<>();
        this.disclaimer = disclaimer;
        this.dataSource = dataSource;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InputMedicineDto inputMedicine;
        private Boolean prescribedFound;
        private List<MedicineAlternativeDto> results = new ArrayList<>();
        private List<String> notes = new ArrayList<>();
        private String disclaimer;
        private String dataSource;

        public Builder inputMedicine(InputMedicineDto inputMedicine) { this.inputMedicine = inputMedicine; return this; }
        public Builder prescribedFound(Boolean prescribedFound) { this.prescribedFound = prescribedFound; return this; }
        public Builder results(List<MedicineAlternativeDto> results) { this.results = results != null ? results : new ArrayList<>(); return this; }
        public Builder notes(List<String> notes) { this.notes = notes != null ? notes : new ArrayList<>(); return this; }
        public Builder disclaimer(String disclaimer) { this.disclaimer = disclaimer; return this; }
        public Builder dataSource(String dataSource) { this.dataSource = dataSource; return this; }

        public AnalysisResponseDto build() {
            return new AnalysisResponseDto(inputMedicine, prescribedFound, results, notes, disclaimer, dataSource);
        }
    }

    public InputMedicineDto getInputMedicine() { return inputMedicine; }
    public void setInputMedicine(InputMedicineDto inputMedicine) { this.inputMedicine = inputMedicine; }

    public Boolean getPrescribedFound() { return prescribedFound; }
    public void setPrescribedFound(Boolean prescribedFound) { this.prescribedFound = prescribedFound; }

    public List<MedicineAlternativeDto> getResults() { return results; }
    public void setResults(List<MedicineAlternativeDto> results) { this.results = results; }

    public List<String> getNotes() { return notes; }
    public void setNotes(List<String> notes) { this.notes = notes; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
}
