package gov.nysenate.openleg.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import gov.nysenate.openleg.model.SOBIBlock;
import gov.nysenate.openleg.processors.BillProcessor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class BillProcessorTests
{
    @Test
    public void testGetBlocks() throws IOException
    {
        BillProcessor processor = new BillProcessor();
        List<SOBIBlock> blocks = processor.getBlocks(new File("src/test/resources/BLOCKTEST.txt"));
        Iterator<SOBIBlock> blockIterator = blocks.iterator();
        assertEquals(new SOBIBlock("2013A03006D1Budget              00000"), blockIterator.next());
        assertEquals(new SOBIBlock("2013A03006D1Budget              00000"), blockIterator.next());
        assertEquals(new SOBIBlock("2013A03006D2Budget Bills"), blockIterator.next());
        assertEquals(new SOBIBlock("2013A03006D2Budget Bills"), blockIterator.next());
        assertEquals(new SOBIBlock("2013A03006D5Same As S04643C"), blockIterator.next());
        assertEquals(new SOBIBlock("2013A03006D5Same As S04643C"), blockIterator.next());
        assertEquals(new SOBIBlock("2013A03006D3Amends various provisions of law relating to implementing the health and mental hygiene budget for\n" +
        		                                                 "the 2013-2014 state fiscal year"),blockIterator.next());
        assertEquals(new SOBIBlock("2013A03006D401/22/13 referred to ways and means\n" +
        		"02/13/13 amend and recommit to ways and means\n" +
        		"02/13/13 print number 3006a\n" +
        		"02/22/13 amend (t) and recommit to ways and means\n" +
        		"02/22/13 print number 3006b\n" +
        		"03/08/13 amend (t) and recommit to ways and means"),blockIterator.next());

        assertFalse(blockIterator.hasNext());
    }

    @Test
    void testGetOrCreateBill() throws Exception
    {

    }

    @Test
    void testDeleteBill() throws Exception
    {

    }

    @Test
    void testSaveBill() throws Exception
    {

    }

    @Test
    public void testProcess() throws IOException
    {

    }


}
