package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.client.view.bill.BillInfoView;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

/**
 * Created by kyle on 2/19/15.
 */
@Service
public class BillTextCheckService implements SpotCheckService<BaseBillId, Bill, BillTextReference>{
    private static final Logger logger = Logger.getLogger(BillTextCheckService.class);

    @Autowired
    BillDataService billDataService;

    @PostConstruct
    public void init(){

    }

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill content, LocalDateTime start, LocalDateTime end)
            throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill bill, BillTextReference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("BillTextSpotcheckReference cannot be null when performing spot check");
        }

        BaseBillId baseBillId = bill.getBaseBillId();
        SpotCheckReferenceId referenceId = reference.getReferenceId();

        final SpotCheckObservation<BaseBillId> observation = new SpotCheckObservation<>(referenceId, baseBillId);

        //Add mismatches to observation
        if (reference.isNotFound()) {
            observation.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING, "", reference.getText()));
        } else {
            checkAmendment(bill, reference, observation);
            if (bill.hasAmendment(reference.getActiveVersion())) {
                BillAmendment amendment = bill.getAmendment(reference.getActiveVersion());
                checkBillText(amendment, reference, observation);
                // Only check senate, non-resolution bills for sponsor memos
                // Todo find a better way of checking memo text
                //  currently, memos are sent daily in batches and are not guaranteed to be present in sobi data if on lrs
                //  also, memos are formatted a bit differently
//                if (Chamber.SENATE.equals(baseBillId.getChamber()) && !baseBillId.getBillType().isResolution()) {
//                    checkMemoText(amendment, reference, observation);
//                }
            }
        }
        return observation;
    }

    private void checkAmendment(Bill bill, BillTextReference reference, SpotCheckObservation<BaseBillId> obsrv) {
        if (bill.getActiveVersion() == null || !bill.getActiveVersion().equals(reference.getActiveVersion())) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTIVE_AMENDMENT,
                    bill.getActiveVersion(), reference.getActiveVersion()));
        }
    }

    /**
     * Checks text with all whitespace removed, and generates several mismatches with different levels of text
     * normalization if there was a mismatch in the no-whitespace text
     */
    private void checkBillText(BillAmendment billAmendment, BillTextReference reference, SpotCheckObservation<BaseBillId> obsrv){
        String dataText = billAmendment.getFullText();
        String refText = reference.getText();
        String strippedDataText = stripNonAlpha(dataText);
        String strippedRefText = stripNonAlpha(refText);
        // Check normalized text and report on non-normalized text as well if there is a mismatch
        if (!StringUtils.equals(strippedRefText, strippedDataText)) {
            String pureContentRefText = stripNonContent(refText);
            String pureContentDataText = stripNonContent(dataText);
            if (!StringUtils.equals(pureContentRefText, pureContentDataText)) {
                obsrv.addMismatch(new SpotCheckMismatch(BILL_TEXT_CONTENT, dataText, refText));
            } else {
                obsrv.addMismatch(new SpotCheckMismatch(BILL_TEXT_LINE_OFFSET, dataText, refText));
            }
        }
    }

    private void checkMemoText(BillAmendment billAmendment, BillTextReference reference, SpotCheckObservation<BaseBillId> obsrv){
        String dataMemo = billAmendment.getMemo();
        String refMemo = reference.getMemo();
        if (!StringUtils.equalsIgnoreCase(dataMemo, refMemo)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_MEMO, dataMemo, refMemo));
        }
    }

    /**
     * Removes all non alpha characters
     */
    private String stripNonAlpha(String text) {
        return text.replaceAll("(?:[^\\w]|_)+", "");
    }

    static String lineNumberRegex = "(?:^( {4}\\d| {3}\\d\\d))";
    static String pageMarkerRegex = "^ {7}[A|S]\\. \\d+(--[A-Z])?[ ]+\\d+([ ]+[A|S]\\. \\d+(--[A-Z])?)?$";
    static String budgetPageMargerRegex = "^[ ]{42,43}\\d+[ ]+\\d+-\\d+-\\d+$";
    static String explanationRegex = "^[ ]+EXPLANATION--Matter in ITALICS \\(underscored\\) is new; matter in brackets\\n";
    static String explanationRegex2 = "^[ ]+\\[ ] is old law to be omitted.\\n[ ]+LBD\\d+-\\d+-\\d+$";
    static String ultraNormalizeRegex = "(?m)" + String.join("|", Arrays.asList(
            lineNumberRegex, pageMarkerRegex, budgetPageMargerRegex, explanationRegex, explanationRegex2));
    /**
     * Removes all whitespace, line numbers, and page numbers
     */
    private String stripNonContent(String text) {
        String stripped = text.replaceAll(ultraNormalizeRegex, "");
        return stripNonAlpha(stripped);
    }

}
