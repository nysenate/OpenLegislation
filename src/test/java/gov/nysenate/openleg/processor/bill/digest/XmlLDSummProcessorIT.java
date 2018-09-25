package gov.nysenate.openleg.processor.bill.digest;

import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.bill.XmlLDSummProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by senateuser on 2017/2/28.
 */
@Category(IntegrationTest.class)
public class XmlLDSummProcessorIT extends BaseXmlProcessorTest {

    @Autowired
    BillDataService billDataService;
    @Autowired
    XmlLDSummProcessor xmlLDSummProcessor;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return xmlLDSummProcessor;
    }

    @Test
    public void replaceDigestBillTest() {
        final BaseBillId baseBillId = new BaseBillId("S00767", 2017);
        final String path = "processor/bill/digest/2017-01-05-10.29.46.171044_LDSUMM_S00767.XML";

        // Expected data
        final String expectedSummary = "test digest bill summary";
        final String expectedLaw = "test digest bill law";
        final BillId expectedPreviousVersion = new BillId("S06883", 2016);

        // Assert bill is not initialized with expected data
        if (doesBillExist(baseBillId)) {
            Bill bill = getBill(baseBillId);
            assertNotEquals(expectedSummary, bill.getSummary());
            assertNotEquals(expectedLaw, bill.getAmendment(Version.ORIGINAL).getLaw());
            assertFalse(bill.getAllPreviousVersions().contains(expectedPreviousVersion));
        }

        processXmlFile(path);
        Bill bill = getBill(baseBillId);

        assertEquals(expectedSummary, bill.getSummary());
        assertEquals(expectedLaw, bill.getAmendment(Version.ORIGINAL).getLaw());
        assertTrue(bill.getAllPreviousVersions().contains(expectedPreviousVersion));
    }

    @Test
    public void removeDigestBillTest() {
        //create bill
        final String createBillPath = "processor/bill/digest/2017-01-05-10.29.46.171044_LDSUMM_S00767.XML";
        processXmlFile(createBillPath);

        //remove bill
        final String path = "processor/bill/digest/2017-01-23-12.24.20.161955_LDSUMM_A02830.XML";
        processXmlFile(path);

        Bill b = billDataService.getBill(new BaseBillId("S00767", 2017));

        assertEquals("", b.getSummary());
        assertEquals("", b.getAmendment(Version.ORIGINAL).getLaw());
        assertTrue(b.getAllPreviousVersions().isEmpty());
    }
}
