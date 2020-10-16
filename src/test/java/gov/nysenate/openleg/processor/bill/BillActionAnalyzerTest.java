package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillStatusType;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static gov.nysenate.openleg.model.bill.BillStatusType.INTRODUCED;
import static gov.nysenate.openleg.model.bill.BillStatusType.LOST;
import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class BillActionAnalyzerTest {

    private int testYear = 1999;
    private BillId testBillId = new BaseBillId("S9000", testYear);
    private PublishStatus defaultPubStatus =
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

    /* --- Internal Methods --- */

    private void analyzeAndVerifyStatus(List<BillAction> actions, BillStatusType expectedType) {
        BillActionAnalyzer analyzer = new BillActionAnalyzer(testBillId, actions, Optional.of(defaultPubStatus));
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
