package ua.lpnu.kzp.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import ua.lpnu.kzp.model.EstimatePriority;
import ua.lpnu.kzp.model.ProjectTask;

/** Tests metrics calculated from valid project-task objects. */
class ProjectMetricsCalculatorTest {
    private static final List<ProjectTask> FIVE_VALID_TASKS = List.of(
            ProjectTask.fromCsv("Розробити авторизацію;Іван Петренко;8.5;3;true"),
            ProjectTask.fromCsv("Підготувати документацію;Олена Коваль;4.0;2;false"),
            ProjectTask.fromCsv("Протестувати API;Андрій Мельник;6.5;1;true"),
            ProjectTask.fromCsv("Налаштувати CI;Марія Бойко;3.0;4;true"),
            ProjectTask.fromCsv("Оптимізувати запити;Софія Левченко;5.5;5;false"));

    @Test
    void countsFiveValidRecords() {
        assertEquals(5, ProjectMetricsCalculator.countValidRecords(FIVE_VALID_TASKS));
    }

    @Test
    void countsEmptyListAsZero() {
        assertEquals(0, ProjectMetricsCalculator.countValidRecords(List.of()));
    }

    @Test
    void aggregatesEstimateHoursAndPriorityForReferenceTasks() {
        EstimatePriority aggregate = ProjectMetricsCalculator
                .calculateEstimatePriority(FIVE_VALID_TASKS);

        assertEquals(27.5, aggregate.totalEstimateHours(), 0.0001);
        assertEquals(15, aggregate.totalPriority());
    }

    @Test
    void aggregatesEmptyListAsZero() {
        EstimatePriority aggregate = ProjectMetricsCalculator.calculateEstimatePriority(List.of());

        assertEquals(0.0, aggregate.totalEstimateHours(), 0.0001);
        assertEquals(0, aggregate.totalPriority());
    }

    @Test
    void calculatesAveragePriorityForReferenceTasks() {
        EstimatePriority aggregate = ProjectMetricsCalculator
                .calculateEstimatePriority(FIVE_VALID_TASKS);

        assertEquals(3.0, ProjectMetricsCalculator.averagePriority(aggregate, FIVE_VALID_TASKS.size()),
                0.0001);
    }

    @Test
    void keepsFractionalPartOfAveragePriority() {
        List<ProjectTask> tasks = List.of(
                ProjectTask.fromCsv("Task one;User;1.0;1;false"),
                ProjectTask.fromCsv("Task two;User;1.0;2;true"));

        assertEquals(1.5, ProjectMetricsCalculator.averagePriority(
                ProjectMetricsCalculator.calculateEstimatePriority(tasks), tasks.size()), 0.0001);
    }

    @Test
    void calculatesEmptyAveragePriorityAsZero() {
        assertEquals(0.0, ProjectMetricsCalculator.averagePriority(new EstimatePriority(0.0, 0), 0),
                0.0001);
    }

    @Test
    void countsDoneTasksForReferenceTasks() {
        assertEquals(3, ProjectMetricsCalculator.countDoneTasks(FIVE_VALID_TASKS));
    }

    @Test
    void countsNoDoneTasksForEmptyList() {
        assertEquals(0, ProjectMetricsCalculator.countDoneTasks(List.of()));
    }
}
