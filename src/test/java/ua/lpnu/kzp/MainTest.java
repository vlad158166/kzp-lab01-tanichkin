package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command-line modes of the application entry point. */
class MainTest {
    @TempDir
    Path tempDir;

    @Test
    void printsHelpWithoutProcessingFiles() {
        String output = captureStandardOutput("--help");

        assertTrue(output.contains("Використання"));
        assertTrue(output.contains("--input <файл>"));
    }

    @Test
    void processesCustomInputAndOutput() throws IOException {
        Path input = createReferenceInput();
        Path output = tempDir.resolve("reports").resolve("custom-report.txt");

        String consoleReport = captureStandardOutput(
                "--input", input.toString(), "--output", output.toString());
        String fileReport = Files.readString(output, StandardCharsets.UTF_8);

        assertEquals(fileReport, consoleReport);
        assertTrue(fileReport.contains("Кількість коректних записів: 5"));
        assertTrue(fileReport.contains("Сумарна оцінка годин: 27.50"));
        assertTrue(fileReport.contains("Середній пріоритет: 3.00"));
        assertTrue(fileReport.contains("Кількість виконаних задач: 3"));
    }

    @Test
    void reportsMissingInputValueWithoutException() {
        String error = captureStandardError("--input");

        assertTrue(error.contains("після --input очікується шлях"));
    }

    @Test
    void reportsMissingOutputValueWithoutException() {
        String error = captureStandardError("--output");

        assertTrue(error.contains("після --output очікується шлях"));
    }

    @Test
    void reportsUnknownArgumentWithoutProcessingFiles() {
        String error = captureStandardError("--abc");

        assertTrue(error.contains("Невідомий аргумент: --abc"));
        assertTrue(error.contains("Використайте --help для довідки"));
    }

    @Test
    void acceptsOutputBeforeInput() throws IOException {
        Path input = createReferenceInput();
        Path output = tempDir.resolve("reversed-order-report.txt");

        captureStandardOutput("--output", output.toString(), "--input", input.toString());

        assertTrue(Files.exists(output));
        assertTrue(Files.readString(output, StandardCharsets.UTF_8)
                .contains("Кількість коректних записів: 5"));
    }

    private Path createReferenceInput() throws IOException {
        Path input = tempDir.resolve("input.csv");
        String content = "Розробити авторизацію;Іван Петренко;8.5;3;true%n"
                + "Підготувати документацію;Олена Коваль;4.0;2;false%n"
                + "Протестувати API;Андрій Мельник;6.5;1;true%n"
                + "Налаштувати CI;Марія Бойко;3.0;4;true%n"
                + "Оптимізувати запити;Софія Левченко;5.5;5;false%n";
        Files.writeString(input, content.formatted(), StandardCharsets.UTF_8);
        return input;
    }

    private static String captureStandardOutput(String... args) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (PrintStream captured = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(captured);
            Main.main(args);
        } finally {
            System.setOut(original);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static String captureStandardError(String... args) {
        PrintStream original = System.err;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (PrintStream captured = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setErr(captured);
            Main.main(args);
        } finally {
            System.setErr(original);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }
}
