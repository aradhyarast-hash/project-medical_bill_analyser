package com.analyzerbackend.backend.Utility;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MedicineNormalizer {

    private static final LevenshteinDistance LEVENSHTEIN = new LevenshteinDistance();

    private static final Pattern STRENGTH_PATTERN = Pattern.compile(
            "(\\d+(?:\\.\\d+)?\\s*(?:mg|g|mcg|ml|iu)(?:\\s*\\+\\s*\\d+(?:\\.\\d+)?\\s*(?:mg|g|mcg|ml|iu))*)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern FORM_PATTERN = Pattern.compile(
            "\\b(tablet|tab|tablets|capsule|cap|capsules|syrup|syp|injection|inj|drops|suspension|gel|cream|ointment)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern RELEASE_PATTERN = Pattern.compile(
            "\\b(sr|er|xr|cr|mr|prolonged-release|sustained release|extended release|gastro-resistant|immediate)\\b",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Normalizes a brand or medicine name by lowercasing, stripping special chars,
     * collapsing multiple spaces, and trimming.
     */
    public static String normalizeName(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Extracts strength such as "500mg", "650mg", "5mg+50mg".
     */
    public static String extractStrength(String text) {
        if (text == null) return "";
        Matcher matcher = STRENGTH_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).replaceAll("\\s+", "").toLowerCase();
        }
        return "";
    }

    /**
     * Standardizes dosage form (Tablet, Capsule, Syrup, etc.).
     */
    public static String extractDosageForm(String text) {
        if (text == null) return "Tablet";
        Matcher matcher = FORM_PATTERN.matcher(text);
        if (matcher.find()) {
            String form = matcher.group(1).toLowerCase();
            if (form.startsWith("tab")) return "Tablet";
            if (form.startsWith("cap")) return "Capsule";
            if (form.startsWith("syp") || form.equals("syrup")) return "Syrup";
            if (form.startsWith("inj")) return "Injection";
            if (form.equals("drops")) return "Drops";
            return capitalize(form);
        }
        return "Tablet";
    }

    /**
     * Standardizes release type (immediate, prolonged-release, gastro-resistant).
     */
    public static String extractReleaseType(String text) {
        if (text == null) return "immediate";
        String lower = text.toLowerCase();
        if (lower.contains("sr") || lower.contains("er") || lower.contains("xr") ||
            lower.contains("cr") || lower.contains("mr") ||
            lower.contains("sustained") || lower.contains("extended") ||
            lower.contains("prolonged")) {
            return "prolonged-release";
        }
        if (lower.contains("gastro") || lower.contains("enteric")) {
            return "gastro-resistant";
        }
        return "immediate";
    }

    /**
     * Computes similarity score between two strings (0.0 to 1.0).
     */
    public static double computeSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        String n1 = normalizeName(s1);
        String n2 = normalizeName(s2);
        if (n1.isEmpty() && n2.isEmpty()) return 1.0;
        if (n1.isEmpty() || n2.isEmpty()) return 0.0;
        if (n1.equals(n2)) return 1.0;
        if (n1.contains(n2) || n2.contains(n1)) {
            double ratio = (double) Math.min(n1.length(), n2.length()) / Math.max(n1.length(), n2.length());
            return Math.max(0.85, ratio);
        }

        int maxLength = Math.max(n1.length(), n2.length());
        int distance = LEVENSHTEIN.apply(n1, n2);
        return Math.max(0.0, 1.0 - ((double) distance / maxLength));
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}
