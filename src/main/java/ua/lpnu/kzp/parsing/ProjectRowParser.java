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
}
