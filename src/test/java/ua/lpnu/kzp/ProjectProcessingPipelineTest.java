package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ua.lpnu.kzp.io.ProjectFileReader;
import ua.lpnu.kzp.io.ProjectReportWriter;
import ua.lpnu.kzp.model.ProjectTask;
import ua.lpnu.kzp.report.ProjectReportFormatter;

/** Tests typed processing, errors, report compatibility, and UTF-8 output together. */
class ProjectProcessingPipelineTest {
    @TempDir
    Path tempDir;

    @Test
    void createsZeroReportWhenEveryInputRowIsInvalid() throws IOException {
        List<String> errors = new ArrayList<>();
        List<ProjectTask> tasks = ProjectFileReader.readTasks(writeInput(
                ";Виконавець;2.0;3;true",
                "Задача;Виконавець;-1.0;3;false"), errors);
        String report = ProjectReportFormatter.formatReport(tasks, errors);

        assertTrue(tasks.isEmpty());
        assertEquals(2, errors.size());
        assertTrue(report.contains("Кількість коректних записів: 0"));
        assertTrue(report.contains("Сумарна оцінка годин: 0.00"));
        assertTrue(report.contains("Середній пріоритет: 0.00"));
        assertTrue(report.contains("Кількість виконаних задач: 0"));
        assertTrue(report.contains("Кількість помилкових записів: 2"));
    }

    @Test
    void keepsValidTasksWhenOneRowIsInvalidAndWritesCompatibleReport() throws IOException {
        String firstValid = "Розробити авторизацію;Іван Петренко;8.5;2;true";
        String secondValid = "Підготувати документацію;Олена Коваль;1.5;4;false";
        Path output = tempDir.resolve("out").resolve("report.txt");
        List<String> errors = new ArrayList<>();
        List<ProjectTask> tasks = ProjectFileReader.readTasks(writeInput(
                firstValid, "Некоректні години;Андрій;abc;3;true", secondValid), errors);
        String report = ProjectReportFormatter.formatReport(tasks, errors);
        ProjectReportWriter.writeReport(output, report);

        assertEquals(2, tasks.size());
        assertEquals("Розробити авторизацію", tasks.getFirst().getTitle());
        assertEquals("Підготувати документацію", tasks.getLast().getTitle());
        assertEquals(List.of("Рядок 2: поле estimateHours має некоректне значення"), errors);
        assertTrue(report.contains("Кількість коректних записів: 2"));
        assertTrue(report.contains("Сумарна оцінка годин: 10.00"));
        assertTrue(report.contains("Середній пріоритет: 3.00"));
        assertTrue(report.contains("Кількість виконаних задач: 1"));
        assertTrue(report.contains("Кількість помилкових записів: 1"));
        assertEquals(report, Files.readString(output, StandardCharsets.UTF_8));
    }

    @Test
    void preservesCompleteLab01ReferenceReportWithTypedTasks() throws IOException {
        List<String> errors = new ArrayList<>();
        List<ProjectTask> tasks = ProjectFileReader.readTasks(writeInput(
                "Розробити авторизацію;Іван Петренко;8.5;3;true",
                "Підготувати документацію;Олена Коваль;4.0;2;false",
                "Протестувати API;Андрій Мельник;6.5;1;true",
                "Налаштувати CI;Марія Бойко;3.0;4;true",
                "Оптимізувати запити;Софія Левченко;5.5;5;false",
                ";Порожній заголовок;2.0;2;false",
                "Порожній виконавець;;2.0;2;false",
                "Некоректні години;Іван Петренко;abc;3;true",
                "Від'ємні години;Олена Коваль;-1.0;2;false",
                "Некоректний boolean;Андрій Мельник;1.0;1;yes",
                "Неповний;Марія Бойко;3.0;4"), errors);
        String report = ProjectReportFormatter.formatReport(tasks, errors);

        assertEquals(5, tasks.size());
        assertEquals(expectedReferenceReport(), report);
    }

    private Path writeInput(String... lines) throws IOException {
        Path input = tempDir.resolve("input.csv");
        Files.write(input, List.of(lines), StandardCharsets.UTF_8);
        return input;
    }

    private static String expectedReferenceReport() {
        String separator = System.lineSeparator();
        return String.join(separator,
                "Результати обробки проєктних задач",
                "",
                "Кількість коректних записів: 5",
                "Сумарна оцінка годин: 27.50",
                "Середній пріоритет: 3.00",
                "Кількість виконаних задач: 3",
                "Кількість помилкових записів: 6",
                "",
                "Помилки:",
                "Рядок 6: поле title порожнє",
                "Рядок 7: поле assignee порожнє",
                "Рядок 8: поле estimateHours має некоректне значення",
                "Рядок 9: поле estimateHours має некоректне значення",
                "Рядок 10: поле done має містити true або false",
                "Рядок 11: очікується 5 полів") + separator;
    }
}
