package gov.nysenate.openleg.service.spotcheck.senatesite.agenda;

import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.model.agenda.AgendaVoteCommittee;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.senatesite.agenda.SenateSiteAgenda;
import gov.nysenate.openleg.model.spotcheck.senatesite.agenda.SenateSiteAgendaBill;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.AGENDA_LOCATION;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.AGENDA_NOTES;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.AGENDA_VOTES;

/**
 * Created by PKS on 4/28/16.
 */
@Service
public class SenateSiteAgendaCheckService
        extends BaseSpotCheckService<CommitteeAgendaAddendumId, Agenda, SenateSiteAgenda> {

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(Agenda content)
            throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(Agenda content, LocalDateTime start, LocalDateTime end)
            throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(Agenda content, SenateSiteAgenda reference) {
        Optional<AgendaInfoCommittee> openlegInfoCommOpt = getOLInfoCommittee(content, reference);
        Optional<AgendaVoteCommittee> openlegVoteCommOpt = getOLVoteCommittee(content, reference);

        // Return an observe data missing observation if there is no info or votes for the given reference
        if (!(openlegInfoCommOpt.isPresent() || openlegVoteCommOpt.isPresent())) {
            return SpotCheckObservation.getObserveDataMissingObs(
                    reference.getReferenceId(), reference.getcommitteeAgendaAddendumId());
        }

        SpotCheckObservation<CommitteeAgendaAddendumId> observation =
                new SpotCheckObservation<>(reference.getReferenceId(),
                        reference.getcommitteeAgendaAddendumId());

        // Use blank values if either votes or info is not present in openleg
        AgendaInfoCommittee openlegInfoComm = openlegInfoCommOpt.orElse(new AgendaInfoCommittee());
        AgendaVoteCommittee openlegVoteComm = openlegVoteCommOpt.orElse(new AgendaVoteCommittee());

        checkLocation(openlegInfoComm, reference, observation);
        checkNotes(openlegInfoComm, reference, observation);
        checkMeetingTime(openlegInfoComm, reference, observation);
        checkVotes(openlegVoteComm, reference, observation);
        return observation;
    }

    /* --- Internal Methods --- */

    private void checkLocation(AgendaInfoCommittee content, SenateSiteAgenda reference,
                               SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        checkString(content.getLocation(), reference.getLocation(), observation, AGENDA_LOCATION);
    }

    private void checkNotes(AgendaInfoCommittee content, SenateSiteAgenda reference,
                               SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        checkString(content.getNotes(), reference.getNotes(), observation, AGENDA_NOTES);
    }

    private void checkMeetingTime(AgendaInfoCommittee content, SenateSiteAgenda reference,
                            SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        checkObject(content.getMeetingDateTime(), reference.getMeetingDateTime(), observation, AGENDA_LOCATION);
    }

    private void checkVotes(AgendaVoteCommittee content, SenateSiteAgenda reference,
                            SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        StringBuffer contentVotes = getFullVoteString(content);
        StringBuffer refVotes = getFullVoteString(reference);

        checkObject(contentVotes, refVotes, observation, AGENDA_VOTES);
    }

    private StringBuffer getFullVoteString(AgendaVoteCommittee agendaVoteCommittee) {
        Map<BillId, Map<BillVoteCode, Integer>> voteCountMap =
                agendaVoteCommittee.getVotedBills().entrySet().stream()
                        .map(entry -> Pair.of(entry.getKey(),
                                entry.getValue().getBillVote().getVoteCounts()))
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        return getFullVoteString(voteCountMap);
    }

    private StringBuffer getFullVoteString(SenateSiteAgenda senateSiteAgenda) {
        Map<BillId, Map<BillVoteCode, Integer>> voteCountMap =
                senateSiteAgenda.getAgendaBills().stream()
                        .collect(Collectors.toMap(
                                SenateSiteAgendaBill::getBillId, SenateSiteAgendaBill::getVoteCounts));

        return getFullVoteString(voteCountMap);
    }

    private StringBuffer getFullVoteString(Map<BillId, Map<BillVoteCode, Integer>> billVotes) {
        return billVotes.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> getVoteLine(entry.getKey(), entry.getValue()))
                .reduce((sb1, sb2) -> sb1.append("\n").append(sb2))
                .orElseGet(StringBuffer::new);
    }

    private StringBuffer getVoteLine(BillId billId, Map<BillVoteCode, Integer> voteCounts) {
        StringBuffer voteString = new StringBuffer();
        voteString.append(billId);
        for (BillVoteCode voteCode : BillVoteCode.values()) {
            voteString.append("\t")
                    .append(voteCode)
                    .append(": ")
                    .append(voteCounts.getOrDefault(voteCode, 0));
        }
        return voteString;
    }

    private Optional<AgendaVoteCommittee> getOLVoteCommittee(Agenda content, SenateSiteAgenda reference) {
        return Optional.ofNullable(content.getAgendaVoteAddendum(reference.getAddendum()))
                        .map(addendum -> addendum.getCommittee(reference.getCommitteeId()));
    }

    private Optional<AgendaInfoCommittee> getOLInfoCommittee(Agenda content, SenateSiteAgenda reference) {
        return Optional.ofNullable(content.getAgendaInfoAddendum(reference.getAddendum()))
                .map(addendum -> addendum.getCommittee(reference.getCommitteeId()));
    }

}

