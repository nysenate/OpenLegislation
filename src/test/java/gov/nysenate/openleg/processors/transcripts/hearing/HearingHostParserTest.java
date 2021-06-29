package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static gov.nysenate.openleg.legislation.transcripts.hearing.HearingHostType.COMMITTEE;
import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class HearingHostParserTest {
    private static final String S = "Senate", A = "Assembly";

    @Test
    public void veteransTest() {
        String block = """
                      SENATE STANDING COMMITTEE ON VETERANS,
                       HOMELAND SECURITY & MILITARY AFFAIRS
                ASSEMBLY STANDING COMMITTEE ON VETERANS' AFFAIRS
                     ASSEMBLY SUBCOMMITTEE ON WOMEN VETERANS
                """;
        HearingHost[] expected = {new HearingHost("Senate", COMMITTEE, "VETERANS, HOMELAND SECURITY & MILITARY AFFAIRS"),
        new HearingHost("Assembly", COMMITTEE, "VETERANS' AFFAIRS"),
                new HearingHost("Assembly", COMMITTEE, "Women Veterans")};
        List<HearingHost> actual = HearingHostParser.parse(block);
        for (int i = 0; i < 3; i++)
            assertEquals(expected[i], actual.get(i));
    }

    @Test
    public void newlineTest() {
        String block = "SENATE STANDING COMMITTEE ON INVESTIGATIONS AND\n" +
                "GOVERNMENT OPERATIONS";
        HearingHost expected = new HearingHost("Senate", COMMITTEE, "INVESTIGATIONS AND GOVERNMENT OPERATIONS");
        assertEquals(expected, HearingHostParser.parse(block).get(0));
    }

    @Test
    public void sandyTest() {
        String block = """

                1      ROUNDTABLE DISCUSSION HELD BY
                       THE NEW YORK STATE SENATE
                2      BIPARTISAN TASK FORCE FOR "HURRICANE SANDY" RECOVERY;""".indent(7);
        HearingHost expected = new HearingHost("Senate", COMMITTEE, "\"HURRICANE SANDY RECOVERY\"");
        assertEquals(expected, HearingHostParser.parse(block).get(0));
    }

    @Test
    public void basicCommitteeParse() {
        var expectedCommittee = new HearingHost("S", COMMITTEE, "LABOR");
        hearingHostTestHelper("06-02-14 NYsenate_Labor_Savino_FINAL.txt", expectedCommittee);
    }

    @Test
    public void basicTaskForceParse() {
        var expectedCommittee = new HearingHost("S", COMMITTEE, "HEROIN AND OPIOID ADDICTION");
        hearingHostTestHelper("06-04-14 NYsenate Heroin-Opioid Addiction Special Task Force_Seneca Nation_FINAL.txt",
                expectedCommittee);
    }

    /**
     * Test that we can parse multiple committees information from public hearing files sent
     * to us by the Senate.
     */
    @Test
    public void multipleCommitteesParse() {
        String sharedTitle = "MENTAL HEALTH AND ENVIRONMENTAL DISABILITIES";
        var expectedCommittee1 = new HearingHost("A", COMMITTEE, sharedTitle);
        var expectedCommittee2 = new HearingHost("S", COMMITTEE, sharedTitle);
        var expectedCommittee3 = new HearingHost("S", COMMITTEE, "HEALTH");
        hearingHostTestHelper("09-17-13 Carlucci_Mental Health_Ogdensburg_FINAL.txt", expectedCommittee1,
                expectedCommittee2, expectedCommittee3);
    }

    /**
     * Test that we can parse multiple committees information from public hearing files sent
     * to us by the Assembly.
     */
    @Test
    public void parsesMultipleCommitteesInAssemblyFiles() {
        var com1 = new HearingHost("S", COMMITTEE, "FINANCE");
        var com2 = new HearingHost("A", COMMITTEE, "WAYS AND MEANS");
        hearingHostTestHelper("2-4-20 Higher Education Transcript.txt", com1, com2);

        com1 = new HearingHost("S", COMMITTEE, "FINANCE");
        com2 = new HearingHost("A", COMMITTEE, "WAYS AND MEANS");
        hearingHostTestHelper("1-30-20 Human Services Transcript.txt", com1, com2);
    }

    @Test
    public void noWhiteSpaceInCommitteeName() {
        var expectedCommittee1 = new HearingHost("S", COMMITTEE, "SOCIAL SERVICES");
        var expectedCommittee2 = new HearingHost("S", COMMITTEE, "CHILDREN AND FAMILIES");
        hearingHostTestHelper("02-09-12 ChildCareHearing_Final.txt", expectedCommittee1, expectedCommittee2);
    }

    @Test
    public void noCommitteesTest() {
        // TODO: what do
    }

    // TODO: don't use files!
    @Test
    public void majorityCoalitionParses() {
        var expectedCommittee = new HearingHost("S", COMMITTEE, "MAJORITY COALITION");
        hearingHostTestHelper("09-11-13 NYS Majority Coalition Forum_Buffalo_ FINAL.txt", expectedCommittee);
    }

    private void hearingHostTestHelper(String block, HearingHost... expected) {
        List<HearingHost> actual = HearingHostParser.parse(block);
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < actual.size(); i++)
            assertEquals(expected[i], actual.get(i));
    }
}
