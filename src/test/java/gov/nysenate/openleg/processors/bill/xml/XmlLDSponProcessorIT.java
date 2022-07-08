package gov.nysenate.openleg.processors.bill.xml;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.processors.BaseXmlProcessorTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This class is responsible for testing all the type cases for the Sponsor Sobi Processor.
 * <p>
 * Created by Robert Bebber on 2/22/17.
 */
@Category(IntegrationTest.class)
public class XmlLDSponProcessorIT extends BaseXmlProcessorTest {

    @Autowired BillDataService billDataService;

    private static final Logger logger = LoggerFactory.getLogger(XmlLDSponProcessorIT.class);

    @Test
    public void coSponsorTest() {
        String xmlFilePath = "processor/bill/sponsor/2017-02-09-14.16.27.609430_LDSPON_S02796.XML";

        processXmlFile(xmlFilePath);

        BaseBillId baseBillId = BaseBillId.of(new BillId("S2796", 2017));

        String expectedSponsor = "KRUEGER";
        List<String> expectedCoSponsors = Arrays.asList("STEWART-COUSINS", "ALCANTARA", "AVELLA", "BRESLIN");
        List<String> expectedMultiSponsors = Collections.emptyList();

        verifySponsors(baseBillId, expectedSponsor, false, false,
                expectedCoSponsors, expectedMultiSponsors);
    }

    // Ignore for now, real data for A818 makes this test fail.
    // These tests should be reorganized to not query the prod database.
    @Ignore
    @Test
    public void ignoreEmptyPrimarySponsor() {
        String xmlFilePath = "processor/bill/sponsor/2020-12-30-12.38.45.708793_LDSPON_A00818.XML";

        processXmlFile(xmlFilePath);

        BaseBillId baseBillId = BaseBillId.of(new BillId("A818", 2021));

        String expectedSponsor = null;
        List<String> expectedCoSponsors = Arrays.asList("FAHY", "MCDONOUGH");
        List<String> expectedMultiSponsors = Collections.emptyList();

        verifySponsors(baseBillId, expectedSponsor, false, false,
                expectedCoSponsors, expectedMultiSponsors);
    }

    @Test
    public void multiSponsorTest() {
        String xmlFilePath = "processor/bill/sponsor/2017-01-05-15.43.14.986330_LDSPON_A00289.XML";

        processXmlFile(xmlFilePath);

        BaseBillId baseBillId = BaseBillId.of(new BillId("A289", 2017));

        String expectedSponsor = "GUNTHER";
        List<String> expectedCoSponsors = Arrays.asList("HOOPER", "CROUCH");
        List<String> expectedMultiSponsors = Arrays.asList(
                "GIGLIO", "LOPEZ", "MCLAUGHLIN", "RIVERA", "STECK"
        );

        verifySponsors(baseBillId, expectedSponsor, false, false,
                expectedCoSponsors, expectedMultiSponsors);
    }

    @Test
    public void rulesTest() {
        String xmlFilePath = "processor/bill/sponsor/2016-12-23-18.14.29.288826_LDSPON_A10756.XML";

        processXmlFile(xmlFilePath);

        BaseBillId baseBillId = BaseBillId.of(new BillId("A10756", 2015));

        String expectedSponsor = "MAYER";
        List<String> expectedCoSponsors = Collections.emptyList();
        List<String> expectedMultiSponsors = Collections.emptyList();

        verifySponsors(baseBillId, expectedSponsor, true, false,
                expectedCoSponsors, expectedMultiSponsors);
    }

    @Test
    public void budgetTest() {
        String xmlFilePath = "processor/bill/sponsor/2017-01-23-18.15.36.277999_LDSPON_S02002.XML";

        processXmlFile(xmlFilePath);

        BaseBillId baseBillId = BaseBillId.of(new BillId("S2002", 2017));

        String expectedSponsor = null;
        List<String> expectedCoSponsors = Collections.emptyList();
        List<String> expectedMultiSponsors = Collections.emptyList();

        verifySponsors(baseBillId, expectedSponsor, false, true,
                expectedCoSponsors, expectedMultiSponsors);
    }

    @Test
    public void removeTest() {
        String xmlFilePath = "processor/bill/sponsor/2017-02-02-13.09.56.364510_LDSPON_A04542.XML";

        processXmlFile(xmlFilePath);

        BaseBillId baseBillId = BaseBillId.of(new BillId("A4542", 2017));

        String expectedSponsor = null;
        List<String> expectedCoSponsors = Collections.emptyList();
        List<String> expectedMultiSponsors = Collections.emptyList();

        verifySponsors(baseBillId, expectedSponsor, false, false,
                expectedCoSponsors, expectedMultiSponsors);
    }

