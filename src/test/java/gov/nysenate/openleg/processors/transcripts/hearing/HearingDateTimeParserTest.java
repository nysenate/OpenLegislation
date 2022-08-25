package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.processors.ParseError;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@Category(UnitTest.class)
public class HearingDateTimeParserTest {
    @Test
    public void singleDigitHoursParse() {
        testHearingDate("06-04-14 Opioid Addiction Test.txt", LocalDate.of(2014, 6, 4),
                LocalTime.of(13, 0), LocalTime.of(15, 0));
    }

    @Test
    public void dateTimeOnSingleLineParses() {
        testHearingDate("03-12-14 Compassionate Care Act Test.txt", LocalDate.of(2014, 3, 12),
                LocalTime.of(10, 0),null);
    }

    @Test
    public void noTimeParses() {
        testHearingDate("01-25-12 Young Roundtable Test.txt", LocalDate.of(2012, 1, 25),
                null,null);
    }

    @Test
    public void invalidCharactersParse() {
        testHearingDate("08-22-13 Buffalo Martins Test.txt", LocalDate.of(2013, 8, 22),
                LocalTime.of(11, 0), LocalTime.of(16, 0));
    }

    @Test
    public void dateTimeLabelsTest() {
        testHearingDate("04-26-19 Joint Farmworkers Test.txt", LocalDate.of(2019, 4, 26),
                LocalTime.of(14, 30),null);
    }

    @Test
    public void alternateEndTimeTest() {
        testHearingDate("05-18-11 Valesky Aging Test.txt", LocalDate.of(2011, 5, 18),
                LocalTime.of(9, 0), LocalTime.of(10, 29));
    }

    @Test
    public void wrongFormatTest() {
        testHearingDate("8-25-20 MTA Transcript Test.txt", LocalDate.of(2020, 8, 25),
                LocalTime.of(10, 0), LocalTime.of(15, 30));
    }

    @Test
    public void noDateTest() {
        assertThrows(ParseError.class, () -> testHearingDate("No Date Test.txt", null, null, null));
    }

    private static void testHearingDate(String filename, LocalDate expectedDate,
                                 LocalTime expectedStartTime, LocalTime expectedEndTime) {
        var hearing = HearingTestHelper.getHearingFromFilename(filename);
        assertEquals(expectedDate, hearing.getDate());
        assertEquals(expectedStartTime, hearing.getStartTime());
        assertEquals(expectedEndTime, hearing.getEndTime());
    }
}
