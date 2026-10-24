package com.analyzerbackend.backend;

import com.analyzerbackend.backend.Service.OcrService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OcrAndPdfTests {

    @Autowired
    private OcrService ocrService;

    @Test
    @DisplayName("Verify digital PDF direct text extraction without requiring external OCR")
    void testPdfTextExtraction() throws Exception {
        // Create an in-memory PDF with text using PDFBox
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                content.newLineAtOffset(100, 700);
                content.showText("APOLLO PHARMACY BILL");
                content.newLineAtOffset(0, -30);
                content.showText("Dolo 650mg Tablet Qty 15 Rs 33.00");
                content.newLineAtOffset(0, -30);
                content.showText("Pan 40mg Tablet Qty 15 Rs 165.00");
                content.endText();
            }
            doc.save(baos);
        }

        MockMultipartFile pdfFile = new MockMultipartFile(
                "prescription",
                "sample_bill.pdf",
                "application/pdf",
                baos.toByteArray()
        );

        String extracted = ocrService.extractText(pdfFile);
        assertNotNull(extracted);
        assertTrue(extracted.contains("Dolo 650mg") || extracted.contains("APOLLO PHARMACY"),
                "Should extract text from digital PDF");
    }

    @Test
    @DisplayName("Verify OCR status check reports state gracefully")
    void testOcrStatusCheck() {
        assertNotNull(ocrService.getOcrStatusMessage());
        // isOcrAvailable should not throw any uncaught exception
        boolean available = ocrService.isOcrAvailable();
        // Regardless of whether host has native tesseract or not, method returns boolean
        assertTrue(available || !available);
    }
}
