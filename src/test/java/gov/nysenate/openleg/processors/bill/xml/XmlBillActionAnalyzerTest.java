package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.PublishStatus;
import gov.nysenate.openleg.legislation.bill.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static gov.nysenate.openleg.legislation.bill.BillStatusType.*;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class XmlBillActionAnalyzerTest {

    private final int testYear = 1999;
    private final BillId testBillId = new BaseBillId("S9000", testYear);
    private final PublishStatus defaultPubStatus =
            new PublishStatus(true, LocalDate.ofYearDay(testYear, 1).atStartOfDay());

    @Test
    public void lostActionTest() {
        List<BillAction> actions = new LinkedList<>();
        addTestAction(actions, "LOST");
        analyzeAndVerifyStatus(actions, LOST);
    }

    @Test
    public void falseLostActionTest() {
        List<String> notLostActionTexts = Arrays.asList(
                "MOTION TO AMEND LOST",
                "MOTION TO AMEND LOST - ROLL CALL VOTE",
                "MOTION TO AMEND LOST - VOICE VOTE",
                "MOTION TO DISCHARGE - LOST - ROLL CALL VOTE",
                "MOTION TO DISCHARGE LOST",
                "MOTION TO LAY UPON THE TABLE LOST",
                "MOTION TO POSTPONE LOST"
        );
        for (String falseLostActionText : notLostActionTexts) {
            List<BillAction> actions = new LinkedList<>();
            addTestAction(actions, falseLostActionText);
            analyzeAndVerifyStatus(actions, INTRODUCED);
        }
    }

    @Test
    public void vetoShouldNotAddPocketApproval() {
        List<BillAction> actions = new LinkedList<>();
        addTestAction(actions, "REFERRED TO RULES");
        addTestAction(actions, "AMENDED BY RESTORING TO ORIGINAL PRINT 9000");
        addTestAction(actions, "PASSED SENATE");
        addTestAction(actions, "DELIVERED TO ASSEMBLY");
        addTestAction(actions, "referred to ways and means");
        addTestAction(actions, "substituted for a8892");
        addTestAction(actions, "ordered to third reading rules cal.702");
        addTestAction(actions, "passed assembly");
        addTestAction(actions, "returned to senate");
        addTestAction(actions, "DELIVERED TO GOVERNOR");
        addTestAction(actions, "VETOED MEMO.101");

        XmlBillActionAnalyzer analyzer = new XmlBillActionAnalyzer(testBillId, actions, Optional.of(defaultPubStatus));
        analyzer.analyze();

        assertEquals(Version.ORIGINAL, analyzer.getActiveVersion());
        LinkedList<BillStatus> milestones  = analyzer.getMilestones();
        boolean sawVetoed = false;
        for (BillStatus milestone : milestones) {
            if (milestone.getStatusType().equals(VETOED)) {
                sawVetoed = true;
            }
            assertFalse(milestone.getStatusType().equals(POCKET_APPROVAL));
        }
        assertTrue(sawVetoed);
    }

    @Test
    public void signedShouldNotAddPocketApproval() {
        List<BillAction> actions = new LinkedList<>();
        addTestAction(actions, "REFERRED TO RULES");
        addTestAction(actions, "PASSED SENATE");
        addTestAction(actions, "REFERRED TO WAYS AND MEANS");
        addTestAction(actions, "ORDERED TO THIRD READING RULES CAL.702");
        addTestAction(actions, "PASSED ASSEMBLY");
        addTestAction(actions, "DELIVERED TO GOVERNOR");
        addTestAction(actions, "SIGNED CHAP.101");

        XmlBillActionAnalyzer analyzer = new XmlBillActionAnalyzer(testBillId, actions, Optional.of(defaultPubStatus));
        analyzer.analyze();

        LinkedList<BillStatus> milestones = analyzer.getMilestones();
        boolean sawSigned = false;
        for (BillStatus milestone : milestones) {
            if (milestone.getStatusType().equals(SIGNED_BY_GOV)) {
                sawSigned = true;
            }
            assertFalse(milestone.getStatusType().equals(POCKET_APPROVAL));
        }
        assertTrue(sawSigned);
    }

    @Test
    public void analyzeShouldUpdateActiveVersionAndMutateActionBillIds() {
        List<BillAction> actions = new LinkedList<>();
        addTestAction(actions, "PRINT NUMBER 9000A");
        addTestAction(actions, "AMENDED BY RESTORING TO ORIGINAL PRINT 9000");

        XmlBillActionAnalyzer analyzer = new XmlBillActionAnalyzer(testBillId, actions, Optional.of(defaultPubStatus));
        analyzer.analyze();

        assertEquals(Version.ORIGINAL, analyzer.getActiveVersion());
        assertEquals(Version.A, actions.get(0).getBillId().getVersion());
        assertEquals(Version.ORIGINAL, actions.get(1).getBillId().getVersion());
        assertTrue(analyzer.getPublishStatusMap().containsKey(Version.A));
        assertTrue(analyzer.getPublishStatusMap().get(Version.A).isPublished());
    }

    @Test
    public void analyzeShouldCarryFloorCalendarNumberForward() {
        List<BillAction> actions = new LinkedList<>();
        addTestAction(actions, "REPORT CAL.123");
        addTestAction(actions, "THIRD READING");

        XmlBillActionAnalyzer analyzer = new XmlBillActionAnalyzer(testBillId, actions, Optional.of(defaultPubStatus));
        analyzer.analyze();

        List<BillStatus> statuses = analyzer.getStatuses();
        assertEquals(2, statuses.size());
        assertEquals(SENATE_FLOOR, statuses.get(0).getStatusType());
        assertEquals(Integer.valueOf(123), statuses.get(0).getCalendarNo());
        assertEquals(SENATE_FLOOR, statuses.get(1).getStatusType());
        assertEquals(Integer.valueOf(123), statuses.get(1).getCalendarNo());
        assertEquals(1, analyzer.getBillStatus().getActionSequenceNo());
    }

    @Test
    public void analyzeShouldTrackSubstitutionOnActiveVersionAndClearOnReconsidered() {
        List<BillAction> actions = new LinkedList<>();
        addTestAction(actions, "PRINT NUMBER 9000A");
        addTestAction(actions, "SUBSTITUTED BY A1234");
        addTestAction(actions, "SUBSTITUTION RECONSIDERED");

        XmlBillActionAnalyzer analyzer = new XmlBillActionAnalyzer(testBillId, actions, Optional.of(defaultPubStatus));
        analyzer.analyze();

        assertTrue(analyzer.getSameAsMap().containsKey(Version.A));
        assertEquals(new BillId("A1234", testYear), analyzer.getSameAsMap().get(Version.A));
        assertNull(analyzer.getSubstitutedBy());
    }

    @Test
    public void analyzeShouldSetStrickenFlag() {
        List<BillAction> actions = new LinkedList<>();
        addTestAction(actions, "ENACTING CLAUSE STRICKEN");

        XmlBillActionAnalyzer analyzer = new XmlBillActionAnalyzer(testBillId, actions, Optional.of(defaultPubStatus));
        analyzer.analyze();

        assertTrue(analyzer.isStricken());
        assertEquals(STRICKEN, analyzer.getBillStatus().getStatusType());
    }

    /* --- Internal Methods --- */

    private void analyzeAndVerifyStatus(List<BillAction> actions, BillStatusType expectedType) {
        XmlBillActionAnalyzer analyzer = new XmlBillActionAnalyzer(testBillId, actions, Optional.of(defaultPubStatus));
        analyzer.analyze();
        assertEquals("Action analyzer produced unexpected bill status",
                expectedType, analyzer.getBillStatus().getStatusType());
    }

    private void addTestAction(List<BillAction> actions, String actionText) {
        BillAction billAction = new BillAction(
                LocalDate.ofYearDay(testYear, 1), actionText, testBillId.getChamber(),
                actions.size(), testBillId);
        actions.add(billAction);
    }
}
