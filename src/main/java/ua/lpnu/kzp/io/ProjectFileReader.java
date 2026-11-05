package ua.lpnu.kzp.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import ua.lpnu.kzp.model.ProjectTask;

/** Reads UTF-8 project-task CSV rows and separates valid tasks from row-level errors. */
public final class ProjectFileReader {
    private ProjectFileReader() {
    }

    /**
     * Reads all lines from a UTF-8 input file and creates each valid task independently.
     *
     * @param input path to the input CSV file
     * @param errors destination for row-numbered validation errors
     * @return valid project-task objects created from the CSV rows
     * @throws IOException if the input file cannot be read
     */
    public static List<ProjectTask> readTasks(Path input, List<String> errors) throws IOException {
        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        List<ProjectTask> tasks = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            try {
                tasks.add(ProjectTask.fromCsv(lines.get(index)));
            } catch (IllegalArgumentException exception) {
                errors.add("Рядок " + (index + 1) + ": " + exception.getMessage());
            }
        }
        return tasks;
    }
}
