package gov.nysenate.openleg.processor.bill.apprmemo;

import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.dao.bill.data.ApprovalDao;
import gov.nysenate.openleg.model.bill.ApprovalMessage;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by senateuser on 3/2/17.
 */
@Category(IntegrationTest.class)
public class ApprmemoSobiProcessorIT extends BaseXmlProcessorTest {

    @Autowired private BillDataService billDataService;
    @Autowired private XmlApprMemoProcessor xmlApprMemoProcessor;

    @Autowired private ApprovalDao approvalDao;

    private static final Logger logger = LoggerFactory.getLogger(ApprmemoSobiProcessorIT.class);

    @Override
    protected SobiProcessor getSobiProcessor() {
        return xmlApprMemoProcessor;
    }

    @Test
    public void replaceProcessor() {
        String xmlFilePath = "processor/bill/apprmemo/2016-11-17-10.06.30.448942_APPRMEMO_2016-00002.XML";
        processXmlFile(xmlFilePath);
        Bill baseBill = billDataService.getBill(new BaseBillId("A06182", 2016));
        String expectedMemo = "\n \n" +
                "                 APPROVAL MEMORANDUM - No. 2 Chapter 62\n" +
                "       MEMORANDUM filed with Assembly Bill Number 6182, entitled:\n" +
                "  This bill is approved.                      (signed) ANDREW M. CUOMO\n" +
                " \n" +
                "                                ______\n";
        String expectedSignature = "ANDREW M. CUOMO";
        int expectedChapter = 62;
        checkConditions(expectedMemo, expectedSignature, expectedChapter, baseBill.getApprovalMessage());
    }

    @Test
    public void removeProcessor() {
        BaseBillId billId = new BaseBillId("S6789", 2015);
        String preProcessXmlFilePath = "processor/bill/apprmemo/2016-12-01-00.00.00.000000_APPRMEMO_2016_00009.XML";
        String xmlFilePath = "processor/bill/apprmemo/2016-12-01-11.01.07.328317_APPRMEMO_2016-00009.XML";
        Bill bill;

        processXmlFile(preProcessXmlFilePath);
        bill = billDataService.getBill(billId);
        assertNotNull("Bill must have an initial approval message", bill.getApprovalMessage());
        processXmlFile(xmlFilePath);
        bill = billDataService.getBill(billId);
        assertNull("Bill must have null approval after removal", bill.getApprovalMessage());
    }

    public void checkConditions(String expectedMemo, String expectedSignature, int expectedChapter, ApprovalMessage approvalMessage) {
        int actualChapter = approvalMessage.getChapter();
        assertEquals(approvalMessage + " Memo Chapter Comparison:", expectedChapter, actualChapter);
        String actualSignature = approvalMessage.getSigner();
        assertEquals(approvalMessage + " Memo Signature Comparison:", expectedSignature, actualSignature);
        String actualMemoText = approvalMessage.getMemoText();
        assertEquals(approvalMessage + " Memo Text Comparison:", expectedMemo, actualMemoText);
    }
}
