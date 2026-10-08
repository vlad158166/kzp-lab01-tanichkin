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
import ua.lpnu.kzp.report.ProjectReportFormatter;

/** Tests edge cases across reading, validation, report formatting, and UTF-8 output. */
class ProjectProcessingPipelineTest {
    @TempDir
    Path tempDir;

    @Test
    void createsZeroReportWhenEveryInputRowIsInvalid() throws IOException {
        Path input = writeInput(
                ";Виконавець;2.0;3;true",
                "Задача;Виконавець;-1.0;3;false");
        List<String> errors = new ArrayList<>();

        List<String> validLines = ProjectFileReader.readValidLines(input, errors);
        String report = ProjectReportFormatter.formatReport(validLines, errors);

        assertTrue(validLines.isEmpty());
        assertEquals(2, errors.size());
        assertTrue(report.contains("Кількість коректних записів: 0"));
        assertTrue(report.contains("Сумарна оцінка годин: 0.00"));
        assertTrue(report.contains("Середній пріоритет: 0.00"));
        assertTrue(report.contains("Кількість виконаних задач: 0"));
        assertTrue(report.contains("Кількість помилкових записів: 2"));
    }

    @Test
    void excludesInvalidRowFromMetricsAndPreservesUkrainianUtf8EndToEnd() throws IOException {
        String firstValid = "Розробити авторизацію;Іван Петренко;8.5;2;true";
        String secondValid = "Підготувати документацію;Олена Коваль;1.5;4;false";
        Path input = writeInput(firstValid, "Некоректні години;Андрій;abc;3;true", secondValid);
        Path output = tempDir.resolve("out").resolve("report.txt");
        List<String> errors = new ArrayList<>();

        List<String> validLines = ProjectFileReader.readValidLines(input, errors);
        String report = ProjectReportFormatter.formatReport(validLines, errors);
        ProjectReportWriter.writeReport(output, report);

        assertEquals(List.of(firstValid, secondValid), validLines);
        assertEquals(List.of("Рядок 2: поле estimateHours має некоректне значення"), errors);
        assertTrue(report.contains("Кількість коректних записів: 2"));
        assertTrue(report.contains("Сумарна оцінка годин: 10.00"));
        assertTrue(report.contains("Середній пріоритет: 3.00"));
        assertTrue(report.contains("Кількість виконаних задач: 1"));
        assertTrue(report.contains("Кількість помилкових записів: 1"));
        assertEquals(report, Files.readString(output, StandardCharsets.UTF_8));
        assertEquals(firstValid, validLines.getFirst());
    }

    private Path writeInput(String... lines) throws IOException {
        Path input = tempDir.resolve("input.csv");
        Files.write(input, List.of(lines), StandardCharsets.UTF_8);
        return input;
    }
}
