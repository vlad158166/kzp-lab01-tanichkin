package ua.lpnu.kzp.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Writes a prepared project-task processing report to a UTF-8 text file. */
public final class ProjectReportWriter {
    private ProjectReportWriter() {
    }

    /**
     * Creates the output directory when necessary and writes the report in UTF-8.
     *
     * @param output path of the report file
     * @param report prepared report text
     * @throws IOException if the output directory or file cannot be written
     */
    public static void writeReport(Path output, String report) throws IOException {
        Path parent = output.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(output, report, StandardCharsets.UTF_8);
    }
}
