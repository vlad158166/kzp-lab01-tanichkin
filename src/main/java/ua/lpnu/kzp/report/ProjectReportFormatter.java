package ua.lpnu.kzp.report;

import java.util.List;
import java.util.Locale;
import ua.lpnu.kzp.metrics.ProjectMetricsCalculator;

/** Formats processing results for already validated project-task CSV rows. */
public final class ProjectReportFormatter {
    private ProjectReportFormatter() {
    }

    /**
     * Formats metrics and validation errors as one Ukrainian text report.
     *
     * @param validLines validated raw CSV rows
     * @param errors validation error messages with row numbers
     * @return formatted text report
     */
    public static String formatReport(List<String> validLines, List<String> errors) {
        int count = ProjectMetricsCalculator.countValidRecords(validLines);
        double totalHours = ProjectMetricsCalculator.totalEstimateHours(validLines);
        double averagePriority = ProjectMetricsCalculator.averagePriority(validLines);
        int doneCount = ProjectMetricsCalculator.countDoneTasks(validLines);

        StringBuilder report = new StringBuilder();
        report.append(String.format(Locale.ROOT, "Результати обробки проєктних задач%n%n"));
        report.append(String.format(Locale.ROOT, "Кількість коректних записів: %d%n", count));
        report.append(String.format(Locale.ROOT, "Сумарна оцінка годин: %.2f%n", totalHours));
        report.append(String.format(Locale.ROOT, "Середній пріоритет: %.2f%n", averagePriority));
        report.append(String.format(Locale.ROOT, "Кількість виконаних задач: %d%n", doneCount));
        report.append(String.format(Locale.ROOT, "Кількість помилкових записів: %d%n", errors.size()));

        if (!errors.isEmpty()) {
            report.append(String.format(Locale.ROOT, "%nПомилки:%n"));
            for (String error : errors) {
                report.append(String.format(Locale.ROOT, "%s%n", error));
            }
        }

        return report.toString();
    }
}
