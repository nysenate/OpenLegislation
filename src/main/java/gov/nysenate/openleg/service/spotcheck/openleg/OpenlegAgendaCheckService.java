package gov.nysenate.openleg.service.spotcheck.openleg;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.client.view.agenda.*;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.client.view.bill.BillVoteView;
import gov.nysenate.openleg.client.view.committee.CommitteeIdView;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

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
        spotCheckUtils.checkObject(content.isHasVotes(), reference.isHasVotes(), observation, AGENDA_HAS_VOTES);
    }

    private String extractChair(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.getMeeting())
                .map(AgendaMeetingView::getChair)
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
        return Optional.ofNullable(acav.getMeeting())
                .map(AgendaMeetingView::getLocation)
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
        return Optional.ofNullable(acav.getMeeting())
                .map(AgendaMeetingView::getMeetingDateTime)
                .orElse(null);
    }

    private void checkMeetingDateTime(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                                      SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        LocalDateTime contentMDT = extractMeetingDateTime(content);
        LocalDateTime refMDT = extractMeetingDateTime(reference);
        spotCheckUtils.checkObject(contentMDT, refMDT, observation, AGENDA_MEETING_TIME);
    }

    private String extractNotes(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.getMeeting())
                .map(AgendaMeetingView::getNotes)
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
        String billIdStr = getBillIdStr(aiv.getBillId());
        return billIdStr + " - " + StringUtils.normalizeSpace(aiv.getMessage());
    }

    private Comparator<AgendaItemView> agendaItemViewComparator =
            Comparator.comparing(a -> Optional.ofNullable(a.getBillId()).map(BillIdView::toBillId).orElse(null));

    private List<AgendaItemView> extractBillList(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.getBills())
                .map(ListView::getItems)
                .orElseGet(ImmutableList::of).stream()
                .sorted(agendaItemViewComparator)
                .collect(Collectors.toList());
    }

    private void checkBillListing(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                                  SpotCheckObservation<CommitteeAgendaAddendumId> obs) {
        List<AgendaItemView> contentBills = extractBillList(content);
        List<AgendaItemView> refBills = extractBillList(reference);
        spotCheckUtils.checkCollection(contentBills, refBills, obs, AGENDA_BILLS, this::agendaItemViewStr, "\n");
    }

    private List<AgendaAttendanceView> extractAttendList(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.getVoteInfo())
                .map(AgendaVoteView::getAttendanceList)
                .map(ListView::getItems)
                .orElse(null);
    }

    private String agendaAttendanceViewStr(AgendaAttendanceView av) {
        String shortName = Optional.ofNullable(av.getMember()).map(MemberView::getShortName).orElse(null);
        return av.getRank() + " - " + shortName + " - " + av.getParty() + " - " + av.getAttend();
    }

    private void checkAttendanceList(AgendaCommAddendumView content, AgendaCommAddendumView reference,
                                     SpotCheckObservation<CommitteeAgendaAddendumId> obs) {
        List<AgendaAttendanceView> contentAttendList = extractAttendList(content);
        List<AgendaAttendanceView> refAttendList = extractAttendList(reference);
        spotCheckUtils.checkCollection(contentAttendList, refAttendList, obs, AGENDA_ATTENDANCE_LIST,
                this::agendaAttendanceViewStr, "\n");
    }


    private List<AgendaVoteBillView> extractVotes(AgendaCommAddendumView acav) {
        return Optional.ofNullable(acav.getVoteInfo())
                .map(AgendaVoteView::getVotesList)
                .map(ListView::getItems)
                .orElse(null);
    }

    private StringBuilder getVoteStr(AgendaVoteBillView avb) {
        StringBuilder sBuilder = new StringBuilder();
        Optional<BillVoteView> voteOpt = Optional.ofNullable(avb.getVote());
        sBuilder.append(voteOpt.map(BillVoteView::getCommittee).map(CommitteeIdView::getName).orElse(null))
                .append(" ")
                .append(voteOpt.map(BillVoteView::getVoteDate).orElse(null)).append(" ")
                .append(getBillIdStr(avb.getBill())).append(" ")
                .append(avb.getAction());
        if (avb.getReferCommittee() != null) {
            sBuilder.append(" -> ")
                    .append(avb.getReferCommittee().getName());
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
