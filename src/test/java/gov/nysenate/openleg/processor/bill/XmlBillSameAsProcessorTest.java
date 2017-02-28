package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by uros on 2/16/17.
 */
@Transactional
public class XmlBillSameAsProcessorTest extends BaseXmlProcessorTest {

    @Autowired private BillDao billDao;
    @Autowired private XmlBillSameAsProcessor sameAsProcessor;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return sameAsProcessor;
    }

    @Test
    public void processSimpleSameAs() {
        String xmlPath = "processor/bill/sameas/2017-02-09-12.48.56.095416_SAMEAS_A05457.XML";
        processXmlFile(xmlPath);

        Bill bill = billDao.getBill(new BillId("A5457", 2017));
        BillAmendment amendment = bill.getAmendment(Version.DEFAULT);
        assertTrue(amendment.getSameAs().contains(new BillId("S1329", 2017)));
        assertTrue(amendment.getSameAs().size() == 1);
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
        Bill bill = billDao.getBill(new BillId("A5457", 2017));
        BillAmendment amendment = bill.getAmendment(Version.DEFAULT);
        assertTrue(amendment.getSameAs().contains(new BillId("S1329", 2017)));
        assertTrue(amendment.getSameAs().size() == 1);
    }

}