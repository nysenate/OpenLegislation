package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.BaseTests;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PublicHearingDateParserTest extends BaseTests {

    private PublicHearingDateParser dateParser;

    @Before
    public void setup() {
        dateParser = new PublicHearingDateParser();
    }

    /** Parses the date time string: June 4, 2014 1:00 p.m. to 3:00 p.m. */
    @Test
    public void singleDigitHoursParse() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "06-04-14 NYsenate Heroin-Opioid Addiction Special Task Force_Seneca Nation_FINAL.txt");

        LocalDate expectedDate = LocalDate.of(2014, 6, 4);
        LocalTime expectedStartTime = LocalTime.of(13, 00);
        LocalTime expectedEndTime = LocalTime.of(15, 00);

        LocalDate actualDate = dateParser.parseDate(pages.get(0));
        LocalTime actualStartTime = dateParser.parseStartTime(pages.get(0));
        LocalTime actualEndTime = dateParser.parseEndTime(pages.get(0));

        assertThat(actualDate, is(expectedDate));
        assertThat(actualStartTime, is(expectedStartTime));
        assertThat(actualEndTime, is(expectedEndTime));
    }

    /** Parses the date time string: June 2, 2014 10:00 a.m. to 12:00 p.m. */
    @Test
    public void doubleDigitHoursParse() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "06-02-14 NYsenate_Labor_Savino_FINAL.txt");

        LocalDate expectedDate = LocalDate.of(2014, 6, 2);
        LocalTime expectedStartTime = LocalTime.of(10, 00);
        LocalTime expectedEndTime = LocalTime.of(12, 00);

        LocalDate actualDate = dateParser.parseDate(pages.get(0));
        LocalTime actualStartTime = dateParser.parseStartTime(pages.get(0));
        LocalTime actualEndTime = dateParser.parseEndTime(pages.get(0));

        assertThat(actualDate, is(expectedDate));
        assertThat(actualStartTime, is(expectedStartTime));
        assertThat(actualEndTime, is(expectedEndTime));
    }

    /** Parses the date time string: Tuesday, August 23, 2011 9:30 a.m. */
    @Test
    public void onlyStartTimeGivenParses() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "08-23-11 NYS Senator Ball SCOPED Final.txt");

        LocalDate expectedDate = LocalDate.of(2011, 8, 23);
        LocalTime expectedStartTime = LocalTime.of(9, 30);
        LocalTime expectedEndTime = null;

        LocalDate actualDate = dateParser.parseDate(pages.get(0));
        LocalTime actualStartTime = dateParser.parseStartTime(pages.get(0));
        LocalTime actualEndTime = dateParser.parseEndTime(pages.get(0));

        assertThat(actualDate, is(expectedDate));
        assertThat(actualStartTime, is(expectedStartTime));
        assertThat(actualEndTime, is(expectedEndTime));
    }

    /** Parses the date time string: Albany, New York March 12, 2014, at 10:00 a.m. */
    @Test
    public void dateTimeOnSingleLineParses() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "03-12-14 Roundtable on the Compassionate Care Act_Savino_FINAL.txt");

        LocalDate expectedDate = LocalDate.of(2014, 3, 12);
        LocalTime expectedStartTime = LocalTime.of(10, 00);
        LocalTime expectedEndTime = null;

        LocalDate actualDate = dateParser.parseDate(pages.get(0));
        LocalTime actualStartTime = dateParser.parseStartTime(pages.get(0));
        LocalTime actualEndTime = dateParser.parseEndTime(pages.get(0));

        assertThat(actualDate, is(expectedDate));
        assertThat(actualStartTime, is(expectedStartTime));
        assertThat(actualEndTime, is(expectedEndTime));
    }

    /** Parses the date time string: January 25, 2012 Afternoon Session */
    @Test
    public void noTimeParses() throws IOException, URISyntaxException, ParseException {
        // 01-03-13 HurricaneSandy_NYS TaskForce Roundtable_Final.txt
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "01-25-12 Young_Roundtable_III_Final.txt");

        LocalDate expectedDate = LocalDate.of(2012, 1, 25);
        LocalTime expectedStartTime = null;
        LocalTime expectedEndTime = null;

        LocalDate actualDate = dateParser.parseDate(pages.get(0));
        LocalTime actualStartTime = dateParser.parseStartTime(pages.get(0));
        LocalTime actualEndTime = dateParser.parseEndTime(pages.get(0));

        assertThat(actualDate, is(expectedDate));
        assertThat(actualStartTime, is(expectedStartTime));
        assertThat(actualEndTime, is(expectedEndTime));
    }


    /** Parses the date time string: August 22, 2013 11:00 a.m. <96> 4:00 p.m. */
    @Test
    public void invalidCharactersParse() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "08-22-13 NYSSenateHearing_Buffalo_Martins_FINAL.txt");

        LocalDate expectedDate = LocalDate.of(2013, 8, 22);
        LocalTime expectedStartTime = LocalTime.of(11, 00);
        LocalTime expectedEndTime = LocalTime.of(16, 00);

        LocalDate actualDate = dateParser.parseDate(pages.get(0));
        LocalTime actualStartTime = dateParser.parseStartTime(pages.get(0));
        LocalTime actualEndTime = dateParser.parseEndTime(pages.get(0));

        assertThat(actualDate, is(expectedDate));
        assertThat(actualStartTime, is(expectedStartTime));
        assertThat(actualEndTime, is(expectedEndTime));
    }

    /** Parses the date time string: May 9, 2012 2:30 p.m. to - 5:30 p.m. */
    @Test
    public void erroneousCharactersParse() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "05-09-12 NYS SenateAgriculture_Ritchie_Final.txt");

        LocalDate expectedDate = LocalDate.of(2012, 5, 9);
        LocalTime expectedStartTime = LocalTime.of(14, 30);
        LocalTime expectedEndTime = LocalTime.of(17, 30);

        LocalDate actualDate = dateParser.parseDate(pages.get(0));
        LocalTime actualStartTime = dateParser.parseStartTime(pages.get(0));
        LocalTime actualEndTime = dateParser.parseEndTime(pages.get(0));

        assertThat(actualDate, is(expectedDate));
        assertThat(actualStartTime, is(expectedStartTime));
        assertThat(actualEndTime, is(expectedEndTime));
    }
}