package com.analyzerbackend.backend.Controller;

import com.analyzerbackend.backend.Dto.AnalysisResponseDto;
import com.analyzerbackend.backend.Dto.BillAnalysisResponseDto;
import com.analyzerbackend.backend.Dto.HealthResponseDto;
import com.analyzerbackend.backend.Dto.SearchMedicineDto;
import com.analyzerbackend.backend.Entity.Medicine;
import com.analyzerbackend.backend.Repository.MedicineRepository;
import com.analyzerbackend.backend.Service.AnalysisService;
import com.analyzerbackend.backend.Service.CsvDataLoaderService;
import com.analyzerbackend.backend.Service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Tag(name = "Medical Bill & Medicine Analysis", description = "Public endpoints for bill audit, OCR extraction, and medicine alternative search")
public class AnalysisController {

    private static final Logger log = LoggerFactory.getLogger(AnalysisController.class);

    private final AnalysisService analysisService;
    private final MedicineRepository medicineRepository;
    private final CsvDataLoaderService csvDataLoaderService;
    private final OcrService ocrService;

    public AnalysisController(AnalysisService analysisService,
                              MedicineRepository medicineRepository,
                              CsvDataLoaderService csvDataLoaderService,
                              OcrService ocrService) {
        this.analysisService = analysisService;
        this.medicineRepository = medicineRepository;
        this.csvDataLoaderService = csvDataLoaderService;
        this.ocrService = ocrService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Analyze a single medicine or prescription file",
               description = "Accepts optional medicineName (text) and optional prescription file (PDF, JPG, PNG). At least one is required.")
    public ResponseEntity<AnalysisResponseDto> analyze(
            @RequestParam(value = "medicineName", required = false) String medicineName,
            @RequestParam(value = "prescription", required = false) MultipartFile prescription
    ) {
        log.info("Received /api/analyze request (hasMedicineName={}, hasPrescription={})",
                medicineName != null && !medicineName.trim().isEmpty(),
                prescription != null && !prescription.isEmpty());

        AnalysisResponseDto response = analysisService.analyzeSingle(medicineName, prescription);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/audit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Audit an entire medical bill or prescription",
               description = "Accepts a bill file ('bill' or 'prescription') and produces an audit breakdown and generic alternatives.")
    public ResponseEntity<BillAnalysisResponseDto> audit(
            @RequestParam(value = "bill", required = false) MultipartFile bill,
            @RequestParam(value = "prescription", required = false) MultipartFile prescription,
            @RequestParam(value = "medicineName", required = false) String medicineName
    ) {
        MultipartFile effectiveFile = bill != null && !bill.isEmpty() ? bill : prescription;

        log.info("Received /api/audit request (hasFile={}, hasMedicineName={})",
                effectiveFile != null && !effectiveFile.isEmpty(),
                medicineName != null && !medicineName.trim().isEmpty());

        BillAnalysisResponseDto response = analysisService.auditBill(effectiveFile, medicineName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medicines/search")
    @Operation(summary = "Search medicines for autocomplete", description = "Finds medicines by prefix or name/salt query.")
    public ResponseEntity<List<SearchMedicineDto>> searchMedicines(
            @RequestParam(value = "q", defaultValue = "") String query
    ) {
        String trimmed = query.trim();
        List<Medicine> list;
        if (trimmed.isEmpty()) {
            list = medicineRepository.findAll().stream().limit(15).collect(Collectors.toList());
        } else {
            list = medicineRepository.searchMedicines(trimmed).stream().limit(15).collect(Collectors.toList());
        }

        List<SearchMedicineDto> dtos = list.stream().map(m -> SearchMedicineDto.builder()
                .id(m.getId())
                .brandName(m.getBrandName())
                .saltComposition(m.getSaltComposition())
                .strength(m.getStrength())
                .dosageForm(m.getDosageForm())
                .releaseType(m.getReleaseType())
                .manufacturer(m.getManufacturer())
                .pricePerUnitInr(m.getPricePerUnitInr())
                .mrpInr(m.getMrpInr())
                .isJanAushadhi(m.getIsJanAushadhi())
                .isDiscontinued(m.getIsDiscontinued())
                .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/medicines/seed")
    @Operation(summary = "Seed medicines from CSV file",
               description = "Loads medicines from the data CSV file. Set clean=true to clear existing data before seeding.")
    public ResponseEntity<Map<String, Object>> seedMedicines(
            @RequestParam(value = "clean", defaultValue = "false") boolean clean
    ) {
        log.info("Received manual request to seed medicines (clean={})", clean);
        int loaded = csvDataLoaderService.loadMedicines(clean);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("rowsLoaded", loaded);
        result.put("totalCount", csvDataLoaderService.getLoadedCount());
        result.put("dataSource", csvDataLoaderService.getDataSource());
        result.put("message", clean
                ? "Cleaned database and seeded " + loaded + " medicines."
                : "Seeded " + loaded + " medicines into database.");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/medicines/seed/status")
    @Operation(summary = "Get medicine seed status",
               description = "Reports current loaded medicine count, load status, and resolved CSV file path.")
    public ResponseEntity<Map<String, Object>> seedStatus() {
        return ResponseEntity.ok(csvDataLoaderService.getStatus());
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Reports status, loaded medicine count, and OCR availability.")
    public ResponseEntity<HealthResponseDto> health() {
        long count = 0;
        try {
            count = csvDataLoaderService.getLoadedCount();
        } catch (Exception e) {
            log.warn("Health check count query error: {}", e.getMessage());
        }

        boolean ocrReady = false;
        try {
            ocrReady = ocrService.isOcrAvailable();
        } catch (Exception e) {
            log.warn("Health check OCR query error: {}", e.getMessage());
        }

        return ResponseEntity.ok(HealthResponseDto.builder()
                .status("UP")
                .databaseLoaded(count > 0)
                .medicineCount(count)
                .ocrAvailable(ocrReady)
                .message("Medical Bill Analyser API is operational. Reference data count: " + count + ". OCR: " + ocrService.getOcrStatusMessage())
                .build());
    }
}
