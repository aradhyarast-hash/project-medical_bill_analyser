package com.analyzerbackend.backend.Service.Impl;

import com.analyzerbackend.backend.Entity.Medicine;
import com.analyzerbackend.backend.Repository.MedicineRepository;
import com.analyzerbackend.backend.Service.CsvDataLoaderService;
import com.analyzerbackend.backend.Utility.MedicineNormalizer;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvDataLoaderServiceImpl implements CsvDataLoaderService {

    private static final Logger log = LoggerFactory.getLogger(CsvDataLoaderServiceImpl.class);

    private final MedicineRepository medicineRepository;

    public CsvDataLoaderServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Value("${app.data.csv-path:data/medicines.csv}")
    private String csvFilePath;

    private String resolvedDataSource = "data/medicines.csv";

    @PostConstruct
    public void init() {
        // Load the reference CSV only once – skip if data already present
        if (medicineRepository.count() == 0) {
            loadMedicines();
        } else {
            log.info("Reference medicines already loaded ({} rows). Skipping CSV import.", medicineRepository.count());
        }
    }

    @Override
    public synchronized void loadMedicines() {
        if (medicineRepository.count() > 0) {
            log.info("Medicines already loaded in database (count: {}). Skipping reload.", medicineRepository.count());
            return;
        }

        Reader reader = getReader();
        if (reader == null) {
            log.warn("Medicine CSV file not found at {} or in classpath. Starting with empty reference data.", csvFilePath);
            return;
        }

        try (CSVReader csvReader = new CSVReader(reader)) {
            String[] header = csvReader.readNext();
            if (header == null) {
                log.warn("Medicine CSV file is empty.");
                return;
            }

            List<Medicine> batch = new ArrayList<>();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line.length == 0 || (line.length == 1 && line[0].trim().isEmpty())) {
                    continue;
                }

                try {
                    Medicine m = parseMedicineLine(line);
                    if (m != null) {
                        batch.add(m);
                    }
                } catch (Exception ex) {
                    log.error("Failed to parse CSV line: {}", String.join(",", line), ex);
                }
            }

            if (!batch.isEmpty()) {
                medicineRepository.saveAll(batch);
                log.info("Successfully loaded {} reference medicines into database.", batch.size());
            }
        } catch (IOException | CsvValidationException e) {
            log.error("Error reading medicine CSV file: {}", e.getMessage(), e);
        }
    }

    private Reader getReader() {
        // Try file path first
        File file = new File(csvFilePath);
        if (file.exists() && file.isFile()) {
            try {
                resolvedDataSource = file.getAbsolutePath();
                return new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            } catch (FileNotFoundException e) {
                log.warn("Could not open file: {}", csvFilePath);
            }
        }

        // Try classpath fallback
        try {
            ClassPathResource resource = new ClassPathResource(csvFilePath);
            if (resource.exists()) {
                resolvedDataSource = "classpath:" + csvFilePath;
                return new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }

        // Try default classpath location
        try {
            ClassPathResource fallback = new ClassPathResource("data/medicines.csv");
            if (fallback.exists()) {
                resolvedDataSource = "classpath:data/medicines.csv";
                return new InputStreamReader(fallback.getInputStream(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }

        return null;
    }

    private Medicine parseMedicineLine(String[] line) {
        if (line.length < 5) return null;

        // Header mapping:
        // 0: id
        // 1: brand_name
        // 2: salt_composition
        // 3: salt_normalized
        // 4: strength
        // 5: dosage_form
        // 6: release_type
        // 7: match_key
        // 8: therapeutic_group
        // 9: pack_size_label
        // 10: units_per_pack
        // 11: mrp_inr
        // 12: price_per_unit_inr
        // 13: manufacturer
        // 14: medicine_type
        // 15: prescription_required
        // 16: is_discontinued
        // 17: is_jan_aushadhi
        // 18: rating_excellent_pct
        // 19: source
        // 20: price_as_of
        // 21: is_sample_data

        String brandName = getSafeString(line, 1);
        if (brandName.isEmpty()) return null;

        String saltComp = getSafeString(line, 2);
        String saltNorm = getSafeString(line, 3);
        if (saltNorm.isEmpty() && !saltComp.isEmpty()) {
            saltNorm = MedicineNormalizer.normalizeName(saltComp);
        }

        String strength = getSafeString(line, 4);
        String dosageForm = getSafeString(line, 5);
        if (dosageForm.isEmpty()) dosageForm = "Tablet";

        String releaseType = getSafeString(line, 6);
        if (releaseType.isEmpty()) releaseType = "immediate";

        String matchKey = getSafeString(line, 7);
        if (matchKey.isEmpty()) {
            matchKey = String.format("%s|%s|%s|%s", saltNorm, strength, dosageForm.toLowerCase(), releaseType.toLowerCase());
        }

        return Medicine.builder()
                .brandName(brandName)
                .brandNameNormalized(MedicineNormalizer.normalizeName(brandName))
                .saltComposition(saltComp)
                .saltNormalized(saltNorm.toLowerCase().trim())
                .strength(strength.toLowerCase().trim())
                .dosageForm(dosageForm.trim())
                .releaseType(releaseType.toLowerCase().trim())
                .matchKey(matchKey.toLowerCase().trim())
                .therapeuticGroup(getSafeString(line, 8))
                .packSizeLabel(getSafeString(line, 9))
                .unitsPerPack(parseInteger(getSafeString(line, 10)))
                .mrpInr(parseDouble(getSafeString(line, 11)))
                .pricePerUnitInr(parseDouble(getSafeString(line, 12)))
                .manufacturer(getSafeString(line, 13))
                .medicineType(getSafeString(line, 14))
                .prescriptionRequired(parseBoolean(getSafeString(line, 15)))
                .isDiscontinued(parseBoolean(getSafeString(line, 16)))
                .isJanAushadhi(parseBoolean(getSafeString(line, 17)))
                .ratingExcellentPct(parseDouble(getSafeString(line, 18)))
                .source(getSafeString(line, 19))
                .priceAsOf(getSafeString(line, 20))
                .isSampleData(parseBoolean(getSafeString(line, 21)))
                .build();
    }

    private String getSafeString(String[] line, int index) {
        if (index < line.length && line[index] != null) {
            return line[index].trim();
        }
        return "";
    }

    private Double parseDouble(String val) {
        if (val == null || val.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String val) {
        if (val == null || val.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseBoolean(String val) {
        if (val == null || val.trim().isEmpty()) return false;
        return Boolean.parseBoolean(val.trim()) || "1".equals(val.trim()) || "TRUE".equalsIgnoreCase(val.trim());
    }

    @Override
    public long getLoadedCount() {
        return medicineRepository.count();
    }

    @Override
    public String getDataSource() {
        return resolvedDataSource;
    }
}
