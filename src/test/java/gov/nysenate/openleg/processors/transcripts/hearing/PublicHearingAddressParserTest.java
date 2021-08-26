package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class PublicHearingAddressParserTest {
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

    //
    @Test
    public void zipPlusFiveParses() {
        testHearingAddress("09-17-13 Carlucci_Mental Health_Ogdensburg_FINAL.txt",
                "Ogdensburg City Hall\nCity Council Chambers\n330 Ford Street\nOgdensburg, New York 13669-1626");
    }

    @Test
    public void parsesWithoutStateZipInfo() {
        testHearingAddress("06-04-14 NYsenate Heroin-Opioid Addiction Special Task Force_Seneca Nation_FINAL.txt",
                "Seneca Nation of Indians'\nCattaraugus County Reservation");
    }

    //
    @Test
    public void withDashesParses() {
        testHearingAddress("10-19-16 NYS Senate Hudson River Barge Hearing FINAL.txt",
                "Croton-on-Hudson Town Hall\n1 Van Wyck Street\nCroton-on-Hudson, New York 10520");
    }

    private void testHearingAddress(String filename, String expected) {
        PublicHearing hearing = PublicHearingTestHelper.getHearingFromFilename(filename);
        assertEquals(expected, hearing.getAddress());
    }
}
