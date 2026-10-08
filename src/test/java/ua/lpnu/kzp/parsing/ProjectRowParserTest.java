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

    @Test
    void acceptsValidEstimateHours() {
        assertTrue(ProjectRowParser.isValidEstimateHours("8.5"));
        assertTrue(ProjectRowParser.isValidEstimateHours("0"));
        assertTrue(ProjectRowParser.isValidEstimateHours(" 4.0 "));
    }

    @Test
    void rejectsInvalidEstimateHours() {
        assertFalse(ProjectRowParser.isValidEstimateHours("-1.0"));
        assertFalse(ProjectRowParser.isValidEstimateHours("abc"));
        assertFalse(ProjectRowParser.isValidEstimateHours(null));
        assertFalse(ProjectRowParser.isValidEstimateHours(""));
        assertFalse(ProjectRowParser.isValidEstimateHours("   "));
    }

    @Test
    void acceptsIntegerPriorityWithoutRangeValidation() {
        assertTrue(ProjectRowParser.isValidPriority("3"));
        assertTrue(ProjectRowParser.isValidPriority(" 5 "));
        assertTrue(ProjectRowParser.isValidPriority("0"));
        assertTrue(ProjectRowParser.isValidPriority("-1"));
    }

    @Test
    void rejectsInvalidPriority() {
        assertFalse(ProjectRowParser.isValidPriority("2.5"));
        assertFalse(ProjectRowParser.isValidPriority("high"));
        assertFalse(ProjectRowParser.isValidPriority(null));
        assertFalse(ProjectRowParser.isValidPriority(""));
    }
}
