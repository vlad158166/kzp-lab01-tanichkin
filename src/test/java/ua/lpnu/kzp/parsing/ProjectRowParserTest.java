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

    @Test
    void acceptsRowWithExactlyFiveFields() {
        assertTrue(ProjectRowParser.hasCorrectFieldCount("Task;User;3.5;2;true"));
    }

    @Test
    void rejectsRowsWithFourOrSixFields() {
        assertFalse(ProjectRowParser.hasCorrectFieldCount("Task;User;3.5;2"));
        assertFalse(ProjectRowParser.hasCorrectFieldCount("Task;User;3.5;2;true;extra"));
    }

    @Test
    void keepsEmptyLastFieldWhenCounting() {
        assertTrue(ProjectRowParser.hasCorrectFieldCount("Task;User;3.5;2;"));
    }

    @Test
    void rejectsNullAndEmptyRows() {
        assertFalse(ProjectRowParser.hasCorrectFieldCount(null));
        assertFalse(ProjectRowParser.hasCorrectFieldCount(""));
    }

    @Test
    void identifiesNonBlankText() {
        assertFalse(ProjectRowParser.isNonBlank(null));
        assertFalse(ProjectRowParser.isNonBlank(""));
        assertFalse(ProjectRowParser.isNonBlank("   "));
        assertTrue(ProjectRowParser.isNonBlank("Задача"));
        assertTrue(ProjectRowParser.isNonBlank(" Іван Петренко "));
    }
}
