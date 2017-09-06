package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.bill.BillActionView;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

import static gov.nysenate.openleg.model.base.Version.after;
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
    public SpotCheckObservation<BaseBillId>  check(BillView content, BillView reference) {
        final SpotCheckObservation<BaseBillId> observation = new SpotCheckObservation<BaseBillId>(reference.toBaseBillId());
        checkBillTitle(reference, content, observation);
        checkActiveVersion(reference, content, observation);
        checkBillSummary(reference, content, observation);
        checkBillActions(reference, content, observation);
        checkBillSponsor(reference, content, observation);
        checkBillYear(reference, content, observation);
        checkBillStatus(reference, content, observation);
        checkAdditionalSponsors(reference, content, observation);
        checkBillApproveMessage(reference, content, observation);
        checkVotes(reference, content, observation);
        checkCalendars(reference, content, observation);
        checkBillCommitteeAgendas(reference, content, observation);
        checkBillPastCommmittee(reference, content, observation);
        return observation;
    }

    protected void checkBillTitle(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        if (!stringEquals(content.getTitle(), reference.getTitle(), false, true))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_TITLE, content.getTitle(), reference.getTitle()));
    }

    protected void checkActiveVersion(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        if ( !content.getActiveVersion().equals(reference.getActiveVersion() )  )
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTIVE_AMENDMENT, content.getActiveVersion(), reference.getActiveVersion()));
    }

    protected void checkBillSummary(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        if (!stringEquals(content.getSummary(), reference.getSummary(), false, true))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_SUMMARY, content.getSummary(), reference.getSummary()));
    }/**/

    protected void checkBillActions(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        TreeMap<String, String> contentActionVersionDateMap = getActionVersionDateMap(content.getActions().getItems());
        TreeMap<String, String> referenceActionVersionDateMap = getActionVersionDateMap(reference.getActions().getItems());

        String content_str = contentActionVersionDateMap.toString();
        String reference_str = referenceActionVersionDateMap.toString();
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTION, content_str, reference_str));

        List<BillActionView> contentBillActions = content.getActions().getItems();
        List<BillActionView> referenceBillActions = content.getActions().getItems();
        if ( contentBillActions.size() == referenceBillActions.size() ) {
            for (int index = 0; index < contentBillActions.size(); index++) {
                if (!contentBillActions.get(index).equals(referenceBillActions.get(index))) {
                    obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTION, contentBillActions.get(index), referenceBillActions.get(index)));
                }
            }
        }
        else {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTION, contentBillActions.size(), referenceBillActions.size()));
        }
    }

    private TreeMap<String, String> getActionVersionDateMap(List<BillActionView> billActions) {
        TreeMap<String, String> actionVersionDateMap = new TreeMap<>();

        for (BillActionView billAction: billActions) {
            String version = billAction.getBillId().getVersion();
            String date = billAction.getDate();

            if (!actionVersionDateMap.containsKey(date)) {
                actionVersionDateMap.put(date, version);
            }
            else if (actionVersionDateMap.containsKey(date) ) {
                List<Version> versionsAfter = after( Version.of( actionVersionDateMap.get(date) ) );

                if (  versionsAfter.contains( Version.of(version) )  ) {
                    actionVersionDateMap.replace(date,version);
                }
            }

        }
        return actionVersionDateMap;
    }

    protected void checkBillSponsor(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = OutputUtils.toJson(content.getSponsor());
        String reference_str = OutputUtils.toJson(reference.getSponsor());
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_SPONSOR, content_str, reference_str));
    }

    protected void checkBillYear(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = String.valueOf( content.getSession() );
        String reference_str = String.valueOf( reference.getSession() );
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_SESSION_YEAR, content_str, reference_str));
    }


    protected void checkBillStatus(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = OutputUtils.toJson(content.getStatus());
        String reference_str = OutputUtils.toJson(reference.getStatus());
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_LAST_STATUS, content_str, reference_str));
    }

    protected void checkAdditionalSponsors(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = OutputUtils.toJson(content.getAdditionalSponsors());
        String reference_str = OutputUtils.toJson(reference.getAdditionalSponsors());
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(Bill_ADDITIONAL_SPONSOR_OPENLEG, content_str, reference_str));
    }

    protected void checkBillApproveMessage(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = OutputUtils.toJson(content.getApprovalMessage());
        String reference_str = OutputUtils.toJson(reference.getApprovalMessage());
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_APPROVE_MESSAGE_OPENLEG, content_str, reference_str));
    }

    protected void checkVotes(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = OutputUtils.toJson(content.getVotes());
        String reference_str = OutputUtils.toJson(reference.getVotes());
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_VOTES_OPENLEG, content_str, reference_str));
    }

    protected void checkCalendars(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = OutputUtils.toJson(content.getCalendars());
        String reference_str = OutputUtils.toJson(reference.getCalendars());
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(CALENDAR_OPENLEG, content_str, reference_str));
    }

    protected void checkBillCommitteeAgendas(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = OutputUtils.toJson(content.getCommitteeAgendas());
        String reference_str = OutputUtils.toJson(reference.getCommitteeAgendas());
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_COMMITTEE_AGENDAS_OPENLEG, content_str, reference_str));
    }

    protected void checkBillPastCommmittee(BillView reference, BillView content, SpotCheckObservation<BaseBillId> obsrv) {
        String content_str = OutputUtils.toJson(content.getPastCommittees());
        String reference_str = OutputUtils.toJson(reference.getPastCommittees());
        if (!content_str.equals(reference_str))
            obsrv.addMismatch(new SpotCheckMismatch(BILL_PAST_COMMITTEE_OPENLEG, content_str, reference_str));
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
