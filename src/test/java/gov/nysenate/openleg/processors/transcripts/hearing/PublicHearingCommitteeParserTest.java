package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingCommittee;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class PublicHearingCommitteeParserTest {
    private static final String SENATE = "SENATE", ASSEMBLY = "ASSEMBLY";

    @Test
    public void basicCommitteeParse() throws IOException, URISyntaxException {
        var expectedCommittee = new PublicHearingCommittee("LABOR", SENATE);
        committeeTestHelper("06-02-14 NYsenate_Labor_Savino_FINAL.txt", expectedCommittee);
    }

    @Test
    public void basicTaskForceParse() throws IOException, URISyntaxException {
        var expectedCommittee = new PublicHearingCommittee("HEROIN AND OPIOID ADDICTION", SENATE);
        committeeTestHelper("06-04-14 NYsenate Heroin-Opioid Addiction Special Task Force_Seneca Nation_FINAL.txt",
                expectedCommittee);
    }

    /**
     * Test that we can parse multiple committees information from public hearing files sent
     * to us by the Senate.
     */
    @Test
    public void multipleCommitteesParse() throws IOException, URISyntaxException {
        String sharedTitle = "MENTAL HEALTH AND ENVIRONMENTAL DISABILITIES";
        var expectedCommittee1 = new PublicHearingCommittee(sharedTitle, ASSEMBLY);
        var expectedCommittee2 = new PublicHearingCommittee(sharedTitle, SENATE);
        var expectedCommittee3 = new PublicHearingCommittee("HEALTH", SENATE);
        committeeTestHelper("09-17-13 Carlucci_Mental Health_Ogdensburg_FINAL.txt", expectedCommittee1,
                expectedCommittee2, expectedCommittee3);
    }

    /**
     * Test that we can parse multiple committees information from public hearing files sent
     * to us by the Assembly.
     */
    @Test
    public void parsesMultipleCommitteesInAssemblyFiles() throws IOException, URISyntaxException {
        var com1 = new PublicHearingCommittee("FINANCE", SENATE);
        var com2 = new PublicHearingCommittee("WAYS AND MEANS", ASSEMBLY);
        committeeTestHelper("2-4-20 Higher Education Transcript.txt", com1, com2);

        com1 = new PublicHearingCommittee("FINANCE", SENATE);
        com2 = new PublicHearingCommittee("WAYS AND MEANS", ASSEMBLY);
        committeeTestHelper("1-30-20 Human Services Transcript.txt", com1, com2);
    }

    @Test
    public void noWhiteSpaceInCommitteeName() throws IOException, URISyntaxException {
        var expectedCommittee1 = new PublicHearingCommittee("SOCIAL SERVICES", SENATE);
        var expectedCommittee2 = new PublicHearingCommittee("CHILDREN AND FAMILIES", SENATE);
        committeeTestHelper("02-09-12 ChildCareHearing_Final.txt", expectedCommittee1, expectedCommittee2);
    }

    @Test
    public void noCommitteeParses() throws IOException, URISyntaxException {
        // Empty committee list.
        committeeTestHelper("03-12-14 Roundtable on the Compassionate Care Act_Savino_FINAL.txt");
    }

    @Test
    public void majorityCoalitionParses() throws IOException, URISyntaxException {
        var expectedCommittee = new PublicHearingCommittee("MAJORITY COALITION", SENATE);
        committeeTestHelper("09-11-13 NYS Majority Coalition Forum_Buffalo_ FINAL.txt", expectedCommittee);
    }

    private void committeeTestHelper(String hearingTitle, PublicHearingCommittee... expected)
            throws IOException, URISyntaxException {
        List<List<String>> pages = PublicHearingTestHelper.getPagesFromFileName(hearingTitle);
        assertEquals(List.of(expected), PublicHearingCommitteeParser.parse(pages.get(0)));
    }
}