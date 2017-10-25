package gov.nysenate.openleg.service.spotcheck.senatesite.agenda;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillVote;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.senatesite.agenda.SenateSiteAgenda;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

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
        checkBills(openlegVoteComm, openlegInfoComm, reference, observation);
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

    private void checkBills(AgendaVoteCommittee contentVotes, AgendaInfoCommittee contentInfo, SenateSiteAgenda reference,
                            SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        StringBuffer contentVoteString = getFullVoteString(contentVotes, contentInfo);
        StringBuffer refVoteString = getFullVoteString(reference);

        checkObject(contentVoteString, refVoteString, observation, AGENDA_BILLS);
    }

    private static final ImmutableMap<BillVoteCode, Integer> emptyVoteCountMap = ImmutableMap.copyOf(
            Maps.toMap(Arrays.asList(BillVoteCode.values()), (bvc) -> 0)
    );

    private StringBuffer getFullVoteString(AgendaVoteCommittee voteComm, AgendaInfoCommittee infoComm) {
        Map<BillId, AgendaVoteBill> votedBills = voteComm.getVotedBills();

        Set<BillId> billIds = new HashSet<>();

        billIds.addAll(votedBills.keySet());
        infoComm.getItems().stream()
                .map(AgendaInfoCommitteeItem::getBillId)
                .forEach(billIds::add);

        Table<BillId, BillVoteCode, Integer> voteCountTable = HashBasedTable.create();

        for (BillId billId : billIds) {
            // Some bills do not have votes, use an empty vote count map for these
            Map<BillVoteCode, Integer> voteCountMap =
                    Optional.ofNullable(votedBills.get(billId))
                            .map(AgendaVoteBill::getBillVote)
                            .map(BillVote::getVoteCounts)
                            .orElse(emptyVoteCountMap);

            voteCountTable.row(billId).putAll(voteCountMap);
        }

        return getFullVoteString(voteCountTable);
    }

    private StringBuffer getFullVoteString(SenateSiteAgenda senateSiteAgenda) {
        Table<BillId, BillVoteCode, Integer> voteCountTable = HashBasedTable.create();
        senateSiteAgenda.getAgendaBills().forEach(bill ->
                voteCountTable.row(bill.getBillId()).putAll(bill.getVoteCounts()));

        return getFullVoteString(voteCountTable);
    }

    private StringBuffer getFullVoteString(Table<BillId, BillVoteCode, Integer> billVoteTable) {
        return billVoteTable.rowKeySet().stream()
                .sorted()
                .map(billId -> getVoteLine(billId, billVoteTable.row(billId)))
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

