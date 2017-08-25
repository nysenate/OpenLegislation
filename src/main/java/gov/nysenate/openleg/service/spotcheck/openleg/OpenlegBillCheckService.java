package gov.nysenate.openleg.service.spotcheck.openleg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

/**
 * Created by Chenguang He on 2017/3/22.
 *  This service use to compare the difference between two branches of Openleg.
 *  It requires to pass in an API key to enable the comparision.
 *
 */
@Service("openlegBillCheck")
public class OpenlegBillCheckService extends BaseSpotCheckService<BaseBillId, BillView, BillView> {
    @Override
    public SpotCheckObservation<BaseBillId> check(BillView content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

    @Override
    public SpotCheckObservation<BaseBillId> check(BillView content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

    /**
     * Check the mismatch between openleg sobi-processing and xml-data-processing Bills
     * @param content ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return The mismatches
     */
    @Override
    public SpotCheckObservation<BaseBillId> check(BillView content, BillView reference) {
        final SpotCheckObservation<BaseBillId> observation = new SpotCheckObservation<BaseBillId>(reference.toBaseBillId());
        checkBillTitle(content, reference, observation);
        checkViewType(content, reference, observation);
        checkActiveVersion(content, reference, observation);
        checkBillBasePrintNo(content, reference, observation);
        checkBillBasePrintNoNoStr(content, reference, observation);
        checkBillSummary(content, reference, observation);
        checkBillActions(content, reference, observation);
        checkBillSponsor(content, reference, observation);
        checkBillYear(content, reference, observation);
        checkBillStatus(content, reference, observation);
        checkAdditionalSponsors(content, reference, observation);
        checkAmendment(content, reference, observation);
        checkAmendmentVersions(content, reference, observation);
        checkBillApproveMessage(content, reference, observation);
        checkVotes(content, reference, observation);
        checkCalendars(content, reference, observation);
        checkBillCommitteeAgendas(content, reference, observation);
        checkBillPastCommmittee(content, reference, observation);
        return observation;
    }

    protected void checkBillTitle(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        if (!stringEquals(content.getTitle(), reference.getTitle(), false, true))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_TITLE_OPENLEG_DEV, content.getTitle(), reference.getTitle()));
    }

    protected void checkViewType(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        if (!stringEquals(content.getViewType(), reference.getViewType(), false, true))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_VIEW_TYPE_OPENLEG_DEV, content.getViewType(), reference.getViewType()));
    }

    protected void checkActiveVersion(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        if (!stringEquals(content.getActiveVersion(), reference.getActiveVersion(), false, true))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTIVE_VERSION_OPENLEG_DEV, content.getActiveVersion(), reference.getActiveVersion()));
    }

    protected void checkBillBasePrintNo(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        if (!stringEquals(content.getBasePrintNo(), reference.getBasePrintNo(), false, true))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_BASE_PRINT_NO_OPENLEG_DEV, content.getBasePrintNo(), reference.getBasePrintNo()));
    }

    protected void checkBillBasePrintNoNoStr(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        if (!stringEquals(content.getBasePrintNoStr(), reference.getBasePrintNoStr(), false, true))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_BASE_PRINT_NO_NoStr_OPENLEG_DEV, content.getBasePrintNoStr(), reference.getBasePrintNoStr()));
    }

    protected void checkBillSummary(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        if (!stringEquals(content.getSummary(), reference.getSummary(), false, true))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_SUMMARY_OPENLEG_DEV, content.getSummary(), reference.getSummary()));
    }/**/

    protected void checkBillActions(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getActions(), true);
        String reference_str = serialize(reference.getActions(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_SPONSOR_OPENLEG_DEV, content_str, reference_str));
    }

    protected void checkBillSponsor(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getSponsor(), true);
        String reference_str = serialize(reference.getSponsor(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_SPONSOR_OPENLEG_DEV, content_str, reference_str));
    }

    protected void checkBillYear(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getYear(), true);
        String reference_str = serialize(reference.getYear(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_SESSION_YEAR_OPENLEG_DEV, content_str, reference_str));
    }


    protected void checkBillStatus(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getStatus(), true);
        String reference_str = serialize(reference.getStatus(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_LAST_STATUS_OPENLEG_DEV, content_str, reference_str));
    }

    protected void checkAdditionalSponsors(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getAdditionalSponsors(), true);
        String reference_str = serialize(reference.getAdditionalSponsors(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(Bill_ADDITIONAL_SPONSOR_OPENLEG_DEV, content_str, reference_str));
    }

    protected void checkAmendment(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getAmendments(), true);
        String reference_str = serialize(reference.getAmendments(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTIVE_AMENDMENT_OPENLEG_DEV, content_str, reference_str));
    }

    protected void checkAmendmentVersions(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getAmendmentVersions(), true);
        String reference_str = serialize(reference.getAmendmentVersions(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_AMENDMENT_VERSION_OPENLEG_DEV, content_str, reference_str));
    }

    protected void checkBillApproveMessage(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getApprovalMessage(), true);
        String reference_str = serialize(reference.getApprovalMessage(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_APPROVE_MESSAGE_OPENLEG_DEV, content_str, reference_str));
    }

    protected void checkVotes(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getVotes(), true);
        String reference_str = serialize(reference.getVotes(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_VOTES_OPENLEG_DEV, content_str, reference_str));
    }

    protected void checkCalendars(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getCalendars(), true);
        String reference_str = serialize(reference.getCalendars(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(CALENDAR_OPENLEG_DEV, content_str, reference_str));
    }

    protected void checkBillCommitteeAgendas(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getCommitteeAgendas(), true);
        String reference_str = serialize(reference.getCommitteeAgendas(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_COMMITTEE_AGENDAS_OPENLEG_DEV, content_str, reference_str));
    }

    protected void checkBillPastCommmittee(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = serialize(content.getPastCommittees(), true);
        String reference_str = serialize(reference.getPastCommittees(), true);
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_PAST_COMMITTEE_OPENLEG_DEV, content_str, reference_str));
    }

    /**
     * Serialize a complex object to JSON and compare the mismatches of JSON strings.
     * @param obj a complex object
     * @param pretty pretty print of json string
     * @return serialized Json string
     */
    private String serialize(Object obj, boolean pretty) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        try {
            if (pretty) {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                return mapper.writeValueAsString(obj);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected boolean stringEquals(String a, String b, boolean ignoreCase, boolean normalizeSpaces) {
        // Convert null values to empty strings.
        a = (a == null) ? "" : a;
        b = (b == null) ? "" : b;
        // Remove excess spaces if requested
        if (normalizeSpaces) {
            a = a.replaceAll("\\s+", " ");
            b = b.replaceAll("\\s+", " ");
        }
        return (ignoreCase) ? StringUtils.equalsIgnoreCase(a, b) : StringUtils.equals(a, b);
    }
}
