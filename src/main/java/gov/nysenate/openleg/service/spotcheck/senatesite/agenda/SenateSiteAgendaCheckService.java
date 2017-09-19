package gov.nysenate.openleg.service.spotcheck.senatesite.agenda;

import gov.nysenate.openleg.model.agenda.AgendaVoteCommittee;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
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
import java.util.stream.Collectors;

/**
 * Created by PKS on 4/28/16.
 */
@Service
public class SenateSiteAgendaCheckService
        extends BaseSpotCheckService<CommitteeAgendaAddendumId, AgendaVoteCommittee, SenateSiteAgenda> {

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(AgendaVoteCommittee content)
            throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(AgendaVoteCommittee content,
                                                                 LocalDateTime start, LocalDateTime end)
            throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(AgendaVoteCommittee content, SenateSiteAgenda reference) {
        SpotCheckObservation<CommitteeAgendaAddendumId> observation =
                new SpotCheckObservation<>(reference.getReferenceId(),
                        reference.getcommitteeAgendaAddendumId());
        checkVotes(content, reference, observation);
        return observation;
    }

    /* --- Internal Methods --- */

    private void checkVotes(AgendaVoteCommittee content, SenateSiteAgenda reference,
                            SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        StringBuffer contentVotes = getFullVoteString(content);
        StringBuffer refVotes = getFullVoteString(reference);

        checkObject(contentVotes, refVotes, observation, SpotCheckMismatchType.AGENDA_VOTES);
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

}

