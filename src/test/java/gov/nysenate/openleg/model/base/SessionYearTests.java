package gov.nysenate.openleg.model.base;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SessionYearTests
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
        String year = "2013";
        assertEquals(year, SessionYear.current().toString());
    }
}
