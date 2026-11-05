package ua.lpnu.kzp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the immutable aggregate semantics of {@link EstimatePriority}. */
class EstimatePriorityTest {
    @Test
    void storesAggregateComponentsIncludingZeroBoundary() {
        EstimatePriority aggregate = new EstimatePriority(0.0, 0);

        assertEquals(0.0, aggregate.totalEstimateHours(), 0.0001);
        assertEquals(0, aggregate.totalPriority());
    }

    @Test
    void usesStandardRecordValueSemantics() {
        EstimatePriority first = new EstimatePriority(27.5, 15);
        EstimatePriority second = new EstimatePriority(27.5, 15);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals("EstimatePriority[totalEstimateHours=27.5, totalPriority=15]",
                first.toString());
    }

    @Test
    void rejectsInvalidTotalEstimateHours() {
        assertThrows(IllegalArgumentException.class, () -> new EstimatePriority(-0.1, 0));
        assertThrows(IllegalArgumentException.class, () -> new EstimatePriority(Double.NaN, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new EstimatePriority(Double.POSITIVE_INFINITY, 0));
    }

    @Test
    void rejectsNegativeTotalPriority() {
        assertThrows(IllegalArgumentException.class, () -> new EstimatePriority(1.0, -1));
    }
}
