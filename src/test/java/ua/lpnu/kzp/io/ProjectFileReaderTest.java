package ua.lpnu.kzp.io;

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

/** Tests UTF-8 reading and row-numbered validation errors without using project data files. */
class ProjectFileReaderTest {
    private static final String FIRST_VALID_ROW = "Розробити авторизацію;Іван Петренко;8.5;3;true";
    private static final String SECOND_VALID_ROW = "Підготувати документацію;Олена Коваль;4.0;2;false";

    @TempDir
    Path temporaryDirectory;

    @Test
    void returnsTwoValidLinesWithoutErrors() throws IOException {
        Path input = writeLines(FIRST_VALID_ROW, SECOND_VALID_ROW);
        List<String> errors = new ArrayList<>();

        List<String> validLines = ProjectFileReader.readValidLines(input, errors);

        assertEquals(List.of(FIRST_VALID_ROW, SECOND_VALID_ROW), validLines);
        assertTrue(errors.isEmpty());
    }

    @Test
    void keepsValidLinesAndReportsInvalidRowNumber() throws IOException {
        Path input = writeLines(FIRST_VALID_ROW, "Task;User;abc;3;true", SECOND_VALID_ROW);
        List<String> errors = new ArrayList<>();

        List<String> validLines = ProjectFileReader.readValidLines(input, errors);

        assertEquals(List.of(FIRST_VALID_ROW, SECOND_VALID_ROW), validLines);
        assertEquals(List.of("Рядок 2: поле estimateHours має некоректне значення"), errors);
    }

    @Test
    void reportsBlankLineWithItsNumber() throws IOException {
        Path input = writeLines(FIRST_VALID_ROW, "", SECOND_VALID_ROW);
        List<String> errors = new ArrayList<>();

        ProjectFileReader.readValidLines(input, errors);

        assertEquals(List.of("Рядок 2: порожній рядок"), errors);
    }

    @Test
    void keepsReasonFromRowValidatorForInvalidNumericField() throws IOException {
        Path input = writeLines("Task;User;abc;3;true");
        List<String> errors = new ArrayList<>();

        ProjectFileReader.readValidLines(input, errors);

        assertEquals(List.of("Рядок 1: поле estimateHours має некоректне значення"), errors);
    }

    @Test
    void readsUkrainianCharactersAsUtf8() throws IOException {
        Path input = writeLines(SECOND_VALID_ROW);
        List<String> errors = new ArrayList<>();

        List<String> validLines = ProjectFileReader.readValidLines(input, errors);

        assertEquals(List.of(SECOND_VALID_ROW), validLines);
        assertTrue(errors.isEmpty());
    }

    private Path writeLines(String... lines) throws IOException {
        Path input = temporaryDirectory.resolve("input.csv");
        Files.write(input, List.of(lines), StandardCharsets.UTF_8);
        return input;
    }
}
