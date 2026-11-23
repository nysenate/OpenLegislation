package gov.nysenate.openleg.common.util;

import com.google.common.collect.Range;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {
    private static final LocalDate MAY_22 = LocalDate.of(2026, 5, 22);
    private static final LocalDate MAY_25 = LocalDate.of(2026, 5, 25);

    @Test
    public void closedBoundsAreKept() {
        Range<LocalDate> range = Range.closed(MAY_22, MAY_25);
        assertEquals(MAY_22, DateUtils.startOfDateRange(range));
        assertEquals(MAY_25, DateUtils.endOfDateRange(range));
    }

    @Test
    public void openBoundsMoveInByOneDay() {
        Range<LocalDate> range = Range.open(MAY_22, MAY_25);
        assertEquals(LocalDate.of(2026, 5, 23), DateUtils.startOfDateRange(range));
        assertEquals(LocalDate.of(2026, 5, 24), DateUtils.endOfDateRange(range));
    }

    @Test
    public void missingBoundsUseTheReferenceDates() {
        Range<LocalDate> range = Range.all();
        assertEquals(LocalDate.of(1970, 1, 1), DateUtils.startOfDateRange(range));
        assertEquals(LocalDate.of(2999, 12, 31), DateUtils.endOfDateRange(range));
    }

    @Test
    public void emptyClosedOpenRangeEndsBeforeItStarts() {
        Range<LocalDate> range = Range.closedOpen(MAY_22, MAY_22);
        assertEquals(MAY_22, DateUtils.startOfDateRange(range));
        assertEquals(LocalDate.of(2026, 5, 21), DateUtils.endOfDateRange(range));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullStartRangeIsRejected() {
        DateUtils.startOfDateRange(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullEndRangeIsRejected() {
        DateUtils.endOfDateRange(null);
    }
}
