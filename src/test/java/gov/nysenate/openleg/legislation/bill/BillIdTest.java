package gov.nysenate.openleg.legislation.bill;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class BillIdTest
{
    @Test
    public void testBillIdConstructor() {
        assertEquals("S1234-2013", new BillId("S1234", 2013).toString());

        BillId id = new BillId("S1234a", 2013);
        assertEquals("S1234", id.getBasePrintNo());
        assertEquals(Version.A, id.getVersion());

        id = new BillId("S214-A", 2013);
        assertEquals("S214", id.getBasePrintNo());
        assertEquals(Version.A, id.getVersion());

        id = new BillId("S02143", 2013, "a");
        assertEquals("S2143", id.getBasePrintNo());
        assertEquals(2013, id.getSession().year());
        assertEquals(Version.A, id.getVersion());

        id = new BillId("A1133", new SessionYear(2019), null);
        assertEquals(id.getVersion(), Version.ORIGINAL);
    }

    @Test
    public void testGetBaseBillId() {
        BillId id = new BillId("A1133", 2019, "A");
        BaseBillId baseId = new BaseBillId(id.getBasePrintNo(), id.getSession());
        assertEquals(baseId, BillId.getBaseId(id));
    }

    @Test
    public void testGetters() {
        BaseBillId baseId = new BaseBillId("A9618", 2015);
        BillId id = new BillId(baseId, Version.O);
        assertEquals(BillType.A, id.getBillType());
        assertEquals(Chamber.ASSEMBLY, id.getChamber());
        assertEquals(9618, id.getNumber());
        assertFalse(BillId.isBaseVersion(id.getVersion()));
        assertEquals("A09618O-2015", id.getPaddedBillIdString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBillIdConstructor_emptyPrintNo() {
        new BillId("", 2013);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBillIdConstructor_nullPrintNo() {
        new BillId(null, 2015);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBillIdConstructor_oneLetterPrintNo() {
        new BillId("A", 2019);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBillIdConstructor_missingDesignator() {
        new BillId("1234", 2013);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBillIdConstructor_badBaseId() {
        new BillId("S1234A", new SessionYear(2015), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBillIdConstructor_missingSessionYear() {
        new BillId("S1234", null, null);
    }

    @Test
    public void testEquals() {
        BaseBillId baseId1 = new BaseBillId("S1234", 2013);
        assertFalse(baseId1.equalsBase(null));
        BaseBillId baseId2 = new BaseBillId("S1234", 2013);
        assertEquals(baseId1, baseId2);

        BillId id1 = new BillId(baseId1, Version.L);
        assertTrue(id1.equalsBase(baseId1));
        BillId id2 = new BillId("S1111", 2013);
        assertNotEquals(id1, id2);
        BillId id3 = new BillId("S1111", 2015);
        assertNotEquals(id2, id3);
    }
}