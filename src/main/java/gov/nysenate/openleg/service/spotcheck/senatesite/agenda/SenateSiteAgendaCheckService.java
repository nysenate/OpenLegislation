package gov.nysenate.openleg.service.spotcheck.senatesite.agenda;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillVote;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.senatesite.agenda.SenateSiteAgenda;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

/**
 * Created by PKS on 4/28/16.
 */
@Service
public class SenateSiteAgendaCheckService
        implements SpotCheckService<CommitteeAgendaAddendumId, Agenda, SenateSiteAgenda> {

    @Autowired private SpotCheckUtils spotCheckUtils;

    @Override
    public SpotCheckObservation<CommitteeAgendaAddendumId> check(Agenda content, SenateSiteAgenda reference) {
        Optional<AgendaInfoCommittee> openlegInfoCommOpt = getOLInfoCommittee(content, reference);

        // Return an observe data missing observation if Openleg does not have a info addendum matching the SenateSite addendum
        if (!openlegInfoCommOpt.isPresent()) {
            return SpotCheckObservation.getObserveDataMissingObs(
                    reference.getReferenceId(), reference.getcommitteeAgendaAddendumId());
        }
        AgendaInfoCommittee openlegInfoComm = openlegInfoCommOpt.orElse(new AgendaInfoCommittee());

        SpotCheckObservation<CommitteeAgendaAddendumId> observation =
                new SpotCheckObservation<>(reference.getReferenceId(),
                        reference.getcommitteeAgendaAddendumId());

        checkLocation(openlegInfoComm, reference, observation);
        checkNotes(openlegInfoComm, reference, observation);
        checkMeetingTime(openlegInfoComm, reference, observation);
        checkBills(content, openlegInfoComm, reference, observation);
        return observation;
    }

    /* --- Internal Methods --- */

    private void checkLocation(AgendaInfoCommittee content, SenateSiteAgenda reference,
                               SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        spotCheckUtils.checkString(content.getLocation(), reference.getLocation(), observation, AGENDA_LOCATION);
    }

    private void checkNotes(AgendaInfoCommittee content, SenateSiteAgenda reference,
                               SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        spotCheckUtils.checkString(content.getNotes(), reference.getNotes(), observation, AGENDA_NOTES);
    }

    private void checkMeetingTime(AgendaInfoCommittee content, SenateSiteAgenda reference,
                            SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        spotCheckUtils.checkObject(content.getMeetingDateTime(), reference.getMeetingDateTime(), observation, AGENDA_LOCATION);
    }

    /**
     * Compares bill votes for bills in the {@code reference} to the bill votes for bills in the {@code openlegInfoAddendum}
     * Fetches openleg bill votes from the {@code openlegAgenda} since the info addenda do not contain votes.
     *
     * We cannot simply look at the openleg vote addendum because bills in a openleg vote addendum do not
     * necessarily match the bills in the info addendum, while in the SenateSite, those bills always match.
     * i.e.:    Openleg Info Addendum A - contains bills: S100 & S200.
     *          Openleg Vote Addendum A - contains bill votes for: S100 & S999 (S999 would of been in a different info addendum.)
     *          SenateSite Addendum A - contains bill votes for S100 & S200.
     *          Cant compare Openleg Vote A to SenateSite A without false positives.
     * @param openlegAgenda
     * @param openlegInfoAddendum
     * @param reference
     * @param observation
     */
    private void checkBills(Agenda openlegAgenda, AgendaInfoCommittee openlegInfoAddendum, SenateSiteAgenda reference,
                            SpotCheckObservation<CommitteeAgendaAddendumId> observation) {
        Table<BillId, BillVoteCode, Integer> olVoteTable = getOpenlegVoteTable(openlegAgenda, openlegInfoAddendum);
        Table<BillId, BillVoteCode, Integer> referenceVoteTable = getSenateSiteVoteTable(reference);

        spotCheckUtils.checkObject(getVoteTableString(olVoteTable), getVoteTableString(referenceVoteTable), observation, AGENDA_BILLS);
    }

    private static final ImmutableMap<BillVoteCode, Integer> emptyVoteCountMap = ImmutableMap.copyOf(
            Maps.toMap(Arrays.asList(BillVoteCode.values()), (bvc) -> 0)
    );

    private Table<BillId, BillVoteCode, Integer> getOpenlegVoteTable(Agenda agenda, AgendaInfoCommittee infoComm) {
        Table<BillId, BillVoteCode, Integer> voteCountTable = HashBasedTable.create();

        for (AgendaInfoCommitteeItem item : infoComm.getItems()) {
            BillId billId = item.getBillId();
            // Search through all agenda vote addenda for this bill's votes.
            for (AgendaVoteCommittee voteCommittee : agenda.getVotesForCommittee(infoComm.getCommitteeId())) {
                AgendaVoteBill agendaVoteBill = voteCommittee.getVotedBills().get(billId);
                // If billId has votes in this vote addendum
                if (agendaVoteBill != null) {
                    // Add votes to table.
                    BillVote billVote = agendaVoteBill.getBillVote();
                    voteCountTable.row(billId).putAll(billVote.getVoteCounts());
                }
            }

            // If no votes were found
            if (voteCountTable.row(billId).size() == 0) {
                // Add empty vote row for this bill.
                voteCountTable.row(billId).putAll(emptyVoteCountMap);
            }
        }

        return voteCountTable;
    }

    private Table<BillId, BillVoteCode, Integer> getSenateSiteVoteTable(SenateSiteAgenda senateSiteAgenda) {
        Table<BillId, BillVoteCode, Integer> voteCountTable = HashBasedTable.create();
        senateSiteAgenda.getAgendaBills().forEach(bill ->
                voteCountTable.row(bill.getBillId()).putAll(bill.getVoteCounts()));
        return voteCountTable;
    }

    private StringBuffer getVoteTableString(Table<BillId, BillVoteCode, Integer> billVoteTable) {
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

