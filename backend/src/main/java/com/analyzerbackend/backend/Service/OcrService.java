package com.analyzerbackend.backend.Service;

import org.springframework.web.multipart.MultipartFile;

public interface OcrService {
    String extractText(MultipartFile file);
    boolean isOcrAvailable();
    String getOcrStatusMessage();
}
