package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.base.Version;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BillIdTests
{
    @Test
    public void testBillIdConstructor() throws Exception {
//        assertEquals("S1234-2013", new BillId("S1234", 2013).toString());

        BillId id = new BillId("S1234a", 2013);
        assertEquals("S1234", id.getBasePrintNo());
        assertEquals(Version.A, id.getVersion());

        id = new BillId("S214-A", 2013);
        assertEquals("S214", id.getBasePrintNo());
        assertEquals(Version.A, id.getVersion());

        id = new BillId("S0002143", 2013, "a");
        assertEquals("S2143", id.getBasePrintNo());
        assertEquals(2013, id.getSession().getYear());
        assertEquals(Version.A, id.getVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBillIdConstructor_emptyPrintNo() throws Exception {
        new BillId("", 2013);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBillIdConstructor_missingDesignator() throws Exception {
        new BillId("1234", 2013);
    }

    @Test
    public void testEquals() throws Exception {
        BillId id = new BillId("S1234A", 2013);
        BaseBillId id2 = new BaseBillId("S1234", 2013);
        BaseBillId id3 = new BaseBillId("S1234", 2013);
        assertEquals(id3.hashCode(), id2.hashCode());
    }
}