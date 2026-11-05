package ua.lpnu.kzp.metrics;

import java.util.List;
import ua.lpnu.kzp.model.EstimatePriority;
import ua.lpnu.kzp.model.ProjectTask;

/** Calculates numeric metrics from already validated project-task objects. */
public final class ProjectMetricsCalculator {
    private ProjectMetricsCalculator() {
    }

    /**
     * Counts rows that were already accepted by the validation layer.
     *
     * @param tasks validated project tasks
     * @return number of valid records
     */
    public static int countValidRecords(List<ProjectTask> tasks) {
        return tasks.size();
    }

    /**
     * Aggregates estimate hours and priorities from already validated project tasks.
     *
     * @param tasks validated project tasks
     * @return immutable aggregate used for report metrics
     */
    public static EstimatePriority calculateEstimatePriority(List<ProjectTask> tasks) {
        double totalEstimateHours = 0.0;
        int totalPriority = 0;
        for (ProjectTask task : tasks) {
            totalEstimateHours += task.getEstimateHours();
            totalPriority += task.getPriority();
        }
        return new EstimatePriority(totalEstimateHours, totalPriority);
    }

    /**
     * Calculates average priority from an aggregate and the number of valid tasks.
     *
     * @param aggregate sum of estimate-hours and priority values
     * @param validTaskCount number of valid project tasks
     * @return average priority, or {@code 0.0} when there are no valid rows
     */
    public static double averagePriority(EstimatePriority aggregate, int validTaskCount) {
        if (validTaskCount == 0) {
            return 0.0;
        }
        return (double) aggregate.totalPriority() / validTaskCount;
    }

    /**
     * Counts completed tasks from already validated project-task objects.
     *
     * @param tasks validated project tasks
     * @return number of rows whose done value is {@code true}
     */
    public static int countDoneTasks(List<ProjectTask> tasks) {
        int doneCount = 0;
        for (ProjectTask task : tasks) {
            if (task.isDone()) {
                doneCount++;
            }
        }
        return doneCount;
    }
}
