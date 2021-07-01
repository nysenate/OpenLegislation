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
public class PublicHearingDateTimeParserTest {

    /** Parses the date time string: June 4, 2014 1:00 p.m. to 3:00 p.m. */
    @Test
    public void singleDigitHoursParse() {
        testHearingDate("06-04-14 NYsenate Heroin-Opioid Addiction Special Task Force_Seneca Nation_FINAL.txt",
                LocalDate.of(2014, 6, 4), LocalTime.of(13, 0), LocalTime.of(15, 0));
    }

    /** Parses the date time string: June 2, 2014 10:00 a.m. to 12:00 p.m. */
    @Test
    public void doubleDigitHoursParse() {
        testHearingDate("06-02-14 NYsenate_Labor_Savino_FINAL.txt",
                LocalDate.of(2014, 6, 2), LocalTime.of(10, 0), LocalTime.of(12, 0));
    }

    /** Parses the date time string: Tuesday, August 23, 2011 9:30 a.m. */
    @Test
    public void onlyStartTimeGivenParses() {
        testHearingDate("08-23-11 NYS Senator Ball SCOPED Final.txt",
                LocalDate.of(2011, 8, 23), LocalTime.of(9, 30),null);
    }

    /** Parses the date time string: Albany, New York March 12, 2014, at 10:00 a.m. */
    @Test
    public void dateTimeOnSingleLineParses() {
        testHearingDate("03-12-14 Roundtable on the Compassionate Care Act_Savino_FINAL.txt",
                LocalDate.of(2014, 3, 12), LocalTime.of(10, 0),null);
    }

    /** Parses the date time string: January 25, 2012 Afternoon Session */
    @Test
    public void noTimeParses() {
        testHearingDate("01-25-12 Young_Roundtable_III_Final.txt",
                LocalDate.of(2012, 1, 25), null,null);
    }

    /** Parses the date time string: August 22, 2013 11:00 a.m. <96> 4:00 p.m. */
    @Test
    public void invalidCharactersParse() {
        testHearingDate("08-22-13 NYSSenateHearing_Buffalo_Martins_FINAL.txt",
                LocalDate.of(2013, 8, 22), LocalTime.of(11, 0), LocalTime.of(16, 0));
    }

    /** Parses the date time string: May 9, 2012 2:30 p.m. to - 5:30 p.m. */
    @Test
    public void erroneousCharactersParse() {
        testHearingDate("05-09-12 NYS SenateAgriculture_Ritchie_Final.txt",
                LocalDate.of(2012, 5, 9), LocalTime.of(14, 30), LocalTime.of(17, 30));
    }

    @Test
    public void dateTimeLabelsTest() {
        testHearingDate("04-26-19 NYS Joint Farmworkers Hearing Long Island FINAL.txt",
                LocalDate.of(2019, 4, 26), LocalTime.of(14, 30),null);
    }

    @Test
    public void alternateEndTimeTest() {
        testHearingDate("05-18-11 ValeskyAgingCommitteeRoundtableFINAL.txt",
                LocalDate.of(2011, 5, 18), LocalTime.of(9, 0), LocalTime.of(10, 29));
    }

    @Test
    public void wrongFormatTest() {
        testHearingDate("8-25-20 MTA Transcript.txt", LocalDate.of(2020, 8, 25), LocalTime.of(10, 0),
                LocalTime.of(15, 30));
    }

    @Test
    public void noDateTest() {
        assertThrows(ParseError.class, () -> testHearingDate("noDate.txt", null, null, null));
    }

    private void testHearingDate(String filename, LocalDate expectedDate, LocalTime expectedStartTime,
                                 LocalTime expectedEndTime) {
        var hearing = PublicHearingTestHelper.getHearingFromFilename(filename);
        assertEquals(expectedDate, hearing.getDate());
        assertEquals(expectedStartTime, hearing.getStartTime());
        assertEquals(expectedEndTime, hearing.getEndTime());
    }
}