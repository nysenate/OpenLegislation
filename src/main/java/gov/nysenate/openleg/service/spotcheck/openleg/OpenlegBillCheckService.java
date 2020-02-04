package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.agenda.CommAgendaIdView;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.bill.*;
import gov.nysenate.openleg.client.view.calendar.CalendarIdView;
import gov.nysenate.openleg.client.view.committee.CommitteeIdView;
import gov.nysenate.openleg.client.view.committee.CommitteeVersionIdView;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillVoteType;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

/**
 * Created by Chenguang He on 2017/3/22.
 * This service use to compare the difference between two branches of Openleg.
 * It requires to pass in an API key to enable the comparision.
 */
@Service("openlegBillCheck")
public class OpenlegBillCheckService implements SpotCheckService<BaseBillId, BillView, BillView> {

    @Autowired private SpotCheckUtils spotCheckUtils;

    /**
     * Check the mismatch between openleg sobi-processing and xml-data-processing Bills
     *
     * @param content   ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return The mismatches
     */
    @Override
    public SpotCheckObservation<BaseBillId> check(BillView content, BillView reference) {
        final SpotCheckObservation<BaseBillId> observation = new SpotCheckObservation<>(
                new SpotCheckReferenceId(SpotCheckRefType.OPENLEG_BILL, LocalDateTime.now()),
                reference.toBaseBillId());
        if (content.getActiveVersion().equals(reference.getActiveVersion())) {
            checkBillTitle(content, reference, observation);
            checkBillSummary(content, reference, observation);
            checkLawCode(content, reference, observation);
            checkBillLawSection(content, reference, observation);
            checkBillActions(content, reference, observation);
            checkBillSponsor(content, reference, observation);
            checkBillYear(content, reference, observation);
            checkBillStatus(content, reference, observation);
            checkAdditionalSponsors(content, reference, observation);
            checkCoSponsors(content, reference, observation);
            checkMultisponsors(content, reference, observation);
            checkBillApproveMessage(content, reference, observation);
            checkVotes(content, reference, observation);
            checkCalendars(content, reference, observation);
            checkBillCommitteeAgendas(content, reference, observation);
            checkBillPastCommmittee(content, reference, observation);
        } else {
            checkActiveVersion(content, reference, observation);
        }
        return observation;
    }

    protected void checkBillTitle(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkString(
                StringUtils.normalizeSpace(content.getTitle()),
                StringUtils.normalizeSpace(reference.getTitle()),
                obsrv, BILL_TITLE);
    }

    protected void checkActiveVersion(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkString(content.getActiveVersion(), reference.getActiveVersion(), obsrv, BILL_ACTIVE_AMENDMENT);
    }

    protected void checkBillSummary(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkString(
                StringUtils.normalizeSpace(content.getSummary()),
                StringUtils.normalizeSpace(reference.getSummary()),
                obsrv, BILL_SUMMARY);
    }

    private String getLawCode(BillView billView) {
        return getActiveAmendOpt(billView)
                .map(BillAmendmentView::getLawCode)
                .map(StringUtils::deleteWhitespace)
                .map(lawCode -> lawCode.replaceAll("Â§", "§"))
                .orElse(null);
    }

    protected void checkLawCode(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obs) {
        spotCheckUtils.checkString(getLawCode(content), getLawCode(reference), obs, BILL_LAW_CODE);
    }

    private String formatLawSection(BillView billView) {
        return getActiveAmendOpt(billView)
                .map(BillAmendmentView::getLawSection)
                .map(String::trim)
                .map(section -> section.replaceAll("Â§", "§"))
                .map(section -> section.replaceAll(" +", " "))
                .orElse(null);
    }

    protected void checkBillLawSection(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkString(formatLawSection(content), formatLawSection(reference), obsrv, BILL_LAW_SECTION);
    }

