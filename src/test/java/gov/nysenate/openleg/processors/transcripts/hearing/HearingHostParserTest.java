package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Set;

import static gov.nysenate.openleg.legislation.committee.Chamber.ASSEMBLY;
import static gov.nysenate.openleg.legislation.committee.Chamber.SENATE;
import static gov.nysenate.openleg.legislation.transcripts.hearing.HearingHostType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class HearingHostParserTest {

    @Test
    public void veteransTest() {
        String block = """
                SENATE STANDING COMMITTEE ON VETERANS,
                HOMELAND SECURITY & MILITARY AFFAIRS
                ASSEMBLY STANDING COMMITTEE ON VETERANS' AFFAIRS
                ASSEMBLY SUBCOMMITTEE ON WOMEN VETERANS
                """;
        HearingHost[] expected = {new HearingHost(SENATE, COMMITTEE, "VETERANS, HOMELAND SECURITY AND MILITARY AFFAIRS"),
        new HearingHost(ASSEMBLY, COMMITTEE, "VETERANS' AFFAIRS"),
                new HearingHost(ASSEMBLY, COMMITTEE, "WOMEN VETERANS")};
        hearingHostTestHelper(block, expected);
    }

    @Test
    public void newlineTest() {
        String block = "SENATE STANDING COMMITTEE ON INVESTIGATIONS AND\nGOVERNMENT OPERATIONS";
        HearingHost expected = new HearingHost(SENATE, COMMITTEE, "INVESTIGATIONS AND GOVERNMENT OPERATIONS");
        hearingHostTestHelper(block, expected);
    }

    @Test
    public void sandyTest() {
        String block = """
                ROUNDTABLE DISCUSSION HELD BY
                THE NEW YORK STATE SENATE
                BIPARTISAN TASK FORCE FOR "HURRICANE SANDY" RECOVERY;""";
        HearingHost expected = new HearingHost(SENATE, TASK_FORCE, "\"HURRICANE SANDY\" RECOVERY");
        hearingHostTestHelper(block, expected);
    }

    @Test
    public void multipleTypeParse() {
        String block = "BEFORE THE NEW YORK STATE SENATE MAJORITY COALITION\n" +
                "JOINT TASK FORCE ON HEROIN AND OPIOID ADDICTION";
        var expected = new HearingHost(SENATE, TASK_FORCE, "HEROIN AND OPIOID ADDICTION");
        hearingHostTestHelper(block, expected);
    }

    /**
     * Test that we can parse multiple committees information from hearing files sent
     * to us by the Senate.
     */
    @Test
    public void multipleCommitteesParse() {
        String sharedTitle = "MENTAL HEALTH AND ENVIRONMENTAL DISABILITIES";
        var expectedCommittee1 = new HearingHost(ASSEMBLY, COMMITTEE, sharedTitle);
        var expectedCommittee2 = new HearingHost(SENATE, COMMITTEE, sharedTitle);
        var expectedCommittee3 = new HearingHost(SENATE, COMMITTEE, "HEALTH");
        String block = """
                NEW YORK STATE LEGISLATURE JOINT HEARING BEFORE
                THE NEW YORK STATE ASSEMBLY STANDING COMMITTEE ON
                MENTAL HEALTH AND ENVIRONMENTAL DISABILITIES,
                AND
                THE SENATE STANDING COMMITTEE ON MENTAL HEALTH AND
                ENVIRONMENTAL DISABILITIES,
                AND
                THE SENATE STANDING COMMITTEE ON HEALTH""";
        hearingHostTestHelper(block, expectedCommittee1, expectedCommittee2, expectedCommittee3);
    }

    /**
     * Test that we can parse multiple committees information from hearing files sent
     * to us by the Assembly.
     */
    @Test
    public void budgetHearing() {
        String block = "BEFORE THE NEW YORK STATE SENATE FINANCE\n" +
                "AND WAYS AND MEANS COMMITTEES";
        var com1 = new HearingHost(SENATE, COMMITTEE, "FINANCE");
        var com2 = new HearingHost(ASSEMBLY, COMMITTEE, "WAYS AND MEANS");
        hearingHostTestHelper(block, com1, com2);

        block = """
                NEW YORK STATE
                2016 ECONOMIC AND REVENUE CONSENSUS
                FORECASTING CONFERENCE""";
        hearingHostTestHelper(block, com1, com2);
    }

    @Test
    public void noCommitteesTest() {
        String block = "BEFORE THE NEW YORK STATE SENATE";
        hearingHostTestHelper(block, new HearingHost(SENATE, WHOLE_CHAMBER, ""));
    }

    @Test
    public void majorityCoalitionParses() {
        String block = "BEFORE THE NEW YORK STATE SENATE MAJORITY COALITION";
        var expected = new HearingHost(SENATE, MAJORITY_COALITION, "");
        hearingHostTestHelper(block, expected);
    }

    @Test
    public void legislativeCommissionTest() {
        String block = """
                NEW YORK JOINT LEGISLATURE
                LEGISLATIVE TASK FORCE ON
                DEMOGRAPHIC RESEARCH AND REAPPORTIONMENT
                SENATE STANDING COMMITTEE ON THE JUDICIARY
                ASSEMBLY STANDING COMMITTEE ON GOVERNMENTAL OPERATIONS""";
        hearingHostTestHelper(block, new HearingHost(SENATE, TASK_FORCE, "DEMOGRAPHIC RESEARCH AND REAPPORTIONMENT"),
                new HearingHost(ASSEMBLY, TASK_FORCE, "DEMOGRAPHIC RESEARCH AND REAPPORTIONMENT"),
                new HearingHost(SENATE, COMMITTEE, "JUDICIARY"),
                new HearingHost(ASSEMBLY, COMMITTEE, "GOVERNMENTAL OPERATIONS"));
    }

    @Test
    public void stateLabelTest() {
        String block = """
                BEFORE THE NEW YORK STATE SENATE
                STANDING COMMITTEE ON HOUSING, CONSTRUCTION, AND
                COMMUNITY DEVELOPMENT
                AND
                STANDING COMMITTEE ON INVESTIGATIONS AND
                GOVERNMENT OPERATIONS
                AND
                NYS SENATE STANDING COMMITTEE ON CONSUMER PROTECTION""";
        HearingHost[] hosts = {new HearingHost(SENATE, COMMITTEE, "HOUSING, CONSTRUCTION, AND COMMUNITY DEVELOPMENT"),
        new HearingHost(SENATE, COMMITTEE, "INVESTIGATIONS AND GOVERNMENT OPERATIONS"),
        new HearingHost(SENATE, COMMITTEE, "CONSUMER PROTECTION")};
        hearingHostTestHelper(block, hosts);
    }

    private void hearingHostTestHelper(String block, HearingHost... expected) {
        Set<HearingHost> actual = HearingHostParser.parse(block);
        assertEquals(expected.length, actual.size());
        for (var host : expected)
            assertTrue(actual.contains(host));
    }
}
