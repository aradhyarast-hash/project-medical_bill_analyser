package com.analyzerbackend.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AnalysisControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Health endpoint returns UP and medicine count")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.databaseLoaded").value(true))
                .andExpect(jsonPath("$.medicineCount", greaterThanOrEqualTo(31)));
    }

    @Test
    @DisplayName("Search/autocomplete endpoint returns matching medicines")
    void testMedicineAutocomplete() throws Exception {
        mockMvc.perform(get("/api/medicines/search?q=dolo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].brandName", containsStringIgnoringCase("Dolo")));
    }

    @Test
    @DisplayName("Text-only input returns matched medicine and alternatives")
    void testTextOnlyInput() throws Exception {
        mockMvc.perform(multipart("/api/analyze")
                        .param("medicineName", "Dolo 650"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prescribedFound").value(true))
                .andExpect(jsonPath("$.inputMedicine.matchedName").value("Dolo 650"))
                .andExpect(jsonPath("$.results", not(empty())))
                .andExpect(jsonPath("$.disclaimer", notNullValue()));
    }

    @Test
    @DisplayName("Medicine not found flow returns alternatives with ALTERNATE tag or clear note")
    void testMedicineNotFound() throws Exception {
        mockMvc.perform(multipart("/api/analyze")
                        .param("medicineName", "NonExistentDrug12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prescribedFound").value(false))
                .andExpect(jsonPath("$.notes", not(empty())));
    }

    @Test
    @DisplayName("Empty input returns 400 Bad Request")
    void testEmptyInput() throws Exception {
        mockMvc.perform(multipart("/api/analyze"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("Please provide either a medicine name or a prescription file")));
    }

    @Test
    @DisplayName("Unsupported file type returns 400 Bad Request")
    void testUnsupportedFileType() throws Exception {
        MockMultipartFile unsupportedFile = new MockMultipartFile(
                "prescription",
                "test.txt",
                "text/plain",
                "Plain text document".getBytes()
        );

        mockMvc.perform(multipart("/api/analyze")
                        .file(unsupportedFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("Unsupported file type")));
    }

    @Test
    @DisplayName("Oversized file returns 400 error")
    void testOversizedFile() throws Exception {
        byte[] largeBytes = new byte[6 * 1024 * 1024]; // 6 MB (limit is 5 MB)
        MockMultipartFile largeFile = new MockMultipartFile(
                "prescription",
                "large.png",
                "image/png",
                largeBytes
        );

        mockMvc.perform(multipart("/api/analyze")
                        .file(largeFile))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Bill audit endpoint processes request and returns items and generic alternatives")
    void testAuditEndpointWithMedicine() throws Exception {
        mockMvc.perform(multipart("/api/audit")
                        .param("medicineName", "Calpol 500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", not(empty())))
                .andExpect(jsonPath("$.items[0].matched_name").value("Calpol 500"))
                .andExpect(jsonPath("$.items[0].generic", notNullValue()))
                .andExpect(jsonPath("$.items[0].alternatives", not(empty())))
                .andExpect(jsonPath("$.disclaimer", notNullValue()));
    }
}
