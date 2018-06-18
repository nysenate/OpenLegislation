package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.agenda.CommAgendaIdView;
import gov.nysenate.openleg.client.view.bill.BillActionView;
import gov.nysenate.openleg.client.view.bill.BillAmendmentView;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

/**
 * Created by Chenguang He on 2017/3/22.
 *  This service use to compare the difference between two branches of Openleg.
 *  It requires to pass in an API key to enable the comparision.
 *
 */
@Service("openlegBillCheck")
public class OpenlegBillCheckService extends BaseSpotCheckService<BaseBillId, BillView, BillView> {

    public SpotCheckObservation<BaseBillId> check(BillView content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException("");
    }

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
        final SpotCheckObservation<BaseBillId> observation = new SpotCheckObservation<BaseBillId>(
                new SpotCheckReferenceId(SpotCheckRefType.OPENLEG_BILL, LocalDateTime.now()),
                reference.toBaseBillId());
        if (content.getActiveVersion().equals(reference.getActiveVersion() ) ) {
            checkBillTitle(content, reference, observation);
            checkBillSummary(content, reference, observation);
            checkBillLawSection(content, reference, observation);
            checkBillActions(content, reference, observation);
            checkBillSponsor(content, reference, observation);
            checkBillYear(content, reference, observation);
            checkBillStatus(content, reference, observation);
            checkAdditionalSponsors(content, reference, observation);
            checkCoSponsors(content,reference,observation);
            checkMultisponsors(content, reference, observation);
            checkBillApproveMessage(content, reference, observation);
            checkVotes(content, reference, observation);
            checkCalendars(content, reference, observation);
            checkBillCommitteeAgendas(content, reference, observation);
            checkBillPastCommmittee(content, reference, observation);
        }
        else {
            checkActiveVersion(content, reference, observation);
        }
        return observation;
    }

    protected void checkBillTitle(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        checkString(content.getTitle(), reference.getTitle(), obsrv, BILL_TITLE);
    }

    protected void checkActiveVersion(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        checkString(content.getActiveVersion(),reference.getActiveVersion(),obsrv,BILL_ACTIVE_AMENDMENT);
    }

    protected void checkBillSummary(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        checkString(content.getSummary(), reference.getSummary(), obsrv,BILL_SUMMARY );
    }

    protected void checkBillLawSection(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        BillAmendmentView contentLatestAmendment = content.getAmendments().getItems().get(content.getActiveVersion());
        BillAmendmentView referenceLatestAmendment = reference.getAmendments().getItems().get(reference.getActiveVersion());
        if (referenceLatestAmendment != null && contentLatestAmendment != null) {
            checkString(removeExcessWhitespace(contentLatestAmendment.getLawCode()),
                    removeExcessWhitespace(referenceLatestAmendment.getLawCode())
                            .replaceAll("Â§", "§"),
                    obsrv, BILL_LAW_CODE);
            checkString(contentLatestAmendment.getLawSection().trim(),
                    referenceLatestAmendment.getLawSection().trim().
                            replaceAll("Â§", "§")
                            .replaceAll(" +", " "),obsrv,BILL_LAW_SECTION);
        }
    }

    protected void checkBillActions(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        TreeMap<String, String> contentActionVersionDateMap = getActionVersionDateMap(content.getActions().getItems());
        TreeMap<String, String> referenceActionVersionDateMap = getActionVersionDateMap(reference.getActions().getItems());

        checkString(contentActionVersionDateMap.toString(),referenceActionVersionDateMap.toString(),obsrv,BILL_ACTION);

        List<BillActionView> contentBillActions = content.getActions().getItems();
        List<BillActionView> referenceBillActions = content.getActions().getItems();
        if ( contentBillActions.size() == referenceBillActions.size() ) {
            for (int index = 0; index < contentBillActions.size(); index++) {
                checkObject(contentBillActions.get(index), referenceBillActions.get(index), obsrv, BILL_ACTION);
            }
        }
        else {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTION, contentBillActions.size(), referenceBillActions.size()));
        }
    }

    protected void checkBillSponsor(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        checkString(OutputUtils.toJson(content.getSponsor()),OutputUtils.toJson(reference.getSponsor()),obsrv,BILL_SPONSOR);
    }

    protected void checkAdditionalSponsors(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        checkString(OutputUtils.toJson(content.getAdditionalSponsors()),OutputUtils.toJson(reference.getAdditionalSponsors()), obsrv, BILL_ADDITIONAL_SPONSOR_OPENLEG);
    }

    protected void checkCoSponsors(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        boolean referenceCosponsors = true;
        boolean contentCosponsors = true;

        try {
            content.getAmendments()
                    .getItems()
                    .get(content.getActiveVersion())
                    .getCoSponsors();
        }
        catch (NullPointerException e) {
            contentCosponsors = false;
        }

        try {
            reference.getAmendments()
                    .getItems()
                    .get(reference.getActiveVersion())
                    .getCoSponsors();
        }
        catch (NullPointerException e) {
            contentCosponsors = false;
        }

        if (contentCosponsors && referenceCosponsors)  {
            checkString(OutputUtils.toJson(content.getAmendments()
                            .getItems()
                            .get(content.getActiveVersion())
                            .getCoSponsors()),
                    OutputUtils.toJson(reference.getAmendments().
                            getItems()
                            .get(reference.getActiveVersion())
                            .getCoSponsors()),
                    obsrv, BILL_COSPONSOR);
        }
        else if (!contentCosponsors && referenceCosponsors) {
            checkString(OutputUtils.toJson(""),
                    OutputUtils.toJson(reference.getAmendments().
                            getItems()
                            .get(reference.getActiveVersion())
                            .getCoSponsors()),
                    obsrv, BILL_COSPONSOR);
        }
        else if (contentCosponsors && !referenceCosponsors) {
            checkString(OutputUtils.toJson(content.getAmendments()
                            .getItems()
                            .get(content.getActiveVersion())
                            .getCoSponsors()),
                    OutputUtils.toJson(""),
                    obsrv, BILL_COSPONSOR);
        }

    }

    protected void checkMultisponsors(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        boolean referenceMultisponsors = true;
        boolean contentMultisponsors = true;

        try {
            content.getAmendments()
                    .getItems()
                    .get(content.getActiveVersion())
                    .getMultiSponsors();
        }
        catch (NullPointerException e) {
            contentMultisponsors = false;
        }

        try {
            reference.getAmendments()
                    .getItems()
                    .get(reference.getActiveVersion())
                    .getMultiSponsors();
        }
        catch (NullPointerException e) {
            contentMultisponsors= false;
        }

        if (contentMultisponsors && referenceMultisponsors)  {
            checkString(OutputUtils.toJson(content.getAmendments()
                            .getItems()
                            .get(content.getActiveVersion())
                            .getMultiSponsors()),
                    OutputUtils.toJson(reference.getAmendments().
                            getItems()
                            .get(reference.getActiveVersion())
                            .getMultiSponsors()),
                    obsrv, BILL_MULTISPONSOR);
        }
        else if (!contentMultisponsors && referenceMultisponsors) {
            checkString(OutputUtils.toJson(""),
                    OutputUtils.toJson(reference.getAmendments().
                            getItems()
                            .get(reference.getActiveVersion())
                            .getMultiSponsors()),
                    obsrv, BILL_MULTISPONSOR);
        }
        else if (contentMultisponsors && !referenceMultisponsors) {
            checkString(OutputUtils.toJson(content.getAmendments()
                            .getItems()
                            .get(content.getActiveVersion())
                            .getCoSponsors()),
                    OutputUtils.toJson(""),
                    obsrv, BILL_MULTISPONSOR);
        }
    }

    protected void checkBillYear(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        checkString(String.valueOf( content.getSession() ), String.valueOf( reference.getSession() ),obsrv,BILL_SESSION_YEAR);
    }


    protected void checkBillStatus(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        checkString(OutputUtils.toJson(content.getStatus()),OutputUtils.toJson(reference.getStatus()), obsrv, BILL_LAST_STATUS );
    }

    protected void checkBillApproveMessage(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        String contentApprMemo;
        String referenceApprMemo;
        try {
            contentApprMemo = removeExcessWhitespace(content.getApprovalMessage().getText().replaceAll("\n","").replaceAll("\\\\n", "\n"));
            referenceApprMemo = removeExcessWhitespace(reference.getApprovalMessage().getText().replaceAll("\n","").replaceAll("\\\\n", "\n"));
            checkString(OutputUtils.toJson(contentApprMemo), OutputUtils.toJson(referenceApprMemo),obsrv,BILL_APPROVE_MESSAGE_OPENLEG );
        }
        catch (NullPointerException e) {
            //The Bill did not have an appr memo no need to do anything
        }
    }

    protected void checkVotes(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        checkString(OutputUtils.toJson(content.getVotes()),  OutputUtils.toJson(reference.getVotes()), obsrv,BILL_VOTES_OPENLEG);
    }

    protected void checkCalendars(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        checkString(OutputUtils.toJson(content.getCalendars()),OutputUtils.toJson(reference.getCalendars()), obsrv,CALENDAR_OPENLEG);
    }

    protected void checkBillCommitteeAgendas(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        ArrayList<CommAgendaIdView> contentCommAgendaIds = new ArrayList<>();
        ArrayList<CommAgendaIdView> referenceCommAgendaIds = new ArrayList<>();

        contentCommAgendaIds.addAll( content.getCommitteeAgendas().getItems() );
        referenceCommAgendaIds.addAll( reference.getCommitteeAgendas().getItems() );

        for (CommAgendaIdView refView: referenceCommAgendaIds) {
            boolean matched = false;

            for (CommAgendaIdView contentView: contentCommAgendaIds) {
                boolean commName = false;
                boolean chamber = false;
                boolean number = false;
                boolean year = false;

                if (refView.getCommitteeId().getName().equals(contentView.getCommitteeId().getName())) {
                    commName = true;
                }

                if (refView.getCommitteeId().getChamber().equals(contentView.getCommitteeId().getChamber())) {
                    chamber = true;
                }

                if (refView.getAgendaId().getNumber() == contentView.getAgendaId().getNumber()) {
                    number = true;
                }

                if (refView.getAgendaId().getYear() == contentView.getAgendaId().getYear()) {
                    year = true;
                }

                if (commName && chamber && number && year) {
                    matched = true;
                }
            }

            if (!matched) {
                checkString(OutputUtils.toJson(contentCommAgendaIds), OutputUtils.toJson(referenceCommAgendaIds),
                        obsrv,BILL_COMMITTEE_AGENDAS_OPENLEG );
                return;
            }
        }


    }

    protected void checkBillPastCommmittee(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        checkString(OutputUtils.toJson(content.getPastCommittees()),OutputUtils.toJson(reference.getPastCommittees()), obsrv,BILL_PAST_COMMITTEE_OPENLEG);
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
                actionVersionDateMap.replace(date,version);
            }

        }
        return actionVersionDateMap;
    }

    private String removeExcessWhitespace(String input) {
        return StringUtils.deleteWhitespace(input);
    }
}