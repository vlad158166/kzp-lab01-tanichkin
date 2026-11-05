package ua.lpnu.kzp.model;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents one valid project task from variant 22.
 *
 * <p>The task state is immutable: validation is completed in the constructor and no mutating
 * methods are exposed.</p>
 */
public final class ProjectTask {
    private final String title;
    private final String assignee;
    private final double estimateHours;
    private final int priority;
    private final boolean done;

    /**
     * Creates a project task after validating its domain invariants.
     *
     * @param title task title
     * @param assignee person responsible for the task
     * @param estimateHours non-negative finite effort estimate in hours
     * @param priority non-negative task priority
     * @param done completion status
     * @throws NullPointerException if title or assignee is {@code null}
     * @throws IllegalArgumentException if text fields are blank or numeric values are invalid
     */
    public ProjectTask(String title, String assignee, double estimateHours, int priority,
            boolean done) {
        this.title = requireNonBlank(title, "поле title порожнє");
        this.assignee = requireNonBlank(assignee, "поле assignee порожнє");
        if (!Double.isFinite(estimateHours) || estimateHours < 0.0) {
            throw new IllegalArgumentException("поле estimateHours має некоректне значення");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("поле priority має некоректне значення");
        }
        this.estimateHours = estimateHours;
        this.priority = priority;
        this.done = done;
    }

    /**
     * Creates a project task from one variant-22 CSV row.
     *
     * <p>The row contains five semicolon-separated fields in this order: title, assignee,
     * estimate hours, priority, and completion status. Structure and textual numeric conversion
     * are handled here; domain invariants are enforced by the constructor.</p>
     *
     * @param line UTF-8 CSV row represented as a Java string
     * @return immutable valid project task
     * @throws NullPointerException if {@code line} is {@code null}
     * @throws IllegalArgumentException if the row has an invalid structure, number, boolean, or
     *         violates a task invariant
     */
    public static ProjectTask fromCsv(String line) {
        Objects.requireNonNull(line, "CSV-рядок не може бути null");
        if (line.isBlank()) {
            throw new IllegalArgumentException("порожній рядок");
        }
        String[] fields = line.split(";", -1);
        if (fields.length != 5) {
            throw new IllegalArgumentException("очікується 5 полів");
        }

        return new ProjectTask(
                fields[0].trim(),
                fields[1].trim(),
                parseEstimateHours(fields[2]),
                parsePriority(fields[3]),
                parseDone(fields[4]));
    }

    /** Returns the task title. */
    public String getTitle() {
        return title;
    }

    /** Returns the person responsible for the task. */
    public String getAssignee() {
        return assignee;
    }

    /** Returns the non-negative effort estimate in hours. */
    public double getEstimateHours() {
        return estimateHours;
    }

    /** Returns the non-negative task priority. */
    public int getPriority() {
        return priority;
    }

    /** Returns whether the task is complete. */
    public boolean isDone() {
        return done;
    }

    /**
     * Returns a readable representation intended for diagnostics, not CSV serialization.
     *
     * @return formatted immutable task state
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "%s; виконавець: %s; оцінка: %.2f; пріоритет: %d; виконано: %b",
                title, assignee, estimateHours, priority, done);
    }

    private static String requireNonBlank(String value, String message) {
        Objects.requireNonNull(value, message);
        if (value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static double parseEstimateHours(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("поле estimateHours має некоректне значення",
                    exception);
        }
    }

    private static int parsePriority(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("поле priority має некоректне значення", exception);
        }
    }

    private static boolean parseDone(String value) {
        String normalized = value.trim();
        if ("true".equalsIgnoreCase(normalized)) {
            return true;
        }
        if ("false".equalsIgnoreCase(normalized)) {
            return false;
        }
        throw new IllegalArgumentException("поле done має містити true або false");
    }
}