    private String getActionStr(BillActionView actionView) {
        BillAction action = actionView.toBillAction();
        return String.join(" ",
                String.valueOf(action.getSequenceNo()),
                String.valueOf(action.getBillId()),
                String.valueOf(action.getDate()),
                String.valueOf(action.getChamber()),
                String.valueOf(action.getText())
        );
    }

    protected void checkBillActions(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkCollection(
                extractListValue(content.getActions()),
                extractListValue(reference.getActions()),
                obsrv, BILL_ACTION, this::getActionStr, "\n");
    }

    private String getSponsorString(BillView billView) {
        Optional<SponsorView> sponsorOpt = Optional.ofNullable(billView.getSponsor());
        return sponsorOpt.map(SponsorView::getMember).map(MemberView::getShortName).orElse(null) +
                " budget:" + sponsorOpt.map(SponsorView::isBudget).orElse(null) +
                " rules:" + sponsorOpt.map(SponsorView::isRules).orElse(null);
    }

    protected void checkBillSponsor(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkString(getSponsorString(content), getSponsorString(reference), obsrv, BILL_SPONSOR);
    }

    protected void checkAdditionalSponsors(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkCollection(
                extractListValue(content.getAdditionalSponsors()),
                extractListValue(reference.getAdditionalSponsors()),
                obsrv, BILL_ADDITIONAL_SPONSOR, MemberView::getShortName, "\n");
    }

    protected List<MemberView> extractActiveAmendSponsors(BillView bv,
                                                          Function<BillAmendmentView, ListView<MemberView>> sponFunc) {
        return getActiveAmendOpt(bv)
                .map(sponFunc)
                .map(this::extractListValue)
                .orElse(null);
    }

    protected void checkCoSponsors(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkCollection(
                extractActiveAmendSponsors(content, BillAmendmentView::getCoSponsors),
                extractActiveAmendSponsors(reference, BillAmendmentView::getCoSponsors),
                obsrv, BILL_COSPONSOR, MemberView::getShortName, "\n"
        );
    }

    protected void checkMultisponsors(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkCollection(
                extractActiveAmendSponsors(content, BillAmendmentView::getMultiSponsors),
                extractActiveAmendSponsors(reference, BillAmendmentView::getMultiSponsors),
                obsrv, BILL_MULTISPONSOR, MemberView::getShortName, "\n"
        );
    }

    protected void checkBillYear(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkObject(content.getSession(), reference.getSession(), obsrv, BILL_SESSION_YEAR);
    }

    private String getStatusString(BillView billView) {
        Optional<BillStatusView> statusOpt = Optional.ofNullable(billView.getStatus());
        return String.join(" ",
                statusOpt.map(BillStatusView::getActionDate).map(String::valueOf).orElse("null"),
                statusOpt.map(BillStatusView::getStatusType).orElse("null"),
                statusOpt.map(BillStatusView::getStatusDesc).orElse("null"),
                statusOpt.map(BillStatusView::getCommitteeName).orElse("null"),
                statusOpt.map(BillStatusView::getBillCalNo).map(String::valueOf).orElse("null")
        );
    }

    protected void checkBillStatus(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkString(getStatusString(content), getStatusString(reference), obsrv, BILL_LAST_STATUS);
    }

    private String getApprovalMessage(BillView billView) {
        return Optional.ofNullable(billView.getApprovalMessage())
                .map(ApprovalMessageView::getText)
                .map(aprm -> aprm.replaceAll("\\\\n", ""))
                .map(StringUtils::deleteWhitespace)
                .orElse(null);
    }

    protected void checkBillApproveMessage(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkString(getApprovalMessage(content), getApprovalMessage(reference), obsrv, BILL_APPROVAL_MESSAGE);
    }

