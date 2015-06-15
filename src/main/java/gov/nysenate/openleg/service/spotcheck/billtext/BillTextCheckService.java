package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.client.view.bill.BillInfoView;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
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
            observation.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING,
                    reference.getBaseBillId() + "\n" + reference.getText(),
                    OutputUtils.toJson(new BillInfoView(bill.getBillInfo()))));
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
                    reference.getActiveVersion(), bill.getActiveAmendment()));
        }
    }

    /**
     * Checks text with all whitespace removed, and generates several mismatches with different levels of text
     * normalization if there was a mismatch in the no-whitespace text
     */
    private void checkBillText(BillAmendment billAmendment, BillTextReference reference, SpotCheckObservation<BaseBillId> obsrv){
        String dataText = billAmendment.getFullText();
        String refText = reference.getText();
        String normalizedDataText = normalizeText(dataText);
        String normalizedRefText = normalizeText(refText);
        String superNormalizedDataText = superNormalizeText(dataText);
        String superNormalizedRefText = superNormalizeText(refText);
        // Check normalized text and report on non-normalized text as well if there is a mismatch
        if (!StringUtils.equalsIgnoreCase(superNormalizedRefText, superNormalizedDataText)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_FULL_TEXT, refText, dataText));
            obsrv.addMismatch(new SpotCheckMismatch(BILL_FULL_TEXT_NORMALIZED,
                    normalizedRefText, normalizedDataText));
            obsrv.addMismatch(new SpotCheckMismatch(BILL_FULL_TEXT_SUPER_NORMALIZED,
                    superNormalizedRefText, superNormalizedDataText));
            String ultraNormRefText = ultraNormalizeText(refText, reference.getBillId());
            String ultraNormDataText = ultraNormalizeText(dataText, billAmendment.getBillId());
            if (!StringUtils.equalsIgnoreCase(ultraNormRefText, ultraNormDataText)) {
                obsrv.addMismatch(new SpotCheckMismatch(BILL_FULL_TEXT_ULTRA_NORMALIZED,
                        ultraNormRefText, ultraNormDataText));
            }
        }
    }

    private void checkMemoText(BillAmendment billAmendment, BillTextReference reference, SpotCheckObservation<BaseBillId> obsrv){
        String dataMemo = billAmendment.getMemo();
        String refMemo = reference.getMemo();
        if (!StringUtils.equalsIgnoreCase(dataMemo, refMemo)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_MEMO, refMemo, dataMemo));
        }
    }

    /**
     * Removes duplicate spaces and trims leading/trailing spaces for each line
     */
    private String normalizeText(String text) {
        text = text.replaceAll("[ ]+", " ");
        text = text.replaceAll("(?<=\n)[ ]+|[ ]+(?=\n)", "");
        return text;
    }

    /**
     * Removes all whitespace
     */
    private String superNormalizeText(String text) {
        return text.replaceAll("[^\\w]+", "");
    }

    /**
     * Removes all whitespace, line numbers, and page numbers
     */
    private String ultraNormalizeText(String text, BillId billId) {
        String pageMarkerRegex = String.format("(?<=\n)%s. %d%s \\d+(?=\n)", billId.getBillType(), billId.getNumber(),
                (BillId.isBaseVersion(billId.getVersion()) ? "" : "--") + billId.getVersion());
        return superNormalizeText(
                normalizeText(text)
                        .replaceAll("(?<=\n)\\d{1,2} ", "")
                        .replaceAll(pageMarkerRegex, ""));
    }

}
