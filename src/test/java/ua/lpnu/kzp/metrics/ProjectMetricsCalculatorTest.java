package ua.lpnu.kzp.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests metrics calculated from valid raw CSV rows. */
class ProjectMetricsCalculatorTest {
    private static final List<String> FIVE_VALID_LINES = List.of(
            "Розробити авторизацію;Іван Петренко;8.5;3;true",
            "Підготувати документацію;Олена Коваль;4.0;2;false",
            "Протестувати API;Андрій Мельник;6.5;1;true",
            "Налаштувати CI;Марія Бойко;3.0;4;true",
            "Оптимізувати запити;Софія Левченко;5.5;5;false");

    @Test
    void countsFiveValidRecords() {
        assertEquals(5, ProjectMetricsCalculator.countValidRecords(FIVE_VALID_LINES));
    }

    @Test
    void countsEmptyListAsZero() {
        assertEquals(0, ProjectMetricsCalculator.countValidRecords(List.of()));
    }

    @Test
    void sumsEstimateHoursForFiveValidRecords() {
        double actual = ProjectMetricsCalculator.totalEstimateHours(FIVE_VALID_LINES);

        assertEquals(27.5, actual, 0.0001);
    }

    @Test
    void sumsEmptyListAsZero() {
        assertEquals(0.0, ProjectMetricsCalculator.totalEstimateHours(List.of()), 0.0001);
    }

    @Test
    void trimsEstimateHoursBeforeParsing() {
        List<String> validLines = List.of("Task;User; 4.0 ;3;true");

        assertEquals(4.0, ProjectMetricsCalculator.totalEstimateHours(validLines), 0.0001);
    }

    @Test
    void calculatesAveragePriorityForFiveValidRecords() {
        double actual = ProjectMetricsCalculator.averagePriority(FIVE_VALID_LINES);

        assertEquals(3.0, actual, 0.0001);
    }

    @Test
    void keepsFractionalPartOfAveragePriority() {
        List<String> validLines = List.of(
                "Task one;User;1.0;1;false",
                "Task two;User;1.0;2;true");

        assertEquals(1.5, ProjectMetricsCalculator.averagePriority(validLines), 0.0001);
    }

    @Test
    void calculatesEmptyAveragePriorityAsZero() {
        assertEquals(0.0, ProjectMetricsCalculator.averagePriority(List.of()), 0.0001);
    }

    @Test
    void countsDoneTasksForFiveValidRecords() {
        assertEquals(3, ProjectMetricsCalculator.countDoneTasks(FIVE_VALID_LINES));
    }

    @Test
    void countsNoDoneTasksForEmptyList() {
        assertEquals(0, ProjectMetricsCalculator.countDoneTasks(List.of()));
    }

    @Test
    void countsNoDoneTasksWhenAllValuesAreFalse() {
        List<String> validLines = List.of(
                "Task one;User;1.0;1;false",
                "Task two;User;1.0;2;FALSE");

        assertEquals(0, ProjectMetricsCalculator.countDoneTasks(validLines));
    }

    @Test
    void countsOneDoneTaskForUppercaseTrue() {
        List<String> validLines = List.of("Task;User;1.0;1;TRUE");

        assertEquals(1, ProjectMetricsCalculator.countDoneTasks(validLines));
    }
}
