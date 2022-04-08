package gov.nysenate.openleg.legislation;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class SessionYearTest
{
    @Test
    public void basicTest() {
        assertEquals(SessionYear.current(), new SessionYear(LocalDate.now().getYear()));
        assertEquals(new SessionYear(2000), SessionYear.of(2000));
        assertEquals(1999, new SessionYear(1999).year());
        assertEquals(1999, new SessionYear(2000).year());
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroSessionTest() {
        new SessionYear(0);
    }

    @Test
    public void prevAndNextSessionYearTest() {
        var testYear = new SessionYear(1999);
        assertEquals(testYear, new SessionYear(2001).previousSessionYear());
        assertEquals(testYear, new SessionYear(1997).nextSessionYear());
    }

    @Test
    public void testGetStartDateTime() {
        assertEquals(new SessionYear(2000).getStartDateTime(),
                LocalDate.ofYearDay(1999, 1).atStartOfDay());
    }

    @Test
    public void testToString() {
        int year = 2013;
        String yearStr = Integer.toString(year);
        assertEquals(yearStr, SessionYear.of(year).toString());
    }

    @Test
    public void testCompareTo() {
        SessionYear one = new SessionYear(2000);
        SessionYear two = new SessionYear(2002);
        assertTrue(one.compareTo(two) < 0);
        assertTrue(two.compareTo(one) > 0);
    }
}
