package com.analyzerbackend.backend.Service.Impl;

import com.analyzerbackend.backend.Service.OcrService;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class OcrServiceImpl implements OcrService {

    private static final Logger log = LoggerFactory.getLogger(OcrServiceImpl.class);

    @Value("${app.ocr.tessdata-path:./tessdata}")
    private String tessdataPath;

    @Value("${app.pdf.max-pages:5}")
    private int maxPdfPages;

    private ITesseract tesseract;
    private Boolean ocrAvailable = null;
    private String ocrStatusMessage = "Initialized";

    private synchronized ITesseract getTesseract() {
        if (tesseract != null) {
            return tesseract;
        }

        try {
            Tesseract t = new Tesseract();
            File tessDir = new File(tessdataPath);
            if (tessDir.exists() && tessDir.isDirectory()) {
                t.setDatapath(tessDir.getAbsolutePath());
            } else {
                // Check common Windows Tesseract path
                File winTess = new File("C:/Program Files/Tesseract-OCR/tessdata");
                if (winTess.exists() && winTess.isDirectory()) {
                    t.setDatapath(winTess.getAbsolutePath());
                }
            }
            t.setLanguage("eng");
            this.tesseract = t;
            this.ocrAvailable = true;
            this.ocrStatusMessage = "OCR engine available";
            return t;
        } catch (Throwable t) {
            this.ocrAvailable = false;
            this.ocrStatusMessage = "OCR engine unavailable: " + t.getMessage();
            log.warn("Tesseract OCR initialization check: {}", ocrStatusMessage);
            return null;
        }
    }

    @Override
    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        StringBuilder fullText = new StringBuilder();

        if (filename.endsWith(".pdf") || "application/pdf".equalsIgnoreCase(file.getContentType())) {
            fullText.append(processPdf(file));
        } else {
            fullText.append(processImage(file));
        }

        String result = fullText.toString().trim();
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Could not extract any readable text from the uploaded document. Please upload a clearer bill image or PDF.");
        }

        return result;
    }

    private String processPdf(MultipartFile file) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] bytes = file.getBytes();
            try (PDDocument document = Loader.loadPDF(bytes)) {
                // 1. First attempt direct digital text extraction (fast and 100% accurate for digital PDFs)
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setEndPage(Math.min(document.getNumberOfPages(), maxPdfPages));
                String extracted = stripper.getText(document);
                if (extracted != null && extracted.trim().length() > 30) {
                    return extracted.trim();
                }

                // 2. If digital text is minimal or empty (scanned PDF), render pages to images and run OCR
                ITesseract engine = getTesseract();
                if (engine == null) {
                    throw new IllegalStateException("OCR engine is not available to process scanned PDF pages. " + ocrStatusMessage);
                }

                PDFRenderer renderer = new PDFRenderer(document);
                int totalPages = Math.min(document.getNumberOfPages(), maxPdfPages);
                for (int i = 0; i < totalPages; i++) {
                    BufferedImage pageImage = renderer.renderImageWithDPI(i, 300);
                    try {
                        String pageText = engine.doOCR(pageImage);
                        if (pageText != null) {
                            sb.append(pageText).append("\n");
                        }
                    } catch (TesseractException te) {
                        log.error("OCR failed for PDF page {}: {}", i + 1, te.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read PDF document: " + e.getMessage(), e);
        }

        return sb.toString().trim();
    }

    private String processImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new IllegalArgumentException("Uploaded image could not be decoded. Supported formats: JPG, PNG.");
            }

            ITesseract engine = getTesseract();
            if (engine == null) {
                throw new IllegalStateException("OCR engine is not available to process image. " + ocrStatusMessage);
            }

            try {
                return engine.doOCR(image);
            } catch (TesseractException | UnsatisfiedLinkError e) {
                this.ocrAvailable = false;
                this.ocrStatusMessage = e.getMessage();
                throw new IllegalStateException("OCR text extraction failed: " + e.getMessage(), e);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process image file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isOcrAvailable() {
        if (ocrAvailable == null) {
            getTesseract();
        }
        return Boolean.TRUE.equals(ocrAvailable);
    }

    @Override
    public String getOcrStatusMessage() {
        return ocrStatusMessage;
    }
}
