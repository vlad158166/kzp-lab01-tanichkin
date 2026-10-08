package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import org.junit.jupiter.api.Test;

/** Smoke test that verifies Maven resource filtering for local builds. */
class BuildInfoTest {
    @Test
    void buildMetadataContainsProductVersionAndConfiguredBuildNumber() {
        Properties buildInfo = BuildInfo.load();
        String expectedBuildNumber = System.getProperty("ci.build.number", "local");

        assertEquals("kzp-lab01-tanichkin", buildInfo.getProperty("product.name"));
        assertEquals("1.0.0", buildInfo.getProperty("product.version"));
        assertEquals(expectedBuildNumber, buildInfo.getProperty("ci.build.number"));
    }
}
