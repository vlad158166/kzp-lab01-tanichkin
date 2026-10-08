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
}
