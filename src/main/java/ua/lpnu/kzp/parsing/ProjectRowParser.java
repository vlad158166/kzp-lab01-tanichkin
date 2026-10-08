package ua.lpnu.kzp.parsing;

/** Validates individual fields of a project-task CSV record. */
public final class ProjectRowParser {
    private ProjectRowParser() {
    }

    /**
     * Checks whether a raw value is exactly {@code true} or {@code false}, ignoring letter case.
     *
     * @param raw raw CSV field value
     * @return {@code true} only for a trimmed true/false value
     */
    static boolean isValidDoneValue(String raw) {
        if (raw == null) {
            return false;
        }

        String normalized = raw.trim();
        return "true".equalsIgnoreCase(normalized) || "false".equalsIgnoreCase(normalized);
    }

    /**
     * Checks whether a CSV row contains exactly five fields, including an empty last field.
     *
     * @param line raw CSV row
     * @return {@code true} when the row has exactly five semicolon-separated fields
     */
    static boolean hasCorrectFieldCount(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }

        return line.split(";", -1).length == 5;
    }

    /**
     * Checks whether a text field contains at least one non-whitespace character.
     *
     * @param raw raw text field
     * @return {@code true} when the trimmed field is not empty
     */
    static boolean isNonBlank(String raw) {
        return raw != null && !raw.trim().isEmpty();
    }
}
