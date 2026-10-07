package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Properties;

/** Entry point for the Lab 01 executable JAR infrastructure skeleton. */
public final class Main {
    private Main() {
    }

    /**
     * Prints infrastructure help or generated build information.
     * Subject data processing is intentionally not implemented at this checkpoint.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length == 1 && "--version".equals(args[0])) {
            printVersion();
            return;
        }
        System.out.printf(Locale.ROOT,
                "Infrastructure skeleton is ready. Subject processing will be added after the coding checkpoint.%n");
    }

    private static void printVersion() {
        Properties buildInfo = BuildInfo.load();
        System.out.printf(Locale.ROOT, "%s %s%n",
                buildInfo.getProperty("product.name"), buildInfo.getProperty("product.version"));
        System.out.printf(Locale.ROOT, "build %s%n", buildInfo.getProperty("ci.build.number"));
    }
}
