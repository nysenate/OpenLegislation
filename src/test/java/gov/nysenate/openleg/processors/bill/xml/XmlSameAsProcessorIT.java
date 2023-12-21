package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
import gov.nysenate.openleg.processors.ParseError;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by uros on 2/16/17.
 */
@Category(IntegrationTest.class)
public class XmlSameAsProcessorIT extends BaseXmlProcessorTest {

    @Autowired private BillDataService billDataService;

    @Test
    public void processSimpleSameAs() {
        String xmlPath = "processor/bill/sameas/2017-02-09-12.48.56.095416_SAMEAS_A05457.XML";
        processXmlFile(xmlPath);

        Bill bill = billDataService.getBill(new BaseBillId("A5457", 2017));
        BillAmendment amendment = bill.getAmendment(Version.ORIGINAL);
        assertTrue(amendment.getSameAs().contains(new BaseBillId("S1329", 2017)));
        assertEquals(1, amendment.getSameAs().size());
    }

    @Test
    public void shouldClearPreviousSameAs() {
        // Initializing with another same as.
        String xmlPath = "processor/bill/sameas/2017-02-09-12.48.56.095416_SAMEAS_A05457-2.XML";
        processXmlFile(xmlPath);
        // Process new same as info.
        xmlPath = "processor/bill/sameas/2017-02-09-12.48.56.095416_SAMEAS_A05457.XML";
        processXmlFile(xmlPath);

        // Test same as equals new same as.
        Bill bill = billDataService.getBill(new BaseBillId("A5457", 2017));
        BillAmendment amendment = bill.getAmendment(Version.ORIGINAL);
        assertTrue(amendment.getSameAs().contains(new BaseBillId("S1329", 2017)));
        assertEquals(1, amendment.getSameAs().size());
    }

    @Test
    public void removeBIll()    {
        // Initializing with another same as.
        String xmlPath = "processor/bill/sameas/2017-02-09-12.48.56.095416_SAMEAS_A05457.XML";
        processXmlFile(xmlPath);

        // Removing same as
        xmlPath = "processor/bill/sameas/2017-02-09-12.48.35.004442_SAMEAS_A05454-remove.XML";
        processXmlFile(xmlPath);

        Bill bill = billDataService.getBill(new BaseBillId("A5457",2017));
        BillAmendment amendment = bill.getAmendment(Version.ORIGINAL);
        assertEquals(0, amendment.getSameAs().size());
    }

    @Test
    public void testUniBill()  {
        String xmlPath = "processor/bill/sameas/2017-02-07-13.47.57.288703_SAMEAS_S04257.XML";
        processXmlFile(xmlPath);

        Bill bill = billDataService.getBill(new BaseBillId("S4257", 2017));
        BillAmendment amendment = bill.getAmendment(Version.ORIGINAL);
        assertTrue(amendment.getSameAs().contains(new BaseBillId("A5261", 2017)));
        assertEquals(1, amendment.getSameAs().size());
    }

    @Test
    public void testMultipleSameAs()    {
        String xmlPath = "processor/bill/sameas/2017-02-09-12.48.56.095416_SAMEAS_A05457-multiple-sameas.XML";
        processXmlFile(xmlPath);

        Bill bill = billDataService.getBill(new BaseBillId("A5457", 2017));
        BillAmendment amendment = bill.getAmendment(Version.ORIGINAL);
        assertTrue(amendment.getSameAs().contains(new BaseBillId("S1329", 2017)));
        assertTrue(amendment.getSameAs().contains(new BaseBillId("S3779",2017)));
        assertEquals(2, amendment.getSameAs().size());
    }

    @Test(expected = ParseError.class)
    public void textExceptions()   {
        String xmlPath = "processor/bill/sameas/2017-02-01-09.31.43.383879_SAMEAS_S03526A-invalid-xml.XML";
        processXmlFile(xmlPath);
    }

    @Test
    public void testSameAsWithAmendment()   {
        String xmlPath = "processor/bill/sameas/2017-02-01-09.31.43.383879_SAMEAS_S03526A-with-amendment.XML";
        processXmlFile(xmlPath);

        Bill bill = billDataService.getBill(new BaseBillId("S3526",2017));
        BillAmendment amendment = bill.getAmendment(Version.A);
        assertTrue(amendment.getSameAs().contains(new BillId("A3028A", 2017)));
        assertEquals(1, amendment.getSameAs().size());
    }
}