    /**
     * Tests the transfer of sponsors as the active amendment changes.
     */
    @Test
    public void amendmentTransferTest() {
        BaseBillId baseBillId = new BaseBillId("A99999", 2017);
        BillId orig = baseBillId.withVersion(Version.ORIGINAL);
        BillId amdA = baseBillId.withVersion(Version.A);
        assertFalse("Test bill should not exist in db", doesBillExist(baseBillId));

        final String testXmlDir = "processor/bill/sponsor/amend_transfer/";

        // Initialize bill
        processXmlFile(testXmlDir + "2017-01-01-00.00.00.000000_BILLSTAT_A99999.XML");
        processXmlFile(testXmlDir + "2017-01-01-00.00.00.000000_LDSPON_A99999.XML");
        final String mainSponsor = "FINCH";
        final List<String> firstCoSpon = ImmutableList.of("HEVESI");
        final List<String> firstMultiSpon = ImmutableList.of("CASTORINA");
        verifySponsors(orig, mainSponsor, firstCoSpon, firstMultiSpon);
        // Add unpublished amend A
        processXmlFile(testXmlDir + "2017-01-02-00.00.00.000000_BILLTEXT_A99999A.XML");
        verifySponsors(orig, mainSponsor, firstCoSpon, firstMultiSpon);
        verifySponsors(amdA, mainSponsor, firstCoSpon, firstMultiSpon);
        // Add second co/multi sponsors
        processXmlFile(testXmlDir + "2017-01-03-00.00.00.000000_LDSPON_A99999.XML");
        final List<String> secondCoSpon = ImmutableList.<String>builder().addAll(firstCoSpon).add("RODRIGUEZ").build();
        final List<String> secondMultiSpon = ImmutableList.<String>builder().addAll(firstMultiSpon).add("BARRETT").build();
        verifySponsors(orig, mainSponsor, secondCoSpon, secondMultiSpon);
        verifySponsors(amdA, mainSponsor, firstCoSpon, firstMultiSpon);
        // Make A active amendment
        processXmlFile(testXmlDir + "2017-01-04-00.00.00.000000_BILLSTAT_A99999A.XML");
        verifySponsors(orig, mainSponsor, secondCoSpon, secondMultiSpon);
        verifySponsors(amdA, mainSponsor, secondCoSpon, secondMultiSpon);
        // Add third co/multi sponsors
        processXmlFile(testXmlDir + "2017-01-05-00.00.00.000000_LDSPON_A99999A.XML");
        final List<String> thirdCoSpon = ImmutableList.<String>builder().addAll(secondCoSpon).add("TITUS").build();
        final List<String> thirdMultiSpon = ImmutableList.<String>builder().addAll(secondMultiSpon).add("GALEF").build();
        verifySponsors(orig, mainSponsor, secondCoSpon, secondMultiSpon);
        verifySponsors(amdA, mainSponsor, thirdCoSpon, thirdMultiSpon);
        // Restore to original amend
        processXmlFile(testXmlDir + "2017-01-06-00.00.00.000000_BILLSTAT_A99999.XML");
        verifySponsors(orig, mainSponsor, thirdCoSpon, thirdMultiSpon);
        verifySponsors(amdA, mainSponsor, thirdCoSpon, thirdMultiSpon);
    }

    @Test
    public void emptyComTest() {
        final BillId billId = new BillId("S99999", 2017);
        assertFalse("Test bill should not exist in db", doesBillExist(billId));
        final String testXmlDir = "processor/bill/sponsor/empty_com/";
        final String validSponXML = testXmlDir + "2017-02-09-00.00.00.000000_LDSPON_S99999.XML";
        final String emptyComXml = testXmlDir + "2017-02-09-01.00.00.000000_LDSPON_S99999.XML";

        processXmlFile(validSponXML);
        verifySponsors(billId, "MARCHIONE", true, false,
                Collections.emptyList(), Collections.emptyList());

        processXmlFile(emptyComXml);
        verifySponsors(billId, null, true, false,
                Collections.emptyList(), Collections.emptyList());
    }

    /* --- Internal Methods --- */

    /**
     * This method is responsible checking each segment of the bill sponsor section. and checks if it asserts equals.
     *
     * @param billId                The base id of the bill being effected by the sobifragment
     * @param expectedSponsor       expected result of the bill being effected by the sobifragment
     * @param isRules               if the sponsor is also a rule
     * @param isBudget              if the sponsor is also a budget bill
     * @param expectedCoSponsors    a list of the expected CoSponsors for the sobifragment
     * @param expectedMultiSponsors a list of the expected MultiSponsors for the sobifragment
     */
    private void verifySponsors(BillId billId, String expectedSponsor,
                                boolean isRules, boolean isBudget,
                                List<String> expectedCoSponsors, List<String> expectedMultiSponsors) {
        Bill bill = getBill(billId);
        BillAmendment amendment;
        if (billId instanceof BaseBillId) {
            amendment = bill.getActiveAmendment();
        } else {
            amendment = bill.getAmendment(billId.getVersion());
        }

        Optional<BillSponsor> actualSponsorOpt = Optional.ofNullable(bill.getSponsor());
        String actualSponsorShortName = actualSponsorOpt
                .map(BillSponsor::getMember)
                .map(SessionMember::getLbdcShortName)
                .orElse(null);
        assertEquals(billId + " Sponsor", expectedSponsor, actualSponsorShortName);

        boolean actualIsRules = actualSponsorOpt.map(BillSponsor::isRules).orElse(false);
        assertEquals(billId + " Is Rules Sponsor", isRules, actualIsRules);

        boolean actualIsBudget = actualSponsorOpt.map(BillSponsor::isBudget).orElse(false);
        assertEquals(billId + " Is Budget Sponsor", isBudget, actualIsBudget);

        List<String> actualCoSponsors = amendment.getCoSponsors().stream()
                .map(SessionMember::getLbdcShortName)
                .toList();
        assertEquals(billId + " Co-Sponsors", expectedCoSponsors, actualCoSponsors);

        List<String> actualMultiSponsors = amendment.getMultiSponsors().stream()
                .map(SessionMember::getLbdcShortName)
                .toList();
        assertEquals(billId + " Multi-Sponsors", expectedMultiSponsors, actualMultiSponsors);
    }

    private void verifySponsors(BillId billId, String expectedSponsor,
                                List<String> expectedCoSponsors, List<String> expectedMultiSponsors) {
        verifySponsors(billId, expectedSponsor, false, false, expectedCoSponsors, expectedMultiSponsors);
    }

}
