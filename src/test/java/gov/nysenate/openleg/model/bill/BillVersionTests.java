package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.base.Version;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BillVersionTests
{
    private static final Logger logger = LoggerFactory.getLogger(BillVersionTests.class);

    @Test
    public void testToString() throws Exception {
        assertEquals("DEFAULT", Version.of(" ").toString());
        assertEquals("A", Version.of("A ").toString());
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals("", Version.of(" ").getValue());
        assertEquals("A", Version.of("A").getValue());
    }

    @Test
    public void testOf_WhitespaceReturnsDefault() throws Exception {
        String s1 = "";
        String s2 = " ";
        String s3 = "      ";
        String s4 = null;
        assertEquals(Version.DEFAULT, Version.of(s1));
        assertEquals(Version.DEFAULT, Version.of(s2));
        assertEquals(Version.DEFAULT, Version.of(s3));
        assertEquals(Version.DEFAULT, Version.of(s4));
    }

    @Test
    public void testGetValue_Succeeds() throws Exception {
        String s1 = "a";
        String s2 = "A   ";
        String s3 = " z";
        String s4 = "  c ";
        assertEquals(Version.A, Version.of(s1));
        assertEquals(Version.A, Version.of(s2));
        assertEquals(Version.Z, Version.of(s3));
        assertEquals(Version.C, Version.of(s4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetValue_Fails() throws Exception {
        String invalid = "z1";
        Version.of(invalid);
    }

    @Test
    public void testComparable() throws Exception {
        List<Version> versions = Arrays.asList(Version.Z, Version.B, Version.DEFAULT, Version.D);
        Collections.sort(versions);
        List<Version> sorted = Arrays.asList(Version.DEFAULT, Version.B, Version.D, Version.Z);
        assertEquals(sorted, versions);
    }

    @Test
    public void testGetVersionsBefore() throws Exception {
        logger.info("{}", Version.before(Version.A).get(0).name());
    }
}
