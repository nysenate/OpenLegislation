package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.base.Version;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class BillVersionTest
{
    @Test
    public void testToString() {
        assertEquals("", Version.of(" ").toString());
        assertEquals("A", Version.of("A ").toString());
    }

    @Test
    public void testName() {
        assertEquals("ORIGINAL", Version.of(" ").name());
        assertEquals("A", Version.of("A").name());
    }

    @Test
    public void testOf_WhitespaceReturnsDefault() {
        String s1 = "";
        String s2 = " ";
        String s3 = "      ";
        String s4 = null;
        assertEquals(Version.ORIGINAL, Version.of(s1));
        assertEquals(Version.ORIGINAL, Version.of(s2));
        assertEquals(Version.ORIGINAL, Version.of(s3));
        assertEquals(Version.ORIGINAL, Version.of(s4));
    }

    @Test
    public void testGetValue_Succeeds() {
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
    public void testGetValue_Fails() {
        String invalid = "z1";
        Version.of(invalid);
    }

    @Test
    public void testComparable() {
        List<Version> versions = Arrays.asList(Version.Z, Version.B, Version.ORIGINAL, Version.D);
        Collections.sort(versions);
        List<Version> sorted = Arrays.asList(Version.ORIGINAL, Version.B, Version.D, Version.Z);
        assertEquals(sorted, versions);
    }

    @Test
    public void testGetVersionsBefore() {
        assertEquals(Version.before(Version.A).get(0), Version.ORIGINAL);
    }
}
