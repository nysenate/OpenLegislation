package gov.nysenate.openleg.spotchecks.openleg.agenda;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.MapView;
import gov.nysenate.openleg.api.legislation.agenda.view.*;
import gov.nysenate.openleg.api.legislation.bill.view.BillIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BillVoteView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeIdView;
import gov.nysenate.openleg.api.legislation.member.view.MemberView;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.spotchecks.base.SpotCheckService;
import gov.nysenate.openleg.spotchecks.base.SpotCheckUtils;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReferenceId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType.*;

@Service
public class OpenlegAgendaCheckService implements SpotCheckService<CommitteeAgendaAddendumId, AgendaCommAddendumView, AgendaCommAddendumView> {

    @Autowired private SpotCheckUtils spotCheckUtils;

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(
            AgendaCommAddendumView content, AgendaCommAddendumView reference) {
        final SpotCheckObservation<CommitteeAgendaAddendumId> observation = new SpotCheckObservation<>(
                new SpotCheckReferenceId(SpotCheckRefType.OPENLEG_AGENDA, LocalDateTime.now()),
                reference.getCommitteeAgendaAddendumId());
        checkChair(content, reference, observation);
        checkLocation(content, reference, observation);
        checkMeetingDateTime(content, reference, observation);
        checkNotes(content, reference, observation);
        checkBillListing(content, reference, observation);
        checkHasVotes(content, reference, observation);
            checkAttendanceList(content, reference, observation);
            checkVotesList(content, reference, observation);
        return observation;
    }

    private void checkHasVotes(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                               SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        spotCheckUtils.checkObject(content.hasVotes(), reference.hasVotes(), observation, AGENDA_HAS_VOTES);
    }

    private String extractChair(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.meeting())
                .map(AgendaMeetingView::chair)
                .map(StringUtils::normalizeSpace)
                .orElse(null);
    }

    private void checkChair(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                            SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        String contentChair = extractChair(content);
        String refChair = extractChair(reference);
        spotCheckUtils.checkString(contentChair, refChair, observation, AGENDA_CHAIR);
    }

    private String extractLocation(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.meeting())
                .map(AgendaMeetingView::location)
                .map(StringUtils::normalizeSpace)
                .orElse(null);
    }

    private void checkLocation(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                               SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        String contentLocation = extractLocation(content);
        String refLocation = extractLocation(reference);
        spotCheckUtils.checkString(contentLocation, refLocation, observation, AGENDA_LOCATION);
    }

    private LocalDateTime extractMeetingDateTime(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.meeting())
                .map(AgendaMeetingView::meetingDateTime)
                .orElse(null);
    }

    private void checkMeetingDateTime(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                                      SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        LocalDateTime contentMDT = extractMeetingDateTime(content);
        LocalDateTime refMDT = extractMeetingDateTime(reference);
        spotCheckUtils.checkObject(contentMDT, refMDT, observation, AGENDA_MEETING_TIME);
    }

    private String extractNotes(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.meeting())
                .map(AgendaMeetingView::notes)
                .map(StringUtils::normalizeSpace)
                .orElse(null);
    }

    private void checkNotes(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                            SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        String contentNotes = extractNotes(content);
        String refNotes = extractNotes(reference);
        spotCheckUtils.checkString(contentNotes, refNotes, observation, AGENDA_NOTES);
    }

    private String agendaItemViewStr(AgendaItemView aiv) {
        String billIdStr = getBillIdStr(aiv.billId());
        return billIdStr + " - " + StringUtils.normalizeSpace(aiv.message());
    }

    private final Comparator<AgendaItemView> agendaItemViewComparator =
            Comparator.comparing(a -> Optional.ofNullable(a.billId()).map(BillIdView::toBillId).orElse(null));

    private List<AgendaItemView> extractBillList(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.bills())
                .map(ListView::getItems)
                .orElseGet(ImmutableList::of).stream()
                .sorted(agendaItemViewComparator)
                .toList();
    }

    private void checkBillListing(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                                  SpotCheckObservation<CommitteeAgendaAddendumId> obs) {
        List<AgendaItemView> contentBills = extractBillList(content);
        List<AgendaItemView> refBills = extractBillList(reference);
        spotCheckUtils.checkCollection(contentBills, refBills, obs, AGENDA_BILLS, this::agendaItemViewStr, "\n");
    }

    private List<AgendaAttendanceView> extractAttendList(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.voteInfo())
                .map(AgendaVoteView::attendanceList)
                .map(ListView::getItems)
                .orElse(null);
    }

    private String agendaAttendanceViewStr(AgendaAttendanceView av) {
        String shortName = Optional.ofNullable(av.member()).map(MemberView::getShortName).orElse(null);
        return av.party() + " - " + shortName + " - " + av.party() + " - " + av.attend();
    }

    private void checkAttendanceList(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                                     SpotCheckObservation<CommitteeAgendaAddendumId> obs) {
        List<AgendaAttendanceView> contentAttendList = extractAttendList(content);
        List<AgendaAttendanceView> refAttendList = extractAttendList(reference);
        spotCheckUtils.checkCollection(contentAttendList, refAttendList, obs, AGENDA_ATTENDANCE_LIST,
                this::agendaAttendanceViewStr, "\n");
    }


    private List<AgendaVoteBillView> extractVotes(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.voteInfo())
                .map(AgendaVoteView::votesList)
                .map(ListView::getItems)
                .orElse(null);
    }

    private StringBuilder getVoteStr(AgendaVoteBillView avb) {
        StringBuilder sBuilder = new StringBuilder();
        Optional<BillVoteView> voteOpt = Optional.ofNullable(avb.vote());
        sBuilder.append(voteOpt.map(BillVoteView::getCommittee).map(CommitteeIdView::getName).orElse(null))
                .append(" ")
                .append(voteOpt.map(BillVoteView::getVoteDate).orElse(null)).append(" ")
                .append(getBillIdStr(avb.bill())).append(" ")
                .append(avb.action());
        if (avb.referCommittee() != null) {
            sBuilder.append(" -> ")
                    .append(avb.referCommittee().getName());
        }
        Map<String, ListView<MemberView>> memberVoteMap = voteOpt.map(BillVoteView::getMemberVotes)
                .map(MapView::getItems)
                .orElse(new HashMap<>());
        for (String voteCode : memberVoteMap.keySet()) {
            sBuilder.append("\n").append(voteCode);
            memberVoteMap.get(voteCode).getItems()
                    .forEach(mv -> sBuilder.append("\n\t").append(mv.getShortName()));
        }
        return sBuilder;
    }

    private void checkVotesList(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                                SpotCheckObservation<CommitteeAgendaAddendumId> obs) {
        spotCheckUtils.checkCollection(extractVotes(content), extractVotes(reference), obs, AGENDA_VOTES_LIST,
                this::getVoteStr, "\n\n");
    }

    private String getBillIdStr(BillIdView billIdView) {
        return Optional.ofNullable(billIdView)
                .map(BillIdView::toBillId)
                .map(BillId::toString)
                .orElse(null);
    }
}
