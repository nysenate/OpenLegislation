package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class PublicHearingTitleParserTest {
    @Test
    public void basicTest() {
        testTitle("03-12-14 Compassionate Care Act Test.txt",
                "ROUNDTABLE DISCUSSION ON THE COMPASSIONATE CARE ACT");
    }

    @Test
    public void forumTest() {
        testTitle("05-18-11 Valesky Aging Test.txt",
                "NEW YORK STATE FORUM/TOWN HALL ROUNDTABLE ON THE SAGE COMMISSION'S " +
                          "PROPOSAL TO MERGE THE NYS OFFICE FOR THE AGING WITH THE DEPARTMENT OF HEALTH");
    }

    @Test
    public void noHostsTest() {
        testTitle("01-03-13 Hurricane Sandy Test.txt",
                "ROUNDTABLE DISCUSSION HELD BY THE NEW YORK STATE SENATE " +
                          "BIPARTISAN TASK FORCE FOR \"HURRICANE SANDY\" RECOVERY");
    }

    @Test
    public void jointHearingTest() {
        testTitle("1-30-20 Human Services Test.txt",
                "JOINT LEGISLATIVE HEARING In the Matter of the 2020-2021 EXECUTIVE BUDGET ON HUMAN SERVICES");
    }

    private static void testTitle(String filename, String expected) {
        PublicHearing hearing = PublicHearingTestHelper.getHearingFromFilename(filename);
        assertEquals(expected, hearing.getTitle());
    }
}
