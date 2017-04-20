package gov.nysenate.openleg.processor.bill.sponsor;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.dao.sourcefiles.sobi.SobiDao;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * This class is responsible for testing all the type cases for the Sponsor Sobi Processor.
 * <p>
 * Created by Robert Bebber on 2/22/17.
 */
@Transactional
public class SponsorSobiProcessorTest extends BaseXmlProcessorTest {
    @Autowired
    BillDao billDao;
    @Autowired
    SobiDao sobiDao;
    @Autowired
    SponsorSobiProcessor sponsorSobiProcessor;

    private static final Logger logger = LoggerFactory.getLogger(SponsorSobiProcessorTest.class);

    @Override
    protected SobiProcessor getSobiProcessor() {
        return sponsorSobiProcessor;
    }

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
     * This method is responsible checking each segment of the bill sponsor section. and checks if it asserts equals.
     *
     * @param billId                The base id of the bill being effected by the sobifragment
     * @param expectedSponsor       expected result of the bill being effected by the sobifragment
     * @param isRules               if the sponsor is also a rule
     * @param isBudget              if the sponsor is also a budget bill
     * @param expectedCoSponsors    a list of the expected CoSponsors for the sobifragment
     * @param expectedMultiSponsors a list of the expected MultiSponsors for the sobifragment
     */
    private void verifySponsors(BaseBillId billId, String expectedSponsor, boolean isRules, boolean isBudget,
                                List<String> expectedCoSponsors, List<String> expectedMultiSponsors) {
        Bill bill = billDao.getBill(billId);
        BillAmendment activeAmend = bill.getActiveAmendment();

        Optional<BillSponsor> actualSponsorOpt = Optional.ofNullable(bill.getSponsor());
        String actualSponsorShortName = actualSponsorOpt
                .map(BillSponsor::getMember)
                .map(SessionMember::getLbdcShortName)
                .orElse(null);
        assertEquals(billId + " Sponsor", expectedSponsor, actualSponsorShortName);

        boolean actualIsRules = actualSponsorOpt.map(BillSponsor::isRules).orElse(false);
        assertEquals(billId + " Is Rules Sponsor", isRules, actualIsRules);

        boolean actualIsBudget = actualSponsorOpt.map(BillSponsor::isBudget).orElse(false);
        assertEquals(billId + " Is Budget Sponsor", isRules, actualIsBudget);

        List<String> actualCoSponsors = activeAmend.getCoSponsors().stream()
                .map(SessionMember::getLbdcShortName)
                .collect(Collectors.toList());
        assertEquals(billId + " Co-Sponsors", expectedCoSponsors, actualCoSponsors);

        List<String> actualMultiSponsors = activeAmend.getMultiSponsors().stream()
                .map(SessionMember::getLbdcShortName)
                .collect(Collectors.toList());
        assertEquals(billId + " Multi-Sponsors", expectedMultiSponsors, actualMultiSponsors);
    }
}
