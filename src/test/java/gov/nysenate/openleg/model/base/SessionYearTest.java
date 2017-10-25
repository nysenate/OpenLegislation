package gov.nysenate.openleg.model.base;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class SessionYearTest
{
    @Test
    public void testGetYear() throws Exception {
        SessionYear sy = new SessionYear(2013);
        assertEquals(2013, sy.getYear());
        sy = new SessionYear(2014);
        assertEquals(2013, sy.getYear());
    }

    @Test
    public void testGetSessionStartYear() throws Exception {

    }

    @Test
    public void testGetSessionEndYear() throws Exception {

    }

    @Test
    public void testCompareTo() throws Exception {

    }

    @Test
    public void testToString() throws Exception {
        Integer year = 2013;
        String yearStr = year.toString();
        assertEquals(yearStr, SessionYear.of(year).toString());
    }
}
