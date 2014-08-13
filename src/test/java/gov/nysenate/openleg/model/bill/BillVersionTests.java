package gov.nysenate.openleg.model.bill;

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
        assertEquals("DEFAULT", BillVersion.of(" ").toString());
        assertEquals("A", BillVersion.of("A ").toString());
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals("", BillVersion.of(" ").getValue());
        assertEquals("A", BillVersion.of("A").getValue());
    }

    @Test
    public void testOf_WhitespaceReturnsDefault() throws Exception {
        String s1 = "";
        String s2 = " ";
        String s3 = "      ";
        String s4 = null;
        assertEquals(BillVersion.DEFAULT, BillVersion.of(s1));
        assertEquals(BillVersion.DEFAULT, BillVersion.of(s2));
        assertEquals(BillVersion.DEFAULT, BillVersion.of(s3));
        assertEquals(BillVersion.DEFAULT, BillVersion.of(s4));
    }

    @Test
    public void testGetValue_Succeeds() throws Exception {
        String s1 = "a";
        String s2 = "A   ";
        String s3 = " z";
        String s4 = "  c ";
        assertEquals(BillVersion.A, BillVersion.of(s1));
        assertEquals(BillVersion.A, BillVersion.of(s2));
        assertEquals(BillVersion.Z, BillVersion.of(s3));
        assertEquals(BillVersion.C, BillVersion.of(s4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetValue_Fails() throws Exception {
        String invalid = "z1";
        BillVersion.of(invalid);
    }

    @Test
    public void testComparable() throws Exception {
        List<BillVersion> versions = Arrays.asList(BillVersion.Z, BillVersion.B, BillVersion.DEFAULT, BillVersion.D);
        Collections.sort(versions);
        List<BillVersion> sorted = Arrays.asList(BillVersion.DEFAULT, BillVersion.B, BillVersion.D, BillVersion.Z);
        assertEquals(sorted, versions);
    }
}
