package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextSpotcheckReference;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

/**
 * Created by kyle on 2/19/15.
 */
@Service
public class BillTextCheckService implements SpotCheckService<BaseBillId, Bill, BillTextSpotcheckReference>{
    private static final Logger logger = Logger.getLogger(BillTextCheckService.class);

    @Autowired
    BillDataService billDataService;

    @PostConstruct
    public void init(){

    }

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill content) throws ReferenceDataNotFoundEx, NotImplementedException {

        return null;
    }

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx, NotImplementedException {
        return null;
    }

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill bill, BillTextSpotcheckReference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("BillTextSpotcheckReference cannot be null when performing spot check");
        }

        BaseBillId baseBillId = bill.getBaseBillId();
        SpotCheckReferenceId referenceId = reference.getReferenceId();

        final SpotCheckObservation<BaseBillId> observation = new SpotCheckObservation<>(referenceId, baseBillId);
        //Add mismatches to observation
        checkBillText(bill, reference, observation);
        checkMemoText(bill, reference, observation);
        checkSessionYear(bill, reference, observation);
        checkAmendment(bill, reference, observation);


        //return new SpotCheckObservation<BaseBillId>(new SpotCheckReferenceId(SpotCheckRefType.LBDC_BILL,
        //        content.getPublishedDateTime()), content.getBaseBillId());  // x = new SpotCheckObservation<BaseBillId>();
        return observation;
    }


    public void checkBillText(Bill bill, BillTextSpotcheckReference reference, SpotCheckObservation<BaseBillId> obsrv){
        if (!stringEquals(reference.getText(), bill.getFullText(), false, true)){
            obsrv.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.BILL_FULL_TEXT, reference.getText(), bill.getFullText()));
        }
    }
    public void checkMemoText(Bill bill, BillTextSpotcheckReference reference, SpotCheckObservation<BaseBillId> obsrv){
        if (!stringEquals(reference.getMemo(), bill.getFullText(), false, true)){
            obsrv.addMismatch(new SpotCheckMismatch((SpotCheckMismatchType.BILL_MEMO),
                    reference.getMemo(), bill.getActiveAmendment().getMemo()));
        }
    }
    public void checkSessionYear(Bill bill, BillTextSpotcheckReference reference, SpotCheckObservation<BaseBillId> obsrv){
        if (!(reference.getSessionYear() == Integer.parseInt(bill.getSession().toString()))){
            obsrv.addMismatch(new SpotCheckMismatch((SpotCheckMismatchType.BILL_SESSION_YEAR),
                    Integer.toString(reference.getSessionYear()), bill.getSession().toString()));
        }

    }
    public void checkAmendment(Bill bill, BillTextSpotcheckReference reference, SpotCheckObservation<BaseBillId> obsrv){
        if (!stringEquals(reference.getAmendment().toString(), bill.getActiveVersion().toString(), false, true)){
            obsrv.addMismatch(new SpotCheckMismatch((SpotCheckMismatchType.BILL_ACTIVE_AMENDMENT),
                    reference.getAmendment().toString(), bill.getActiveAmendment().toString()));
        }
    }

    /**
     * Compare two strings a and b with the option to ignore case and extra whitespace.
     */
    protected boolean stringEquals(String a, String b, boolean ignoreCase, boolean normalizeSpaces) {
        // Convert null values to empty strings.
        a = (a == null) ? "" : a;
        b = (b == null) ? "" : b;
        // Remove excess spaces if requested
        if (normalizeSpaces) {
            a = a.replaceAll("\\s+", " ");
            b = b.replaceAll("\\s+", " ");
        }
        return (ignoreCase) ? StringUtils.equalsIgnoreCase(a, b) : StringUtils.equals(a,b);
    }


}












