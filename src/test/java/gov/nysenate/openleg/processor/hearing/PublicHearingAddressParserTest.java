package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.BaseTests;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PublicHearingAddressParserTest extends BaseTests
{

    private PublicHearingAddressParser addressParser;

    @Before
    public void setup() {
        addressParser = new PublicHearingAddressParser();
    }

    @Test
    public void basicAddressParses() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "01-17-13 NYSenateHearing_Marcellino_Final.txt");

        String expected = "Legislative & Executive Chamber\nNassau County Office Building\n1550 Franklin Avenue\nMineola, New York 11501";
        String actual = addressParser.parse(pages.get(0));
        assertThat(actual, is(expected));

        // Line numbers aligned differently
        pages = PublicHearingTestHelper.getPagesFromFileName(
                "01-03-13 HurricaneSandy_NYS TaskForce Roundtable_Final.txt");

        expected = "Freeport Recreation Center\n130 East Merrick Road\nFreeport, New York 11520";
        actual = addressParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void multiWordCityParses() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "01-25-12 Young_Roundtable_III_Final.txt");

        String expected = "250 Broadway - 19th Floor\nNew York, New York  10007";
        String actual = addressParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void extraSpacesParses() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "02-04-13 NYSJudiciaryHearing_Bonacic FINAL.txt");

        String expected = "New York State Capitol Building\n172 State Street, Room 124 CAP\nAlbany, New York  12247";
        String actual = addressParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void zipPlusFiveParses() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "09-17-13 Carlucci_Mental Health_Ogdensburg_FINAL.txt");

        String expected = "Ogdensburg City Hall\nCity Council Chambers\n330 Ford Street\nOgdensburg, New York 13669-1626";
        String actual = addressParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void noZipCodeParses() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "09-12-13 NYSsenate_DeFrancisco_Buffalo_FINAL.txt");

        String expected = "Buffalo City Hall\nCommon Council Chambers, 13th Floor\n65 Niagara Square\nBuffalo, New York";
        String actual = addressParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void parsesWithoutStateZipInfo() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "06-04-14 NYsenate Heroin-Opioid Addiction Special Task Force_Seneca Nation_FINAL.txt");

        String expected = "Seneca Nation of Indians'\nCattaraugus County Reservation";
        String actual = addressParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void stateAbbreviationParses() throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "10-10-13 NYsenate_Fuschillo_MTA_FINAL.txt");

        String expected = "Senate Majority Office\n250 Broadway, Suite 2034\nNew York, NY 10007-2375";
        String actual = addressParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }
}