package ua.lpnu.kzp.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import ua.lpnu.kzp.parsing.ProjectRowParser;

/** Reads UTF-8 project-task CSV rows and separates valid rows from validation errors. */
public final class ProjectFileReader {
    private ProjectFileReader() {
    }

    /**
     * Reads all lines from a UTF-8 input file and validates each line independently.
     *
     * @param input path to the input CSV file
     * @param errors destination for row-numbered validation errors
     * @return raw CSV rows that passed validation
     * @throws IOException if the input file cannot be read
     */
    public static List<String> readValidLines(Path input, List<String> errors) throws IOException {
        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        List<String> validLines = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            String error = ProjectRowParser.validateRow(lines.get(index));
            if (error == null) {
                validLines.add(lines.get(index));
            } else {
                errors.add("Рядок " + (index + 1) + ": " + error);
            }
        }
        return validLines;
    }
}
