package com.analyzerbackend.backend.Service;

public interface CsvDataLoaderService {
    void loadMedicines();
    long getLoadedCount();
    String getDataSource();
}
