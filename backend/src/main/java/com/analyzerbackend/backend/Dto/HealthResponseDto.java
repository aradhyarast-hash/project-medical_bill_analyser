package com.analyzerbackend.backend.Dto;

public class HealthResponseDto {
    private String status;
    private Boolean databaseLoaded;
    private Long medicineCount;
    private Boolean ocrAvailable;
    private String message;

    public HealthResponseDto() {
    }

    public HealthResponseDto(String status, Boolean databaseLoaded, Long medicineCount, Boolean ocrAvailable, String message) {
        this.status = status;
        this.databaseLoaded = databaseLoaded;
        this.medicineCount = medicineCount;
        this.ocrAvailable = ocrAvailable;
        this.message = message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String status;
        private Boolean databaseLoaded;
        private Long medicineCount;
        private Boolean ocrAvailable;
        private String message;

        public Builder status(String status) { this.status = status; return this; }
        public Builder databaseLoaded(Boolean databaseLoaded) { this.databaseLoaded = databaseLoaded; return this; }
        public Builder medicineCount(Long medicineCount) { this.medicineCount = medicineCount; return this; }
        public Builder ocrAvailable(Boolean ocrAvailable) { this.ocrAvailable = ocrAvailable; return this; }
        public Builder message(String message) { this.message = message; return this; }

        public HealthResponseDto build() {
            return new HealthResponseDto(status, databaseLoaded, medicineCount, ocrAvailable, message);
        }
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getDatabaseLoaded() { return databaseLoaded; }
    public void setDatabaseLoaded(Boolean databaseLoaded) { this.databaseLoaded = databaseLoaded; }

    public Long getMedicineCount() { return medicineCount; }
    public void setMedicineCount(Long medicineCount) { this.medicineCount = medicineCount; }

    public Boolean getOcrAvailable() { return ocrAvailable; }
    public void setOcrAvailable(Boolean ocrAvailable) { this.ocrAvailable = ocrAvailable; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
