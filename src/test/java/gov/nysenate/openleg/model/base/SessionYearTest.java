package gov.nysenate.openleg.model.base;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class SessionYearTest
{
    @Test
    public void testGetYear() {
        SessionYear sy = new SessionYear(2013);
        assertEquals(2013, sy.getYear());
        sy = new SessionYear(2014);
        assertEquals(2013, sy.getYear());
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroSessionTest() {
        new SessionYear(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeSessionTest() {
        new SessionYear(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDateTest() {
        new SessionYear((LocalDate) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDateTimeTest() {
        new SessionYear((LocalDate) null);
    }

    @Test
    public void testConstructors() {
        LocalDateTime now = LocalDateTime.now();
        SessionYear[] years = {new SessionYear(), new SessionYear(now.getYear()),
                new SessionYear(now.toLocalDate()), new SessionYear(now)};
        for (int i = 0; i < years.length; i++)
            assertEquals(years[i%years.length], years[(i+1)%years.length]);
    }

    @Test
    public void testGetSessionStartYear() {
        assertEquals(2015, new SessionYear(2015).getSessionStartYear());
    }

    @Test
    public void testGetSessionEndYear() {
        assertEquals(2016, new SessionYear(2015).getSessionEndYear());
    }

    @Test
    public void testCompareTo() {
        SessionYear one = new SessionYear(2009);
        SessionYear two = new SessionYear(2011);
        assertTrue(one.compareTo(two) < 0);
        assertTrue(two.compareTo(one) > 0);
    }

    @Test
    public void testToString() {
        int year = 2013;
        String yearStr = Integer.toString(year);
        assertEquals(yearStr, SessionYear.of(year).toString());
    }
}
