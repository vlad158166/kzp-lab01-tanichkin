package ua.lpnu.kzp.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests UTF-8 writing of prepared project-task processing reports. */
class ProjectReportWriterTest {
    @TempDir
    Path tempDir;

    @Test
    void writesReportToExistingTemporaryDirectory() throws IOException {
        Path output = tempDir.resolve("report.txt");
        String report = "Report text";

        ProjectReportWriter.writeReport(output, report);

        assertTrue(Files.exists(output));
        assertEquals(report, Files.readString(output, StandardCharsets.UTF_8));
    }

    @Test
    void createsMissingNestedOutputDirectory() throws IOException {
        Path output = tempDir.resolve("out").resolve("report.txt");
        String report = "Nested report";

        ProjectReportWriter.writeReport(output, report);

        assertTrue(Files.isDirectory(output.getParent()));
        assertEquals(report, Files.readString(output, StandardCharsets.UTF_8));
    }

    @Test
    void preservesUkrainianReportTextInUtf8() throws IOException {
        Path output = tempDir.resolve("ukrainian-report.txt");
        String report = "Результати обробки проєктних задач\nКількість виконаних задач: 3";

        ProjectReportWriter.writeReport(output, report);

        assertEquals(report, Files.readString(output, StandardCharsets.UTF_8));
    }

    @Test
    void writesExactlyTheProvidedReportString() throws IOException {
        Path output = tempDir.resolve("exact-report.txt");
        String report = "First line\n\nSecond line\n";

        ProjectReportWriter.writeReport(output, report);

        assertEquals(report, Files.readString(output, StandardCharsets.UTF_8));
    }
}
