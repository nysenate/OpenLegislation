package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.BaseTests;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PublicHearingDateParserTest extends BaseTests {

    private PublicHearingDateParser dateParser;

    @Before
    public void setup() {
        dateParser = new PublicHearingDateParser();
    }

    @Test
    public void singleDigitHoursParse() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "06-04-14 NYsenate Heroin-Opioid Addiction Special Task Force_Seneca Nation_FINAL.txt");

        LocalDateTime expected = LocalDateTime.of(2014, 6, 4, 13, 00);
        LocalDateTime actual = dateParser.parse(pages.get(0));

        assertThat(actual, is(expected));
    }


    @Test
    public void doubleDigitHoursParse() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "06-02-14 NYsenate_Labor_Savino_FINAL.txt");

        LocalDateTime expected = LocalDateTime.of(2014, 6, 2, 10, 00);
        LocalDateTime actual = dateParser.parse(pages.get(0));

        assertThat(actual, is(expected));
    }

    @Test
    public void onlyStartTimeGivenParses() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "08-23-11 NYS Senator Ball SCOPED Final.txt");

        LocalDateTime expected = LocalDateTime.of(2011, 8, 23, 9, 30);
        LocalDateTime actual = dateParser.parse(pages.get(0));

        assertThat(actual, is(expected));
    }

    @Test
    public void dateTimeOnSingleLineParses() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "03-12-14 Roundtable on the Compassionate Care Act_Savino_FINAL.txt");

        LocalDateTime expected = LocalDateTime.of(2014, 3, 12, 10, 00);
        LocalDateTime actual = dateParser.parse(pages.get(0));

        assertThat(actual, is(expected));
    }

    @Test
    public void noTimeParses() throws IOException, URISyntaxException, ParseException {
        // 01-03-13 HurricaneSandy_NYS TaskForce Roundtable_Final.txt
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "01-25-12 Young_Roundtable_III_Final.txt");

        LocalDateTime expected = LocalDateTime.of(2012, 1, 25, 0, 0);
        LocalDateTime actual = dateParser.parse(pages.get(0));

        assertThat(actual, is(expected));
    }

    @Test
    public void invalidCharactersParse() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "08-22-13 NYSSenateHearing_Buffalo_Martins_FINAL.txt");

        LocalDateTime expected = LocalDateTime.of(2013, 8, 22, 11, 00);
        LocalDateTime actual = dateParser.parse(pages.get(0));

        assertThat(actual, is(expected));
    }

    @Test
    public void erroneousCharactersParse() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "05-09-12 NYS SenateAgriculture_Ritchie_Final.txt");

        LocalDateTime expected = LocalDateTime.of(2012, 5, 9, 14, 30);
        LocalDateTime actual = dateParser.parse(pages.get(0));

        assertThat(actual, is(expected));
    }
}