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
        testHearingAddress("01-17-13 NYSenateHearing_Marcellino_Final.txt",
                "Legislative & Executive Chamber\nNassau County Office Building\n1550 Franklin Avenue\nMineola, New York 11501");
        // Line numbers aligned differently
        testHearingAddress("01-03-13 HurricaneSandy_NYS TaskForce Roundtable_Final.txt",
                "Freeport Recreation Center\n130 East Merrick Road\nFreeport, New York 11520");
    }

    @Test
    public void multiWordCityParses() {
        testHearingAddress("01-25-12 Young_Roundtable_III_Final.txt",
                "250 Broadway - 19th Floor\nNew York, New York  10007");
    }

    @Test
    public void extraSpacesParses() {
        testHearingAddress("02-04-13 NYSJudiciaryHearing_Bonacic FINAL.txt",
                "New York State Capitol Building\n172 State Street, Room 124 CAP\nAlbany, New York  12247");
    }

    @Test
    public void zipPlusFiveParses() {
        testHearingAddress("09-17-13 Carlucci_Mental Health_Ogdensburg_FINAL.txt",
                "Ogdensburg City Hall\nCity Council Chambers\n330 Ford Street\nOgdensburg, New York 13669-1626");
    }

    @Test
    public void noZipCodeParses() {
        testHearingAddress("09-12-13 NYSsenate_DeFrancisco_Buffalo_FINAL.txt",
                "Buffalo City Hall\nCommon Council Chambers, 13th Floor\n65 Niagara Square\nBuffalo, New York");
    }

    @Test
    public void parsesWithoutStateZipInfo() {
        testHearingAddress("06-04-14 NYsenate Heroin-Opioid Addiction Special Task Force_Seneca Nation_FINAL.txt",
                "Seneca Nation of Indians'\nCattaraugus County Reservation");
    }

    @Test
    public void stateAbbreviationParses() {
        testHearingAddress("10-10-13 NYsenate_Fuschillo_MTA_FINAL.txt",
                "Senate Majority Office\n250 Broadway, Suite 2034\nNew York, NY 10007-2375");
    }

    @Test
    public void noZipParses() {
        testHearingAddress("10-10-17 NYS Senate Flooding_IJC Plan 2014 FINAL.txt",
                "Mexico High School Auditorium\n3338 Main Street, Mexico, New York");
    }

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
