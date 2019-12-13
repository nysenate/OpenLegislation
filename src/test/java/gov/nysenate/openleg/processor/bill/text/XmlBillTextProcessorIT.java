package gov.nysenate.openleg.processor.bill.text;

import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.util.FileIOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static gov.nysenate.openleg.model.bill.BillTextFormat.HTML;
import static gov.nysenate.openleg.model.bill.BillTextFormat.PLAIN;
import static gov.nysenate.openleg.model.bill.BillTextFormat.HTML5;
import static gov.nysenate.openleg.model.bill.BillTextFormat.DIFF;
import static org.junit.Assert.*;

/**
 * Created by senateuser on 2017/2/28.
 */
@Category(IntegrationTest.class)
public class XmlBillTextProcessorIT extends BaseXmlProcessorTest {

    private static final String resourceDir = "processor/bill/text";

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
        final String expectedHtml = FileIOUtils.getResourceFileContents(resourceDir + "/S9999_expected.html");
        final String expectedHtml5 = FileIOUtils.getResourceFileContents(resourceDir + "/S9999_expected.html5");
        final String expectedPlain = FileIOUtils.getResourceFileContents(resourceDir + "/S9999_expected.txt");
        final String expectedDiff = FileIOUtils.getResourceFileContents(resourceDir + "/S9999_expected.json");

        assertNotEquals("Preprocessed html is not set", expectedHtml, getHtmlTextSafe(billId));
        assertNotEquals("Preprocessed plaintext is not set", expectedPlain, getPlainTextSafe(billId));
        assertNotEquals("Preprocessed html5 is not set", expectedHtml5, getHtml5TextSafe(billId));
        assertNotEquals("Preprocessed diff is not set", expectedDiff, getDiffSafe(billId));
        processXmlFile(path);
        assertEquals("post processed html is set", expectedHtml, getHtmlText(billId));
        assertEquals("post processed plaintext is set", expectedPlain, getPlainText(billId));
        assertEquals("post processed html5 is set", expectedHtml5, getHtml5Text(billId));
        assertEquals("post processed diff is set", expectedDiff, getDiff(billId));
    }

    /* --- Internal Methods --- */

    /**
     * Get the full text for a bill id
     */
    private String getPlainText(BillId billId) {
        return getAmendment(billId, PLAIN).getFullText(PLAIN);
    }

    /**
     * Get the full text for a bill id that you cannot be sure exists.  Returns null if not exists.
     */
    private String getPlainTextSafe(BillId billId) {
        try {
            return getPlainText(billId);
        } catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
            return null;
        }
    }

    /**
     * Get the html text for a bill id.
     */
    private String getHtmlText(BillId billId) {
        return getAmendment(billId, HTML).getFullText(HTML);
    }

    /**
     * Get the full html text for a bill id that you cannot be sure exists.  Returns null if not exists.
     */
    private String getHtmlTextSafe(BillId billId) {
        try {
            return getHtmlText(billId);
        } catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
            return null;
        }
    }


    /**
     * Get the html5 text for a bill id.
     */
    private String getHtml5Text(BillId billId) { return getAmendment(billId, HTML5).getFullText(HTML5); }

    /**
     * Get the full html5 text for a bill id that you cannot be sure exists.  Returns null if not exists.
     */

    private String getHtml5TextSafe(BillId billId) {
        try {
            return getHtml5Text(billId);
        } catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
            return null;
        }
    }

    /**
     * Get the html text for a bill id.
     */
    private String getDiff(BillId billId) { return getAmendment(billId, DIFF).getFullText(DIFF); }

    /**
     * Get the full html text for a bill id that you cannot be sure exists.  Returns null if not exists.
     */

    private String getDiffSafe(BillId billId) {
        try {
            return getDiff(billId);
        } catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
            return null;
        }
    }
}
