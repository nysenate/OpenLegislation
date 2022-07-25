package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class HearingAddressParserTest {
    @Test
    public void basicAddressParses() {
        testHearingAddress("01-03-13 Hurricane Sandy Test.txt",
                "Freeport Recreation Center\n130 East Merrick Road\nFreeport, New York 11520");
    }

    @Test
    public void multiWordCityParses() {
        testHearingAddress("01-25-12 Young Roundtable Test.txt",
                "250 Broadway - 19th Floor\nNew York, New York  10007");
    }

    @Test
    public void zipPlusFiveParses() {
        testHearingAddress("09-17-13 Mental Health Test.txt",
                "Ogdensburg City Hall\nCity Council Chambers\n330 Ford Street\nOgdensburg, New York 13669-1626");
    }

    @Test
    public void parsesWithoutStateZipInfo() {
        testHearingAddress("06-04-14 Opioid Addiction Test.txt",
                "Seneca Nation of Indians'\nCattaraugus County Reservation");
    }

    @Test
    public void withDashesParses() {
        testHearingAddress("10-19-16 Hudson Barge Test.txt",
                "Croton-on-Hudson Town Hall\n1 Van Wyck Street\nCroton-on-Hudson, New York 10520");
    }

    private void testHearingAddress(String filename, String expected) {
        Hearing hearing = HearingTestHelper.getHearingFromFilename(filename);
        assertEquals(expected, hearing.getAddress());
    }
}
