package ua.lpnu.kzp.report;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import ua.lpnu.kzp.model.ProjectTask;

/** Tests text formatting for reports based on project-task objects. */
class ProjectReportFormatterTest {
    private static final List<ProjectTask> FIVE_VALID_TASKS = List.of(
            ProjectTask.fromCsv("Розробити авторизацію;Іван Петренко;8.5;3;true"),
            ProjectTask.fromCsv("Підготувати документацію;Олена Коваль;4.0;2;false"),
            ProjectTask.fromCsv("Протестувати API;Андрій Мельник;6.5;1;true"),
            ProjectTask.fromCsv("Налаштувати CI;Марія Бойко;3.0;4;true"),
            ProjectTask.fromCsv("Оптимізувати запити;Софія Левченко;5.5;5;false"));

    @Test
    void formatsReferenceMetricsWithTwoDecimalPlaces() {
        String report = ProjectReportFormatter.formatReport(FIVE_VALID_TASKS, List.of());

        assertTrue(report.contains("Кількість коректних записів: 5"));
        assertTrue(report.contains("Сумарна оцінка годин: 27.50"));
        assertTrue(report.contains("Середній пріоритет: 3.00"));
        assertTrue(report.contains("Кількість виконаних задач: 3"));
        assertTrue(report.contains("Кількість помилкових записів: 0"));
    }

    @Test
    void omitsErrorsSectionWhenThereAreNoErrors() {
        assertFalse(ProjectReportFormatter.formatReport(FIVE_VALID_TASKS, List.of())
                .contains("Помилки:"));
    }

    @Test
    void includesAllErrorsWhenTheyArePresent() {
        List<String> errors = List.of(
                "Рядок 6: поле title порожнє",
                "Рядок 7: поле done має містити true або false");
        String report = ProjectReportFormatter.formatReport(FIVE_VALID_TASKS, errors);

        assertTrue(report.contains("Кількість помилкових записів: 2"));
        assertTrue(report.contains("Помилки:"));
        assertTrue(report.contains(errors.getFirst()));
        assertTrue(report.contains(errors.getLast()));
    }

    @Test
    void formatsEmptyTaskListAsZeroMetrics() {
        String report = ProjectReportFormatter.formatReport(List.of(), List.of());

        assertTrue(report.contains("Кількість коректних записів: 0"));
        assertTrue(report.contains("Сумарна оцінка годин: 0.00"));
        assertTrue(report.contains("Середній пріоритет: 0.00"));
        assertTrue(report.contains("Кількість виконаних задач: 0"));
    }

    @Test
    void preservesUkrainianTextAndUsesSystemLineSeparator() {
        String report = ProjectReportFormatter.formatReport(FIVE_VALID_TASKS, List.of());

        assertTrue(report.startsWith("Результати обробки проєктних задач"));
        assertTrue(report.contains(System.lineSeparator()));
    }
}
