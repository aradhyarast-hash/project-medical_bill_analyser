package com.analyzerbackend.backend.Utility;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidator {

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "jpg", "jpeg", "png"
    );

    public static void validateFile(MultipartFile file, long maxSizeBytes) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be empty");
        }

        if (file.getSize() > maxSizeBytes) {
            double mb = (double) maxSizeBytes / (1024 * 1024);
            throw new IllegalArgumentException(String.format("File size exceeds maximum allowed limit of %.1f MB", mb));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !hasAllowedExtension(filename)) {
            throw new IllegalArgumentException("Unsupported file type. Only PDF, JPG, and PNG files are accepted.");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty() && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            // Some systems pass image/pjpeg or octet-stream, so check extension as fallback
            if (!hasAllowedExtension(filename)) {
                throw new IllegalArgumentException("Unsupported content type: " + contentType + ". Only PDF, JPG, and PNG files are accepted.");
            }
        }
    }

    private static boolean hasAllowedExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return false;
        }
        String ext = filename.substring(dotIndex + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(ext);
    }
}
