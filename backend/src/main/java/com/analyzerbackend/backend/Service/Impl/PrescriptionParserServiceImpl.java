package com.analyzerbackend.backend.Service.Impl;

import com.analyzerbackend.backend.Dto.InputMedicineDto;
import com.analyzerbackend.backend.Service.PrescriptionParserService;
import com.analyzerbackend.backend.Utility.MedicineNormalizer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PrescriptionParserServiceImpl implements PrescriptionParserService {

    private static final List<String> NOISE_KEYWORDS = Arrays.asList(
            "invoice", "bill", "cash memo", "tax invoice", "pharmacy", "hospital",
            "patient", "doctor", "dr.", "date", "subtotal", "sub total", "cgst", "sgst",
            "igst", "gst", "total", "grand total", "net amount", "discount", "round off",
            "thank you", "visit again", "signature", "pharmacist", "reg no", "dl no",
            "address", "phone", "mobile", "tel:", "s.no", "serial", "item description",
            "hsn", "batch", "exp", "mfg", "qty", "rate", "amount", "mrp"
    );

    private static final Pattern QTY_PATTERN = Pattern.compile(
            "\\b(?:qty|quantity|x|count)?\\s*[:=]?\\s*(\\d{1,4})\\s*(?:tab|tablet|tablets|cap|capsule|capsules|strip|strips|nos|units)?\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public List<InputMedicineDto> parseTextToCandidateMedicines(String text) {
        List<InputMedicineDto> candidates = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return candidates;
        }

        String[] lines = text.split("\\r?\\n");
        for (String rawLine : lines) {
            String trimmed = rawLine.trim();
            if (trimmed.length() < 3) continue;

            if (isNoiseLine(trimmed)) {
                continue;
            }

            InputMedicineDto candidate = parseCandidateLine(trimmed);
            if (candidate != null && !candidate.getRawText().trim().isEmpty()) {
                candidates.add(candidate);
            }
        }

        return candidates;
    }

    @Override
    public InputMedicineDto parseSingleMedicineQuery(String medicineQuery) {
        if (medicineQuery == null || medicineQuery.trim().isEmpty()) {
            return null;
        }

        String trimmed = medicineQuery.trim();
        String strength = MedicineNormalizer.extractStrength(trimmed);
        String form = MedicineNormalizer.extractDosageForm(trimmed);

        // Name is whatever remains after removing form and strength
        String cleanedName = cleanMedicineName(trimmed);

        return InputMedicineDto.builder()
                .rawText(trimmed)
                .matchedName(cleanedName)
                .strength(strength)
                .dosageForm(form)
                .price(extractPrice(trimmed, strength))
                .confidence(0.95)
                .needsReview(false)
                .build();
    }

    private boolean isNoiseLine(String line) {
        String lower = line.toLowerCase();
        for (String keyword : NOISE_KEYWORDS) {
            if (lower.equals(keyword) || lower.startsWith(keyword + ":") || lower.startsWith(keyword + " :")) {
                return true;
            }
        }
        // Check if line is purely symbols or numbers
        if (line.matches("^[\\d\\s\\W_]+$")) {
            return true;
        }
        return false;
    }

    private InputMedicineDto parseCandidateLine(String line) {
        // Strip leading numbering e.g. "1.", "1 -", "01."
        String cleanLine = line.replaceAll("^\\s*\\d+([.)\\-\\s]+)", "").trim();

        String strength = MedicineNormalizer.extractStrength(cleanLine);
        String form = MedicineNormalizer.extractDosageForm(cleanLine);
        Double price = extractPrice(cleanLine, strength);

        String cleanedName = cleanMedicineName(cleanLine);
        if (cleanedName.length() < 3) {
            return null;
        }

        // Confidence estimation
        double confidence = 0.85;
        if (strength.isEmpty()) {
            confidence -= 0.15;
        }
        if (containsOcrAnomalies(cleanLine)) {
            confidence -= 0.15;
        }
        if (cleanedName.length() > 30) {
            confidence -= 0.10;
        }

        confidence = Math.max(0.40, Math.min(0.98, confidence));
        boolean needsReview = confidence < 0.75;

        return InputMedicineDto.builder()
                .rawText(line)
                .matchedName(cleanedName)
                .strength(strength)
                .dosageForm(form)
                .price(price)
                .confidence(Math.round(confidence * 100.0) / 100.0)
                .needsReview(needsReview)
                .build();
    }

    private String cleanMedicineName(String line) {
        // Remove trailing price when marked with currency or decimals e.g. "Rs 33.00", "₹ 55", "33.00"
        String cleaned = line
                .replaceAll("(?i)\\b(?:rs\\.?|inr|₹)\\s*\\d+(?:\\.\\d+)?\\s*(?:tab|tablets|cap|capsules|strip|strips|nos)?$", "")
                .replaceAll("(?i)\\b\\d+\\.\\d{2}\\s*(?:tab|tablets|cap|capsules|strip|strips|nos)?$", "")
                .replaceAll("(?i)\\b(?:qty|quantity)\\s*[:=]?\\s*\\d+", "")
                .replaceAll("(?i)\\b(?:tab|tablet|tablets|cap|capsule|capsules|strip|strips|syrup|suspension|inj|injection)\\b", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return cleaned;
    }

    private static final Pattern PRICE_PATTERN = Pattern.compile(
            "(?:(?:rs\\.?|inr|₹|@)\\s*(\\d+(?:\\.\\d{1,2})?))|(\\b\\d+\\.\\d{2}\\b)",
            Pattern.CASE_INSENSITIVE
    );

    private Double extractPrice(String line, String strength) {
        if (line == null) return null;
        Matcher matcher = PRICE_PATTERN.matcher(line);
        Double foundPrice = null;
        while (matcher.find()) {
            String matchStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            if (matchStr != null) {
                try {
                    double val = Double.parseDouble(matchStr);
                    if (strength != null && !strength.isEmpty()) {
                        String numOnly = strength.replaceAll("[^0-9.]", "");
                        if (!numOnly.isEmpty() && Math.abs(val - Double.parseDouble(numOnly)) < 0.001) {
                            continue;
                        }
                    }
                    if (val > 0.05 && val < 50000.0) {
                        foundPrice = val;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return foundPrice;
    }

    private boolean containsOcrAnomalies(String text) {
        // e.g. unusual character sequences like "l|", "][", "0O", "1I"
        return text.contains("][") || text.contains("{") || text.contains("}") ||
               text.contains("~~") || text.matches(".*[a-z][A-Z][a-z].*");
    }
}
