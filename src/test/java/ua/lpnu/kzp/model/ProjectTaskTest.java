package ua.lpnu.kzp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests constructor invariants and immutable accessors of {@link ProjectTask}. */
class ProjectTaskTest {
    @Test
    void storesEveryFieldOfValidTask() {
        ProjectTask task = new ProjectTask("Підготувати звіт", "Танічкін В. О.", 8.5, 0, true);

        assertEquals("Підготувати звіт", task.getTitle());
        assertEquals("Танічкін В. О.", task.getAssignee());
        assertEquals(8.5, task.getEstimateHours(), 0.0001);
        assertEquals(0, task.getPriority());
        assertTrue(task.isDone());
    }

    @Test
    void acceptsZeroEstimateAndPriorityAsBoundaryValues() {
        ProjectTask task = new ProjectTask("Планування", "Виконавець", 0.0, 0, false);

        assertEquals(0.0, task.getEstimateHours(), 0.0001);
        assertEquals(0, task.getPriority());
    }

    @Test
    void rejectsNullOrBlankTextFields() {
        assertThrows(NullPointerException.class,
                () -> new ProjectTask(null, "Виконавець", 1.0, 1, false));
        assertThrows(IllegalArgumentException.class,
                () -> new ProjectTask("   ", "Виконавець", 1.0, 1, false));
        assertThrows(NullPointerException.class,
                () -> new ProjectTask("Задача", null, 1.0, 1, false));
        assertThrows(IllegalArgumentException.class,
                () -> new ProjectTask("Задача", "\t", 1.0, 1, false));
    }

    @Test
    void rejectsInvalidNumericInvariants() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProjectTask("Задача", "Виконавець", -0.1, 0, false));
        assertThrows(IllegalArgumentException.class,
                () -> new ProjectTask("Задача", "Виконавець", Double.NaN, 0, false));
        assertThrows(IllegalArgumentException.class,
                () -> new ProjectTask("Задача", "Виконавець",
                        Double.POSITIVE_INFINITY, 0, false));
        assertThrows(IllegalArgumentException.class,
                () -> new ProjectTask("Задача", "Виконавець", 1.0, -1, false));
    }

    @Test
    void formatsFractionalEstimateWithLocaleRoot() {
        ProjectTask task = new ProjectTask("Задача", "Виконавець", 8.5, 2, true);

        assertEquals("Задача; виконавець: Виконавець; оцінка: 8.50; пріоритет: 2; виконано: true",
                task.toString());
    }

    @Test
    void fromCsvCreatesTaskAndPreservesParsedFields() {
        ProjectTask task = ProjectTask.fromCsv("Розробити авторизацію;Іван Петренко;8.5;3;true");

        assertEquals("Розробити авторизацію", task.getTitle());
        assertEquals("Іван Петренко", task.getAssignee());
        assertEquals(8.5, task.getEstimateHours(), 0.0001);
        assertEquals(3, task.getPriority());
        assertTrue(task.isDone());
    }

    @Test
    void fromCsvTrimsFieldsAndAcceptsBooleanIgnoringCase() {
        ProjectTask task = ProjectTask.fromCsv("  Планування ; Виконавець ; 0.0 ; 0 ; FALSE ");

        assertEquals("Планування", task.getTitle());
        assertEquals("Виконавець", task.getAssignee());
        assertEquals(0.0, task.getEstimateHours(), 0.0001);
        assertEquals(0, task.getPriority());
        assertFalse(task.isDone());
    }

    @Test
    void fromCsvRejectsWrongNumberOfFieldsAndKeepsTrailingField() {
        IllegalArgumentException tooFew = assertThrows(IllegalArgumentException.class,
                () -> ProjectTask.fromCsv("Задача;Виконавець;1.0;2"));
        IllegalArgumentException emptyTrailingField = assertThrows(IllegalArgumentException.class,
                () -> ProjectTask.fromCsv("Задача;Виконавець;1.0;2;"));
        IllegalArgumentException blankLine = assertThrows(IllegalArgumentException.class,
                () -> ProjectTask.fromCsv("   "));

        assertEquals("очікується 5 полів", tooFew.getMessage());
        assertEquals("поле done має містити true або false", emptyTrailingField.getMessage());
        assertEquals("порожній рядок", blankLine.getMessage());
    }

    @Test
    void fromCsvRejectsEmptyTitleAndAssigneeThroughConstructor() {
        assertThrows(IllegalArgumentException.class,
                () -> ProjectTask.fromCsv(" ;Виконавець;1.0;2;true"));
        assertThrows(IllegalArgumentException.class,
                () -> ProjectTask.fromCsv("Задача;  ;1.0;2;true"));
    }

    @Test
    void fromCsvRejectsInvalidOrNegativeEstimateHours() {
        IllegalArgumentException invalidNumber = assertThrows(IllegalArgumentException.class,
                () -> ProjectTask.fromCsv("Задача;Виконавець;abc;2;true"));

        assertEquals("поле estimateHours має некоректне значення", invalidNumber.getMessage());
        assertThrows(IllegalArgumentException.class,
                () -> ProjectTask.fromCsv("Задача;Виконавець;-1.0;2;true"));
    }

    @Test
    void fromCsvRejectsInvalidPriority() {
        IllegalArgumentException invalidPriority = assertThrows(IllegalArgumentException.class,
                () -> ProjectTask.fromCsv("Задача;Виконавець;1.0;high;true"));

        assertEquals("поле priority має некоректне значення", invalidPriority.getMessage());
        assertThrows(IllegalArgumentException.class,
                () -> ProjectTask.fromCsv("Задача;Виконавець;1.0;-1;true"));
    }

    @Test
    void fromCsvRejectsInvalidBoolean() {
        IllegalArgumentException invalidDone = assertThrows(IllegalArgumentException.class,
                () -> ProjectTask.fromCsv("Задача;Виконавець;1.0;2;yes"));

        assertEquals("поле done має містити true або false", invalidDone.getMessage());
    }
}
