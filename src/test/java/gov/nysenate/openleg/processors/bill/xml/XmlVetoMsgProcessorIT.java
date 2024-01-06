package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(IntegrationTest.class)
public class XmlVetoMsgProcessorIT extends BaseXmlProcessorTest {
    private static final String dataDir = "processor/bill/vetomessage";
    // This is before any of our bill data, preventing conflicts.
    private static final int testYear = 2008;
    @Autowired private BillDataService billDataService;

    @Test
    public void processStandardVetoMessage()  {
        processXmlFile(dataDir + "/2008-11-29-12.19.46.006100_VETOMSG_2008-00231.XML");
        var billId = new BaseBillId("A1984", testYear);
        Bill bill = billDataService.getBill(billId);

        String memoText = """
                \s
                                         VETO MESSAGE - No. 231
                \s
                TO THE ASSEMBLY:
                \s
                I am returning herewith, without my approval, the following bill:
                \s
                Assembly Bill Number 1984, entitled:
                \s
                    "AN  ACT to amend the executive law, in relation to requiring parole
                      decisions to be published on a website"
                \s
                    NOT APPROVED
                \s
                  The bill is disapproved.                    (signed) ANDREW M. CUOMO
                \s
                                               __________
                """;
        VetoId id = new VetoId(testYear, 231);
        VetoMessage storedMsgObject = bill.getVetoMessages().get(id);

        assertEquals(memoText, storedMsgObject.getMemoText());
        assertEquals(billId, storedMsgObject.getBillId());
        assertEquals("ANDREW M. CUOMO", storedMsgObject.getSigner());
        assertEquals(VetoType.STANDARD, storedMsgObject.getType());
        assertEquals(testYear, storedMsgObject.getYear());
    }

    @Test
    public void lineTypeVetoMessage() {
        processXmlFile(dataDir + "/2008-11-17-09.59.11.184200_VETOMSG_2008-00002.XML");
        Bill bill = billDataService.getBill(new BaseBillId("A9000", testYear));
        String memoText = """
                \s
                                  STATE OF NEW YORK--EXECUTIVE CHAMBER
                \s
                TO THE ASSEMBLY:                                        April 13, 2008
                \s
                     I  hereby transmit pursuant to the provisions of section 7 of Arti-
                cle IV and section 4 of Article VII of the Constitution, a statement  of
                items  to which I object and which I do not approve, contained in Assem-
                bly Bill Number 9000--D, entitled:
                \s
                CHAPTER 50
                \s
                LINE VETO #2
                \s
                "AN ACT making appropriations for the support of government
                \s
                                          STATE OPERATIONS BUDGET"
                \s
                Bill Page 124, Line 31 through Line 35, inclusive
                \s
                NOT APPROVED
                ____________
                \s
                                          EDUCATION DEPARTMENT
                \s
                 "For services and expenses for the supervision of  institutions  regis-
                    tered  pursuant  to  section  5001  of  the  education  law, and for
                    services and expenses of supervisory programs and payment of associ-
                    ated indirect costs and general state charges.
                  Personal service--regular ... 1,747,000 ............... (re. $200,000)"
                \s
                This item passed by the Legislature,  to  which  I  object  and  do  not
                approve,  is  not needed because adequate funding for State agency oper-
                ations is already provided for in the budget. Accordingly, this item  is
                disapproved.
                \s
                                                              (signed) ANDREW M. CUOMO
                """;

        VetoId id = new VetoId(testYear, 2);
        VetoMessage storedMsgObject = bill.getVetoMessages().get(id);

        assertEquals(memoText, storedMsgObject.getMemoText());
        assertEquals(new BaseBillId("A9000",testYear), storedMsgObject.getBillId());
        assertEquals("ANDREW M. CUOMO", storedMsgObject.getSigner());
        assertEquals(VetoType.LINE_ITEM, storedMsgObject.getType());
        assertEquals(testYear, storedMsgObject.getYear());
        assertEquals(2, storedMsgObject.getVetoNumber());
        assertEquals(LocalDate.of(testYear,4,13), storedMsgObject.getSignedDate());
        assertEquals(50, storedMsgObject.getChapter());
        assertEquals(124, storedMsgObject.getBillPage());
        assertEquals(31, storedMsgObject.getLineStart());
        assertEquals(35, storedMsgObject.getLineEnd());
    }

    @Test
    public void removeMessage() {
        final var baseId = new BaseBillId("A9000", testYear);
        // Adding Veto message
        String xmlPath = "processor/bill/vetomessage/2008-11-17-09.59.11.184200_VETOMSG_2008-00002.XML";
        processXmlFile(xmlPath);
        Bill bill = billDataService.getBill(baseId);

        var id = new VetoId(testYear, 2);
        VetoMessage storedMsgObject = bill.getVetoMessages().get(id);
        assertEquals(storedMsgObject.getBillId(), baseId);
        // Removing vetoMessage
        String xmlPath1 = "processor/bill/vetomessage/2008-11-28-23.56.28.935222_VETOMSG_2008-00233.XML";
        processXmlFile(xmlPath1);

        Bill bill1 = billDataService.getBill(baseId);
        assertNull(bill1.getVetoMessages().get(id));
    }
}
