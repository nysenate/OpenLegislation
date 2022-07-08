package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Robert Bebber on 3/20/17.
 */
@Category(IntegrationTest.class)
public class XmlBillStatProcessorIT extends BaseXmlProcessorTest {

    @Autowired BillDataService billDataService;

    private static final Logger logger = LoggerFactory.getLogger(XmlBillStatProcessor.class);

    @Test
    public void replaceTest() {
        String xmlFilePath = "processor/bill/billstat/2017-02-09-13.19.38.127796_BILLSTAT_S04329.XML";

        processXmlFile(xmlFilePath);

        Bill baseBill = billDataService.getBill(new BaseBillId("S04329", 2017));
        String expectedVersion = "";
        String expectedSponsor = "GALLIVAN";
        String expectedLawSec = "General Municipal Law";
        String expectedTitle = "Enacts the \"charitable gaming act of 2017\"";
        ArrayList<String> expectedBillAction = new ArrayList<>();
        expectedBillAction.add("2017-02-09 (SENATE) REFERRED TO RACING, GAMING AND WAGERING");
        assertTest(baseBill, expectedVersion, expectedSponsor, expectedLawSec, expectedTitle, expectedBillAction);
    }

    @Test
    public void replaceLargeTest() {
        String xmlFilePath = "processor/bill/billstat/2016-12-31-21.52.25.010960_BILLSTAT_S05795A.XML";

        processXmlFile(xmlFilePath);

        Bill baseBill = billDataService.getBill(new BaseBillId("S05795", 2015));
        String expectedVersion = "A";
        String expectedSponsor = "MARCELLINO";
        String expectedLawSec = "Education Law";
        String expectedTitle = "Relates to reporting school finances, reserve funds, final annual budgets and multi-year financial plans";
        ArrayList<String> expectedBillAction = new ArrayList<>();
        expectedBillAction.add("2015-06-03 (SENATE) REFERRED TO EDUCATION");
        expectedBillAction.add("2015-06-25 (SENATE) COMMITTEE DISCHARGED AND COMMITTED TO RULES");
        expectedBillAction.add("2015-06-25 (SENATE) ORDERED TO THIRD READING CAL.1897");
        expectedBillAction.add("2015-06-25 (SENATE) PASSED SENATE");
        expectedBillAction.add("2015-06-25 (SENATE) DELIVERED TO ASSEMBLY");
        expectedBillAction.add("2015-06-25 (ASSEMBLY) " + "referred to education".toUpperCase());
        expectedBillAction.add("2016-01-06 (ASSEMBLY)" + " died in assembly".toUpperCase());
        expectedBillAction.add("2016-01-06 (ASSEMBLY)" + " returned to senate".toUpperCase());
        expectedBillAction.add("2016-01-06 (SENATE) REFERRED TO EDUCATION");
        expectedBillAction.add("2016-02-24 (SENATE) 1ST REPORT CAL.225");
        expectedBillAction.add("2016-02-25 (SENATE) 2ND REPORT CAL.");
        expectedBillAction.add("2016-02-29 (SENATE) ADVANCED TO THIRD READING");
        expectedBillAction.add("2016-05-16 (SENATE) AMENDED ON THIRD READING 5795A");
        expectedBillAction.add("2016-05-24 (SENATE) PASSED SENATE");
        expectedBillAction.add("2016-05-24 (SENATE) DELIVERED TO ASSEMBLY");
        expectedBillAction.add("2016-05-24 (ASSEMBLY)" + " referred to education".toUpperCase());
        expectedBillAction.add("2016-06-08 (ASSEMBLY)" + " substituted for a7675a".toUpperCase());
        expectedBillAction.add("2016-06-08 (ASSEMBLY)" + " ordered to third reading rules cal.53".toUpperCase());
        expectedBillAction.add("2016-06-08 (ASSEMBLY)" + " passed assembly".toUpperCase());
        expectedBillAction.add("2016-06-08 (ASSEMBLY)" + " returned to senate".toUpperCase());
        expectedBillAction.add("2016-12-20 (SENATE) DELIVERED TO GOVERNOR");
        expectedBillAction.add("2016-12-31 (SENATE) SIGNED CHAP.514");
        assertTest(baseBill, expectedVersion, expectedSponsor, expectedLawSec, expectedTitle, expectedBillAction);
    }

    @Test
    public void removeTest() {
        String xmlFilePath = "processor/bill/billstat/2017-02-03-15.30.00.880075_BILLSTAT_A04833.XML";

        processXmlFile(xmlFilePath);

        Bill bill = billDataService.getBill(new BaseBillId("A04833", 2017));
        assertFalse("Base version should be unpublished", bill.isBaseVersionPublished());
    }

    /**
     * This Method can be used for all replace Tests and currently Tests that
     * The Version, Sponsor, Law Section and the Title are the same from actual
     * in the Bill to what is expected from the XML.
     *
     * @param baseBill  Bill being tested
     * @param exVersion Expected Version
     * @param exSponsor Expected Sponsor
     * @param exLawSec  Expected Law Section
     * @param exTitle   Expected Title
     */
    public void assertTest(Bill baseBill, String exVersion, String exSponsor, String exLawSec, String exTitle,
                           ArrayList<String> expectedBillActions) {
        String actualVersion = baseBill.getActiveVersion().toString();
        assertEquals("Version Comparison: ", exVersion, actualVersion);
        String actualSponsor = baseBill.getSponsor().getMember().getLbdcShortName();
        assertEquals("Sponsor Comparison: ", exSponsor, actualSponsor);
        String actualLawSec = baseBill.getActiveAmendment().getLawSection();
        assertEquals("Law Section Comparison: ", exLawSec, actualLawSec);
        String actualTitle = baseBill.getTitle();
        assertEquals("Title Comparison: ", exTitle, actualTitle);
        assertNotNull("Actions shouldn't be null", baseBill.getActions());
        List<String> actionStrings = baseBill.getActions().stream().map(String::valueOf).toList();
        assertEquals("Actions should match expected", expectedBillActions, actionStrings);
    }
}
