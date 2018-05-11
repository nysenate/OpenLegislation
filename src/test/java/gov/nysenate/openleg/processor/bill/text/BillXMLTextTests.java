package gov.nysenate.openleg.processor.bill.text;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.bill.XmlBillTextProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by senateuser on 2017/2/28.
 */
public class BillXMLTextTests extends BaseXmlProcessorTest {

    @Autowired
    BillDao billDao;
    @Autowired
    XmlBillTextProcessor xmlBillTextProcessor;
    @Override

    protected SobiProcessor getSobiProcessor() {
        return xmlBillTextProcessor;
    }

    @Test
    public void replaceSenateBill(){
        final String path = "processor/bill/text/2017-02-09-12.41.27.159140_BILLTEXT_S04325.XML";
        final String expectedS = "replace senate bill";
        final BillId billId = new BillId("S04325", 2017);

        assertNotEquals("New text value not yet set", expectedS, getFullTextSafe(billId));
        processXmlFile(path);
        assertEquals("New text value set", expectedS, getFullText(billId));
    }

    @Test
    public void replaceAssemblyBill(){
        final String path = "processor/bill/text/2017-02-09-14.26.07.392403_BILLTEXT_A05464.XML";
        final String expectedS = "replace assembly bill";
        final BillId billId = new BillId("A05464", 2017);

        assertNotEquals("New text value not yet set", expectedS, getFullTextSafe(billId));
        processXmlFile(path);
        assertEquals("New text value set", expectedS, getFullText(billId));
    }

    @Test
    public void replaceUniBill(){
        final String path = "processor/bill/text/2017-02-01-11.40.11.918032_BILLTEXT_S03526A.XML";
        final String expectedText = "replace uni bill";
        final BillId senateBillId = new BillId("S03526A", 2017);
        final BillId asmBillId = new BillId("A03028A", 2017);

        assertNotEquals("New text value not yet set", expectedText, getFullTextSafe(senateBillId));
        assertNotEquals("New text value not yet set", expectedText, getFullTextSafe(asmBillId));
        processXmlFile(path);
        assertEquals("New text value set", expectedText, getFullText(senateBillId));
        assertEquals("New text value set", expectedText, getFullText(asmBillId));
    }

    @Test
    public void removeSenateBill(){
        final String preProcessPath = "processor/bill/text/2017-01-31-00.00.00.000000_BILLTEXT_S03954.XML";
        final String path = "processor/bill/text/2017-01-31-10.28.19.879620_BILLTEXT_S03954.XML";
        final BillId billId = new BillId("S03954", 2017);
        // Add text to remove
        processXmlFile(preProcessPath);
        assertTrue("Pre processed text is not blank", StringUtils.isNotBlank(getFullText(billId)));
        processXmlFile(path);
        assertTrue("Post processed text is blank", StringUtils.isBlank(getFullText(billId)));
    }

    @Test
    public void removeAssemblyBill(){
        final String preProcessPath = "processor/bill/text/2017-01-31-00.00.00.000000_BILLTEXT_A04029.XML";
        final String path = "processor/bill/text/2017-01-31-10.28.49.909483_BILLTEXT_A04029.XML";
        final BillId billId = new BillId("A04029", 2017);
        // Add text to remove
        processXmlFile(preProcessPath);
        assertTrue("Pre processed text is not blank", StringUtils.isNotBlank(getFullText(billId)));
        processXmlFile(path);
        assertTrue("Post processed text is blank", StringUtils.isBlank(getFullText(billId)));
    }

    @Test
    public void removeUniBill(){
        final String preProcessPath = "processor/bill/text/2017-01-01-00.00.00.000000_BILLTEXT_S01000A.XML";
        final String path = "processor/bill/text/2017-01-02-00.00.00.000000_BILLTEXT_S01000A.XML";
        final BillId senBillId = new BillId("S1000A", 2017);
        final BillId asmBillId = new BillId("A1000A", 2017);
        // Add text to remove
        processXmlFile(preProcessPath);
        assertTrue("Pre processed text is not blank", StringUtils.isNotBlank(getFullText(senBillId)));
        assertTrue("Pre processed text is not blank", StringUtils.isNotBlank(getFullText(asmBillId)));
        processXmlFile(path);
        assertTrue("Post processed text is blank", StringUtils.isBlank(getFullText(senBillId)));
        assertTrue("Post processed text is blank", StringUtils.isBlank(getFullText(asmBillId)));
    }

    /* --- Internal Methods --- */

    /**
     * Get the full text for a bill id
     */
    private String getFullText(BillId billId) throws BillNotFoundEx, BillAmendNotFoundEx {
        Bill bill = billDao.getBill(billId);
        BillAmendment amendment = bill.getAmendment(billId.getVersion());
        return amendment.getFullText();
    }

    /**
     * Get the full text for a bill id that you cannot be sure exists.  Returns null if not exists.
     */
    private String getFullTextSafe(BillId billId) {
        try {
            return getFullText(billId);
        } catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
            return null;
        }
    }
}
