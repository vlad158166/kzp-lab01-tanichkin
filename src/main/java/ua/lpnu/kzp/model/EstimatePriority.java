package ua.lpnu.kzp.model;

/**
 * Immutable aggregate of valid project-task effort estimates and priorities.
 *
 * @param totalEstimateHours sum of non-negative finite effort estimates
 * @param totalPriority sum of non-negative task priorities
 */
public record EstimatePriority(double totalEstimateHours, int totalPriority) {
    /** Validates aggregate values derived from valid {@link ProjectTask} objects. */
    public EstimatePriority {
        if (!Double.isFinite(totalEstimateHours) || totalEstimateHours < 0.0) {
            throw new IllegalArgumentException(
                    "Сумарна оцінка годин має бути скінченною і не від'ємною");
        }
        if (totalPriority < 0) {
            throw new IllegalArgumentException("Сумарний пріоритет не може бути від'ємним");
        }
    }
}
