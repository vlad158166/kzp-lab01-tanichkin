package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import ua.lpnu.kzp.io.ProjectFileReader;
import ua.lpnu.kzp.io.ProjectReportWriter;
import ua.lpnu.kzp.report.ProjectReportFormatter;

/**
 * Runs Lab 01 processing: reads and validates a CSV file, calculates metrics, formats a report,
 * prints it to the console, and writes it to a file. It supports {@code --help}, {@code --version},
 * {@code --input <file>}, and {@code --output <file>}.
 */
public final class Main {
    private static final Path DEFAULT_INPUT = Path.of("data", "input.csv");
    private static final Path DEFAULT_OUTPUT = Path.of("out", "report.txt");

    private Main() {
    }

    /**
     * Runs the selected command mode or processes the default/custom input and output paths.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length == 1 && "--help".equals(args[0])) {
            printHelp();
            return;
        }

        if (args.length == 1 && "--version".equals(args[0])) {
            printVersion();
            return;
        }

        Path input = DEFAULT_INPUT;
        Path output = DEFAULT_OUTPUT;

        for (int index = 0; index < args.length; index++) {
            String argument = args[index];
            if ("--input".equals(argument)) {
                if (index + 1 >= args.length) {
                    System.err.printf(Locale.ROOT, "Помилка аргументів: після --input очікується шлях.%n");
                    return;
                }
                input = Path.of(args[++index]);
            } else if ("--output".equals(argument)) {
                if (index + 1 >= args.length) {
                    System.err.printf(Locale.ROOT, "Помилка аргументів: після --output очікується шлях.%n");
                    return;
                }
                output = Path.of(args[++index]);
            } else {
                System.err.printf(Locale.ROOT, "Невідомий аргумент: %s%n", argument);
                System.err.printf(Locale.ROOT, "Використайте --help для довідки.%n");
                return;
            }
        }

        runProcessing(input, output);
    }

    /**
     * Executes the complete processing pipeline for a CSV input and a report output path.
     * Validation errors are collected without stopping processing of valid rows. Any I/O failure is
     * handled here by printing a concise message for the user.
     *
     * @param input path to the input CSV file
     * @param output path to the output report file
     */
    private static void runProcessing(Path input, Path output) {
        List<String> errors = new ArrayList<>();

        try {
            List<String> validLines = ProjectFileReader.readValidLines(input, errors);
            String report = ProjectReportFormatter.formatReport(validLines, errors);
            System.out.print(report);
            ProjectReportWriter.writeReport(output, report);
        } catch (IOException exception) {
            System.err.printf(Locale.ROOT, "Помилка роботи з файлом: %s%n", exception.getMessage());
        }
    }

    private static void printHelp() {
        System.out.printf(Locale.ROOT,
                "Використання:%n"
                        + "  java -jar kzp-lab01-tanichkin-1.0.0.jar [опції]%n%n"
                        + "Опції:%n"
                        + "  --help            Показати довідку%n"
                        + "  --version         Показати версію та номер збірки%n"
                        + "  --input <файл>    Шлях до вхідного CSV%n"
                        + "  --output <файл>   Шлях до вихідного звіту%n");
    }

    private static void printVersion() {
        Properties buildInfo = BuildInfo.load();
        System.out.printf(Locale.ROOT, "%s %s%n",
                buildInfo.getProperty("product.name"), buildInfo.getProperty("product.version"));
        System.out.printf(Locale.ROOT, "build %s%n", buildInfo.getProperty("ci.build.number"));
    }
}
