package com.analyzerbackend.backend.Service;

import java.util.Map;

public interface CsvDataLoaderService {
    void loadMedicines();
    int loadMedicines(boolean cleanFirst);
    long getLoadedCount();
    String getDataSource();
    Map<String, Object> getStatus();
}
