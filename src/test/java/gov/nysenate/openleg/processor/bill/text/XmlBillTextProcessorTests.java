package gov.nysenate.openleg.processor.bill.text;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.bill.XmlBillTextProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by senateuser on 2017/2/28.
 */
public class XmlBillTextProcessorTests extends BaseXmlProcessorTest {

    private static final String resourceDir = "processor/bill/text";

    @Autowired private BillDataService billDataService;
    @Autowired private XmlBillTextProcessor xmlBillTextProcessor;

    @Override
    protected SobiProcessor getSobiProcessor() {
        return xmlBillTextProcessor;
    }

    @Test
    public void replaceSenateBill(){
        final String path = resourceDir + "/2017-02-09-12.41.27.159140_BILLTEXT_S04325.XML";
        final String expectedS = "replace senate bill";
        final BillId billId = new BillId("S04325", 2017);

        assertNotEquals("New text value not yet set", expectedS, getPlainTextSafe(billId));
        processXmlFile(path);
        assertEquals("New text value set", expectedS, getPlainText(billId));
    }

    @Test
    public void replaceAssemblyBill(){
        final String path = resourceDir + "/2017-02-09-14.26.07.392403_BILLTEXT_A05464.XML";
        final String expectedS = "replace assembly bill";
        final BillId billId = new BillId("A05464", 2017);

        assertNotEquals("New text value not yet set", expectedS, getPlainTextSafe(billId));
        processXmlFile(path);
        assertEquals("New text value set", expectedS, getPlainText(billId));
    }

    @Test
    public void replaceUniBill(){
        final String path = resourceDir + "/2017-02-01-11.40.11.918032_BILLTEXT_S03526A.XML";
        final String expectedText = "replace uni bill";
        final BillId senateBillId = new BillId("S03526A", 2017);
        final BillId asmBillId = new BillId("A03028A", 2017);

        assertNotEquals("New text value not yet set", expectedText, getPlainTextSafe(senateBillId));
        assertNotEquals("New text value not yet set", expectedText, getPlainTextSafe(asmBillId));
        processXmlFile(path);
        assertEquals("New text value set", expectedText, getPlainText(senateBillId));
        assertEquals("New text value set", expectedText, getPlainText(asmBillId));
    }

    @Test
    public void removeSenateBill(){
        final String preProcessPath = resourceDir + "/2017-01-31-00.00.00.000000_BILLTEXT_S03954.XML";
        final String path = resourceDir + "/2017-01-31-10.28.19.879620_BILLTEXT_S03954.XML";
        final BillId billId = new BillId("S03954", 2017);
        // Add text to remove
        processXmlFile(preProcessPath);
        assertTrue("Pre processed text is not blank", StringUtils.isNotBlank(getPlainText(billId)));
        processXmlFile(path);
        assertTrue("Post processed text is blank", StringUtils.isBlank(getPlainText(billId)));
    }

    @Test
    public void removeAssemblyBill(){
        final String preProcessPath = resourceDir + "/2017-01-31-00.00.00.000000_BILLTEXT_A04029.XML";
        final String path = resourceDir + "/2017-01-31-10.28.49.909483_BILLTEXT_A04029.XML";
        final BillId billId = new BillId("A04029", 2017);
        // Add text to remove
        processXmlFile(preProcessPath);
        assertTrue("Pre processed text is not blank", StringUtils.isNotBlank(getPlainText(billId)));
        processXmlFile(path);
        assertTrue("Post processed text is blank", StringUtils.isBlank(getPlainText(billId)));
    }

    @Test
    public void removeUniBill(){
        final String preProcessPath = resourceDir + "/2017-01-01-00.00.00.000000_BILLTEXT_S01000A.XML";
        final String path = resourceDir + "/2017-01-02-00.00.00.000000_BILLTEXT_S01000A.XML";
        final BillId senBillId = new BillId("S1000A", 2017);
        final BillId asmBillId = new BillId("A1000A", 2017);
        // Add text to remove
        processXmlFile(preProcessPath);
        assertTrue("Pre processed text is not blank", StringUtils.isNotBlank(getPlainText(senBillId)));
        assertTrue("Pre processed text is not blank", StringUtils.isNotBlank(getPlainText(asmBillId)));
        processXmlFile(path);
        assertTrue("Post processed text is blank", StringUtils.isBlank(getPlainText(senBillId)));
        assertTrue("Post processed text is blank", StringUtils.isBlank(getPlainText(asmBillId)));
    }

    @Test
    public void htmlTextTest() throws IOException {
        final String path = resourceDir + "/2017-01-01-00.00.00.000000_BILLTEXT_S09999.XML";
        final BillId billId = new BillId("S9999", 2017);
        final String expectedHtml = FileUtils.readFileToString(
                new File(getClass().getClassLoader().getResource(resourceDir + "/S9999_expected.html").getFile()));
        final String expectedPlain = FileUtils.readFileToString(
                new File(getClass().getClassLoader().getResource(resourceDir + "/S9999_expected.txt").getFile()));

        assertNotEquals("Preprocessed html is not set", expectedHtml, getHtmlTextSafe(billId));
        assertNotEquals("Preprocessed plaintext is not set", expectedPlain, getPlainTextSafe(billId));
        processXmlFile(path);
        assertEquals("post processed html is set", expectedHtml, getHtmlText(billId));
        assertEquals("post processed plaintext is set", expectedPlain, getPlainText(billId));
    }

    /* --- Internal Methods --- */

    /**
     * Get a bill amendment from the db
     */
    private BillAmendment getAmendment(BillId billId) throws BillNotFoundEx, BillAmendNotFoundEx {
        Bill bill = billDataService.getBill(BaseBillId.of(billId));
        return bill.getAmendment(billId.getVersion());
    }

    /**
     * Get a bill amendment from the db, returning null if bill/amendment not found exceptions are raised.
     */
    private BillAmendment getAmendmentSafe(BillId billId) {
        try {
            return getAmendment(billId);
        } catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
            return null;
        }
    }

    /**
     * Get the full text for a bill id
     */
    private String getPlainText(BillId billId) {
        return getAmendment(billId).getFullText();
    }

    /**
     * Get the full text for a bill id that you cannot be sure exists.  Returns null if not exists.
     */
    private String getPlainTextSafe(BillId billId) {
        return Optional.ofNullable(getAmendmentSafe(billId))
                .map(BillAmendment::getFullText)
                .orElse(null);
    }

    /**
     * Get the html text for a bill id.
     */
    private String getHtmlText(BillId billId) {
        return getAmendment(billId).getFullTextHtml();
    }

    /**
     * Get the full html text for a bill id that you cannot be sure exists.  Returns null if not exists.
     */
    private String getHtmlTextSafe(BillId billId) {
        return Optional.ofNullable(getAmendmentSafe(billId))
                .map(BillAmendment::getFullTextHtml)
                .orElse(null);
    }
}
