package ua.lpnu.kzp.metrics;

import java.util.List;

/** Calculates numeric metrics from already validated project-task CSV rows. */
public final class ProjectMetricsCalculator {
    private ProjectMetricsCalculator() {
    }

    /**
     * Counts rows that were already accepted by the validation layer.
     *
     * @param validLines validated raw CSV rows
     * @return number of valid records
     */
    public static int countValidRecords(List<String> validLines) {
        return validLines.size();
    }

    /**
     * Sums estimate-hours values from already validated project-task CSV rows.
     *
     * @param validLines validated raw CSV rows
     * @return total estimate hours
     */
    public static double totalEstimateHours(List<String> validLines) {
        double total = 0.0;
        for (String line : validLines) {
            String[] fields = line.split(";", -1);
            total += Double.parseDouble(fields[2].trim());
        }
        return total;
    }

    /**
     * Calculates the average priority from already validated project-task CSV rows.
     *
     * @param validLines validated raw CSV rows
     * @return average priority, or {@code 0.0} when there are no valid rows
     */
    public static double averagePriority(List<String> validLines) {
        if (validLines.isEmpty()) {
            return 0.0;
        }

        int totalPriority = 0;
        for (String line : validLines) {
            String[] fields = line.split(";", -1);
            totalPriority += Integer.parseInt(fields[3].trim());
        }

        return (double) totalPriority / validLines.size();
    }

    /**
     * Counts completed tasks from already validated project-task CSV rows.
     *
     * @param validLines validated raw CSV rows
     * @return number of rows whose done value is {@code true}
     */
    public static int countDoneTasks(List<String> validLines) {
        int doneCount = 0;
        for (String line : validLines) {
            String[] fields = line.split(";", -1);
            if (Boolean.parseBoolean(fields[4].trim())) {
                doneCount++;
            }
        }
        return doneCount;
    }
}
