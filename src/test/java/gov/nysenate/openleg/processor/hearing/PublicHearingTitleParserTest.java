package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.BaseTests;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PublicHearingTitleParserTest extends BaseTests
{

    private PublicHearingTitleParser titleParser;

    @Before
    public void setup() {
        titleParser = new PublicHearingTitleParser();
    }

    @Test
    public void basicTitleParses() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "03-12-14 Roundtable on the Compassionate Care Act_Savino_FINAL.txt");

        String expected = "ROUNDTABLE DISCUSSION ON THE COMPASSIONATE CARE ACT";
        String actual = titleParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void forumTownHallTitleParses() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "02-09-12 ChildCareHearing_Final.txt");

        String expected = "FORUM/TOWN HALL: HUMAN SERVICES FORUM ON THE CURRENT AND FUTURE " +
                          "ISSUES AND CONCERNS OF HUMAN SERVICES ADMINISTRATORS, ADVOCATES AND CLIENTS";
        String actual = titleParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void newYorkStateForumTownHallTitleParses() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "05-18-11 ValeskyAgingCommitteeRoundtableFINAL.txt");

        String expected = "NEW YORK STATE FORUM/TOWN HALL ROUNDTABLE ON THE SAGE COMMISSION'S " +
                          "PROPOSAL TO MERGE THE NYS OFFICE FOR THE AGING WITH THE DEPARTMENT OF HEALTH";
        String actual = titleParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void senateHearingTitleParses() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "05-23-11 NY Senate Flanagan Education Hearing FINAL_AMENDED COVER SHEET ONLY.txt");

        String expected = "A NEW YORK STATE SENATE HEARING " +
                          "DUE PROCESS TEACHER DISCIPLINE WITHOUT DELAY: " +
                          "REFORMING SECTION 3020-A OF THE EDUCATION LAW " +
                          "TO MEET THE NEEDS OF THE 21st CENTURY";
        String actual = titleParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void titleFoundIfCommitteeMissing() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "01-03-13 HurricaneSandy_NYS TaskForce Roundtable_Final.txt");

        String expected = "ROUNDTABLE DISCUSSION HELD BY THE NEW YORK STATE SENATE " +
                          "BIPARTISAN TASK FORCE FOR \"HURRICANE SANDY\" RECOVERY";
        String actual = titleParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }

    @Test
    public void conferenceTitleParses() throws IOException, URISyntaxException, ParseException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(
                "02-28-13 2013 RevenueConsensusConference_Final.txt");

        String expected = "NEW YORK STATE 2013 ECONOMIC AND REVENUE " +
                          "CONSENSUS FORECASTING CONFERENCE";
        String actual = titleParser.parse(pages.get(0));
        assertThat(actual, is(expected));
    }
}