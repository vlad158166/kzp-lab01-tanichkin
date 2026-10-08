package ua.lpnu.kzp.parsing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests strict validation of the done field. */
class ProjectRowParserTest {
    @Test
    void rejectsNullAndEmptyValues() {
        assertFalse(ProjectRowParser.isValidDoneValue(null));
        assertFalse(ProjectRowParser.isValidDoneValue(""));
    }

    @Test
    void acceptsTrimmedTrueValue() {
        assertTrue(ProjectRowParser.isValidDoneValue(" true "));
    }

    @Test
    void acceptsFalseIgnoringCase() {
        assertTrue(ProjectRowParser.isValidDoneValue("FALSE"));
    }

    @Test
    void rejectsOtherText() {
        assertFalse(ProjectRowParser.isValidDoneValue("yes"));
    }
}
