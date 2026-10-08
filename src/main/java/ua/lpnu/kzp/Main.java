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
 * prints it to the console, and writes it to a file.
 */
public final class Main {
    private Main() {
    }

    /**
     * Prints generated build information for {@code --version}; without arguments, processes the
     * default CSV input and creates the default report file.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length == 1 && "--version".equals(args[0])) {
            printVersion();
            return;
        }

        if (args.length == 0) {
            runDefaultProcessing();
            return;
        }

        System.err.printf(Locale.ROOT,
                "Підтримується запуск без аргументів або аргумент --version.%n");
    }

    private static void runDefaultProcessing() {
        Path input = Path.of("data", "input.csv");
        Path output = Path.of("out", "report.txt");
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

    private static void printVersion() {
        Properties buildInfo = BuildInfo.load();
        System.out.printf(Locale.ROOT, "%s %s%n",
                buildInfo.getProperty("product.name"), buildInfo.getProperty("product.version"));
        System.out.printf(Locale.ROOT, "build %s%n", buildInfo.getProperty("ci.build.number"));
    }
}
