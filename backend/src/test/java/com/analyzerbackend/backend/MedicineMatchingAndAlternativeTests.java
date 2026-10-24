package com.analyzerbackend.backend;

import com.analyzerbackend.backend.Dto.MedicineAlternativeDto;
import com.analyzerbackend.backend.Entity.Medicine;
import com.analyzerbackend.backend.Repository.MedicineRepository;
import com.analyzerbackend.backend.Service.AlternativeService;
import com.analyzerbackend.backend.Service.MedicineMatchingService;
import com.analyzerbackend.backend.Utility.MedicineNormalizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MedicineMatchingAndAlternativeTests {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private MedicineMatchingService matchingService;

    @Autowired
    private AlternativeService alternativeService;

    @Test
    @DisplayName("Verify CSV data is loaded into database with correct columns")
    void testCsvDataLoaded() {
        long count = medicineRepository.count();
        assertTrue(count >= 31, "Should have loaded at least 31 medicines from CSV");

        Optional<Medicine> dolo = medicineRepository.findFirstByBrandNameIgnoreCase("Dolo 650");
        assertTrue(dolo.isPresent(), "Dolo 650 should be present");
        assertEquals("paracetamol", dolo.get().getSaltNormalized());
        assertEquals("650mg", dolo.get().getStrength());
        assertEquals("Tablet", dolo.get().getDosageForm());
        assertEquals(2.20, dolo.get().getPricePerUnitInr());
        assertEquals("Micro Labs", dolo.get().getManufacturer());
    }

    @Test
    @DisplayName("Verify Name Normalization and Levenshtein similarity")
    void testNameNormalizationAndFuzzyMatching() {
        assertEquals("dolo 650", MedicineNormalizer.normalizeName("DOLO 650!!"));
        assertEquals("crocin advance 500mg tab", MedicineNormalizer.normalizeName("Crocin Advance (500mg) Tab"));

        // Exact match
        MedicineMatchingService.MatchResult exact = matchingService.matchByName("Dolo 650");
        assertTrue(exact.getMedicine().isPresent());
        assertEquals("Dolo 650", exact.getMedicine().get().getBrandName());
        assertFalse(exact.isNeedsReview());

        // Fuzzy match: OCR typo "D0LO 650"
        MedicineMatchingService.MatchResult fuzzy = matchingService.matchByName("D0LO 650");
        assertTrue(fuzzy.getMedicine().isPresent());
        assertEquals("Dolo 650", fuzzy.getMedicine().get().getBrandName());

        // Low confidence match
        MedicineMatchingService.MatchResult notFound = matchingService.matchByName("CompletelyUnknownDrug999");
        assertTrue(notFound.getMedicine().isEmpty());
        assertTrue(notFound.isNeedsReview());
        assertTrue(notFound.getConfidence() < 0.75);
    }

    @Test
    @DisplayName("Verify Alternatives: same salt, same strength, same form, max 5, tagged correctly")
    void testAlternativesForDolo650() {
        Medicine dolo = medicineRepository.findFirstByBrandNameIgnoreCase("Dolo 650").orElseThrow();

        List<MedicineAlternativeDto> alts = alternativeService.findAlternatives(
                dolo,
                dolo.getSaltNormalized(),
                dolo.getStrength(),
                dolo.getDosageForm(),
                dolo.getReleaseType(),
                dolo.getPricePerUnitInr()
        );

        assertNotNull(alts);
        assertFalse(alts.isEmpty(), "Should return alternatives for Paracetamol 650mg");
        assertTrue(alts.size() <= 5, "Must return at most 5 results");

        // Every alternative must have same salt and strength and form
        for (MedicineAlternativeDto alt : alts) {
            assertTrue(alt.getSalt().toLowerCase().contains("paracetamol"), "Must match paracetamol salt");
            assertEquals("650mg", alt.getStrength(), "Strength must be 650mg");
            assertEquals("Tablet", alt.getDosageForm(), "Dosage form must be Tablet");
            assertTrue(alt.getTags().contains("SAME_SALT"), "Must have SAME_SALT tag");
        }

        // At least one must be tagged CHEAPEST
        boolean hasCheapest = alts.stream().anyMatch(a -> a.getTags().contains("CHEAPEST"));
        assertTrue(hasCheapest, "Should tag cheapest option");

        // Jan Aushadhi generic alternative should be tagged
        boolean hasJanAushadhi = alts.stream().anyMatch(a -> a.getTags().contains("JAN_AUSHADHI"));
        assertTrue(hasJanAushadhi, "Should include Jan Aushadhi generic alternative");

        // Prescribed medicine should be first
        assertEquals("Dolo 650", alts.get(0).getName());
    }

    @Test
    @DisplayName("Verify Discontinued medicine is tagged ALTERNATE and explains why")
    void testDiscontinuedMedicineFlow() {
        // Demo-Pcm 650 is discontinued in the CSV
        Medicine demoPcm = medicineRepository.findFirstByBrandNameIgnoreCase("Demo-Pcm 650").orElseThrow();
        assertTrue(Boolean.TRUE.equals(demoPcm.getIsDiscontinued()));

        List<MedicineAlternativeDto> alts = alternativeService.findAlternatives(
                demoPcm,
                demoPcm.getSaltNormalized(),
                demoPcm.getStrength(),
                demoPcm.getDosageForm(),
                demoPcm.getReleaseType(),
                demoPcm.getPricePerUnitInr()
        );

        assertFalse(alts.isEmpty());
        // Alternatives should carry ALTERNATE tag
        boolean hasAlternateTag = alts.stream()
                .filter(a -> !a.getName().equalsIgnoreCase("Demo-Pcm 650"))
                .anyMatch(a -> a.getTags().contains("ALTERNATE"));
        assertTrue(hasAlternateTag, "Alternatives should carry ALTERNATE tag for discontinued prescribed medicine");
    }

    @Test
    @DisplayName("Verify Controlled Release (SR) forms are strictly differentiated from plain tablets")
    void testReleaseTypeDistinction() {
        Medicine glycometSR = medicineRepository.findFirstByBrandNameIgnoreCase("Glycomet SR 500").orElseThrow();
        assertEquals("prolonged-release", glycometSR.getReleaseType());

        List<MedicineAlternativeDto> srAlts = alternativeService.findAlternatives(
                glycometSR,
                glycometSR.getSaltNormalized(),
                glycometSR.getStrength(),
                glycometSR.getDosageForm(),
                glycometSR.getReleaseType(),
                glycometSR.getPricePerUnitInr()
        );

        assertFalse(srAlts.isEmpty());
        // Plain Glycomet 500 (immediate release) must NOT be mixed with Glycomet SR 500
        for (MedicineAlternativeDto alt : srAlts) {
            assertEquals("prolonged-release", alt.getReleaseType(), "Only prolonged-release should match Glycomet SR 500");
            assertNotEquals("Glycomet 500", alt.getName(), "Plain Glycomet 500 must not be returned for SR formulation");
        }
    }
}
