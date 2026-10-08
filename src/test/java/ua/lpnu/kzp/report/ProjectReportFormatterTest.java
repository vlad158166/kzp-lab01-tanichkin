package ua.lpnu.kzp.report;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests text formatting for the project-task processing report. */
class ProjectReportFormatterTest {
    private static final List<String> FIVE_VALID_LINES = List.of(
            "Розробити авторизацію;Іван Петренко;8.5;3;true",
            "Підготувати документацію;Олена Коваль;4.0;2;false",
            "Протестувати API;Андрій Мельник;6.5;1;true",
            "Налаштувати CI;Марія Бойко;3.0;4;true",
            "Оптимізувати запити;Софія Левченко;5.5;5;false");

    @Test
    void formatsReferenceMetricsWithTwoDecimalPlaces() {
        String report = ProjectReportFormatter.formatReport(FIVE_VALID_LINES, List.of());

        assertTrue(report.contains("Кількість коректних записів: 5"));
        assertTrue(report.contains("Сумарна оцінка годин: 27.50"));
        assertTrue(report.contains("Середній пріоритет: 3.00"));
        assertTrue(report.contains("Кількість виконаних задач: 3"));
        assertTrue(report.contains("Кількість помилкових записів: 0"));
    }

    @Test
    void omitsErrorsSectionWhenThereAreNoErrors() {
        String report = ProjectReportFormatter.formatReport(FIVE_VALID_LINES, List.of());

        assertFalse(report.contains("Помилки:"));
    }

    @Test
    void includesAllErrorsWhenTheyArePresent() {
        List<String> errors = List.of(
                "Рядок 6: поле title порожнє",
                "Рядок 7: поле done має містити true або false");

        String report = ProjectReportFormatter.formatReport(FIVE_VALID_LINES, errors);

        assertTrue(report.contains("Кількість помилкових записів: 2"));
        assertTrue(report.contains("Помилки:"));
        assertTrue(report.contains(errors.get(0)));
        assertTrue(report.contains(errors.get(1)));
    }

    @Test
    void formatsEmptyValidLinesAsZeroMetrics() {
        String report = ProjectReportFormatter.formatReport(List.of(), List.of());

        assertTrue(report.contains("Кількість коректних записів: 0"));
        assertTrue(report.contains("Сумарна оцінка годин: 0.00"));
        assertTrue(report.contains("Середній пріоритет: 0.00"));
        assertTrue(report.contains("Кількість виконаних задач: 0"));
    }

    @Test
    void preservesUkrainianTextAndUsesSystemLineSeparator() {
        String report = ProjectReportFormatter.formatReport(FIVE_VALID_LINES, List.of());

        assertTrue(report.startsWith("Результати обробки проєктних задач"));
        assertTrue(report.contains(System.lineSeparator()));
    }
}
