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
import ua.lpnu.kzp.model.ProjectTask;

/** Tests UTF-8 reading into typed tasks and row-numbered errors. */
class ProjectFileReaderTest {
    private static final String FIRST_VALID_ROW = "Розробити авторизацію;Іван Петренко;8.5;3;true";
    private static final String SECOND_VALID_ROW = "Підготувати документацію;Олена Коваль;4.0;2;false";

    @TempDir
    Path temporaryDirectory;

    @Test
    void returnsTwoValidTasksWithoutErrors() throws IOException {
        List<String> errors = new ArrayList<>();
        List<ProjectTask> tasks = ProjectFileReader.readTasks(
                writeLines(FIRST_VALID_ROW, SECOND_VALID_ROW), errors);

        assertEquals(2, tasks.size());
        assertEquals("Розробити авторизацію", tasks.getFirst().getTitle());
        assertEquals("Олена Коваль", tasks.getLast().getAssignee());
        assertTrue(errors.isEmpty());
    }

    @Test
    void keepsValidTasksAndReportsInvalidRowNumber() throws IOException {
        List<String> errors = new ArrayList<>();
        List<ProjectTask> tasks = ProjectFileReader.readTasks(
                writeLines(FIRST_VALID_ROW, "Task;User;abc;3;true", SECOND_VALID_ROW), errors);

        assertEquals(2, tasks.size());
        assertEquals("Розробити авторизацію", tasks.getFirst().getTitle());
        assertEquals("Підготувати документацію", tasks.getLast().getTitle());
        assertEquals(List.of("Рядок 2: поле estimateHours має некоректне значення"), errors);
    }

    @Test
    void reportsBlankLineWithItsNumber() throws IOException {
        List<String> errors = new ArrayList<>();

        ProjectFileReader.readTasks(writeLines(FIRST_VALID_ROW, "", SECOND_VALID_ROW), errors);

        assertEquals(List.of("Рядок 2: порожній рядок"), errors);
    }

    @Test
    void readsUkrainianCharactersAsUtf8() throws IOException {
        List<String> errors = new ArrayList<>();
        List<ProjectTask> tasks = ProjectFileReader.readTasks(writeLines(SECOND_VALID_ROW), errors);

        assertEquals("Підготувати документацію", tasks.getFirst().getTitle());
        assertEquals("Олена Коваль", tasks.getFirst().getAssignee());
        assertTrue(errors.isEmpty());
    }

    @Test
    void returnsNoTasksAndNoErrorsForEmptyInputFile() throws IOException {
        Path input = temporaryDirectory.resolve("empty-input.csv");
        Files.writeString(input, "", StandardCharsets.UTF_8);
        List<String> errors = new ArrayList<>();

        List<ProjectTask> tasks = ProjectFileReader.readTasks(input, errors);

        assertTrue(tasks.isEmpty());
        assertTrue(errors.isEmpty());
    }

    private Path writeLines(String... lines) throws IOException {
        Path input = temporaryDirectory.resolve("input.csv");
        Files.write(input, List.of(lines), StandardCharsets.UTF_8);
        return input;
    }
}