    private StringBuilder getVoteInfoStr(BillVoteView vote) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(Optional.ofNullable(vote.getBillId()).map(BillIdView::toBillId).orElse(null))
                .append(" ")
                .append(vote.getVersion()).append(" ")
                .append(vote.getVoteDate()).append(" ")
                .append(vote.getVoteType()).append(" ");
        if (vote.getVoteType() == BillVoteType.COMMITTEE) {
            sBuilder.append(Optional.ofNullable(vote.getCommittee())
                    .map(CommitteeIdView::getName).orElse(null));
        }
        return sBuilder;
    }

    private StringBuilder getVoteRollStr(BillVoteView vote) {
        StringBuilder sBuilder = getVoteInfoStr(vote);
        Map<String, ListView<MemberView>> memberVoteMap = Optional.ofNullable(vote.getMemberVotes())
                .map(MapView::getItems)
                .orElse(new HashMap<>());
        for (String voteCode : memberVoteMap.keySet()) {
            sBuilder.append("\n").append(voteCode);
            memberVoteMap.get(voteCode).getItems()
                    .forEach(mv -> sBuilder.append("\n\t").append(mv.getShortName()));
        }
        return sBuilder;
    }

    protected void checkVotes(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        List<BillVoteView> contentVotes = extractListValue(content.getVotes());
        List<BillVoteView> refVotes = extractListValue(reference.getVotes());
        spotCheckUtils.checkCollection(contentVotes, refVotes, obsrv, BILL_VOTE_INFO, this::getVoteInfoStr, "\n");
        // Only check for vote roll if there is no vote info mismatch.
        if (!obsrv.hasMismatch(BILL_VOTE_INFO)) {
            spotCheckUtils.checkCollection(contentVotes, refVotes, obsrv, BILL_VOTE_ROLL, this::getVoteRollStr, "\n\n");
        }
    }

    protected void checkCalendars(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkCollection(
                extractListValue(content.getCalendars()),
                extractListValue(reference.getCalendars()),
                obsrv, BILL_CALENDARS,
                (calIdView) -> Optional.ofNullable(calIdView)
                        .map(CalendarIdView::toCalendarId).map(CalendarId::toString).orElse("null"),
                "\n");
    }

    private String getCommAgendaIdStr(CommAgendaIdView id) {
        Optional<CommAgendaIdView> idOpt = Optional.ofNullable(id);
        String agendaIdStr = idOpt.map(CommAgendaIdView::getAgendaId)
                .map(aiv -> aiv.getYear() + "#" + aiv.getNumber())
                .orElse("null");
        String commIdStr = idOpt.map(CommAgendaIdView::getCommitteeId)
                .map(civ -> civ.getChamber() + "-" + civ.getName())
                .orElse("null");
        return agendaIdStr + " " + commIdStr;
    }

    protected void checkBillCommitteeAgendas(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkCollection(
                extractListValue(content.getCommitteeAgendas()),
                extractListValue(reference.getCommitteeAgendas()),
                obsrv, BILL_COMMITTEE_AGENDAS, this::getCommAgendaIdStr, "\n", true
        );
    }

    private String getCommVerIdStr(CommitteeVersionIdView cvid) {
        return cvid.getChamber() + " " + cvid.getName() + " " + cvid.getSessionYear() + " " + cvid.getReferenceDate();
    }

    protected void checkBillPastCommmittee(BillView content, BillView reference, SpotCheckObservation<BaseBillId> obsrv) {
        spotCheckUtils.checkCollection(
                extractListValue(content.getPastCommittees()),
                extractListValue(reference.getPastCommittees()),
                obsrv, BILL_PAST_COMMITTEES, this::getCommVerIdStr, "\n"
        );
    }

    private Optional<BillAmendmentView> getActiveAmendOpt(BillView billView) {
        Optional<String> activeVersionOpt = Optional.ofNullable(billView.getActiveVersion());
        if (!activeVersionOpt.isPresent()) {
            return Optional.empty();
        }

        return Optional.ofNullable(billView.getAmendments())
                .map(MapView::getItems)
                .map(amendMap -> amendMap.get(activeVersionOpt.get()));
    }

    private <T> List<T> extractListValue(ListView<T> listView) {
        return Optional.ofNullable(listView).map(ListView::getItems).orElse(null);
    }
}