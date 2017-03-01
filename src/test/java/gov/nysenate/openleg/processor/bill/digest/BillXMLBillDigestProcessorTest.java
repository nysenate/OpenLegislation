package gov.nysenate.openleg.processor.bill.digest;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.bill.BillXMLBillDigestProcessor;
import gov.nysenate.openleg.processor.bill.BillXMLBillTextProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by senateuser on 2017/2/28.
 */
public class BillXMLBillDigestProcessorTest  extends BaseXmlProcessorTest {

    @Autowired
    BillDao billDao;
    @Autowired
    BillXMLBillDigestProcessor billXMLBillDigestProcessor;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return billXMLBillDigestProcessor;
    }

    @Test
    public void DigestBillTest() {
        final String path = "processor/bill/digest/2017-01-05-10.29.46.171044_LDSUMM_S00767.XML";
        processXmlFile(path);
        Bill b = billDao.getBill(new BillId("S00767", 2017));
        final String expectedSummary = "test digest bill summary";
        assertEquals(expectedSummary,b.getSummary()); // test summary
        final String expectedLaw = "test digest bill law";
        assertEquals(expectedLaw, b.getAmendment(Version.DEFAULT).getLaw()); // test law
        Set<BillId> preBill = b.getAllPreviousVersions();
        assertTrue(preBill.contains(new BillId("S06883", 2016)));//test pre bill
    }
}
