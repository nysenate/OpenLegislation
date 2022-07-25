package gov.nysenate.openleg.legislation.agenda;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.legislation.BaseLegislativeContent;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.CommitteeId;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An Agenda is essentially a list of items (bills) that are brought up for discussion in
 * committees. This Agenda class models the agendas closely to how LBDC sends the source data.
 * It comprises a collection of addenda which either contains committee meeting information
 * including bills that are to be brought up, or committee votes.
 */
public class Agenda extends BaseLegislativeContent implements Serializable {
    @Serial
    private static final long serialVersionUID = -6763891242038699549L;

    /** The agenda id. */
    private AgendaId id;

    /** The map of committee info updates keyed by the addendum id. */
    private Map<String, AgendaInfoAddendum> agendaInfoAddenda;

    /** The map of committee vote updates keyed by the addendum id. */
    private Map<String, AgendaVoteAddendum> agendaVoteAddenda;

    /** --- Constructors --- */

    public Agenda() {
        super();
        this.setAgendaInfoAddenda(new TreeMap<>());
        this.setAgendaVoteAddenda(new TreeMap<>());
    }

    public Agenda(AgendaId id) {
        this();
        this.setId(id);
        this.setSession(SessionYear.of(this.getYear()));
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "Agenda " + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Agenda other = (Agenda) obj;
        return Objects.equals(this.year, other.year) &&
               Objects.equals(this.id, other.id) &&
               Objects.equals(this.agendaInfoAddenda, other.agendaInfoAddenda) &&
               Objects.equals(this.agendaVoteAddenda, other.agendaVoteAddenda) &&
               Objects.equals(this.publishedDateTime, other.publishedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, id, agendaInfoAddenda, agendaVoteAddenda, publishedDateTime);
    }

    /** --- Functional Getters/Setters --- */

    public AgendaInfoAddendum getAgendaInfoAddendum(String addendumId) {
        return this.agendaInfoAddenda.get(addendumId);
    }

    public void putAgendaInfoAddendum(AgendaInfoAddendum addendum) {
        this.agendaInfoAddenda.put(addendum.getId(), addendum);
    }

    public AgendaVoteAddendum getAgendaVoteAddendum(String addendumId) {
        return this.agendaVoteAddenda.get(addendumId);
    }

    public void putAgendaVoteAddendum(AgendaVoteAddendum addendum) {
        this.agendaVoteAddenda.put(addendum.getId(), addendum);
    }

    /**
     * Finds any 'weekOf' set within the info addenda and returns it, If the data is not complete, it could be possible
     * that the agenda doesn't have a weekOf set in which case an empty Optional is returned.
     * @return Optional<LocalDate>.
     */
    public Optional<LocalDate> getWeekOf() {
        return agendaInfoAddenda.values().stream()
            .map(AgendaInfoAddendum::getWeekOf)
            .filter(Objects::nonNull)
            .findAny();
    }

    public Integer totalBillsConsidered() {
        return totalBillsConsidered(Optional.empty());
    }

    public Integer totalBillsConsidered(Optional<CommitteeId> committeeId) {
        return agendaInfoAddenda.values().stream()
            .flatMap(ia ->
                    committeeId.<Stream<AgendaInfoCommittee>>map(value -> (ia.getCommitteeInfoMap().containsKey(value))
                            ? Stream.of(ia.getCommitteeInfoMap().get(value))
                            : Stream.empty()).orElseGet(() -> ia.getCommitteeInfoMap().values().stream())
            )
            .map(ic -> ic.getItems().size())
            .reduce(0, Integer::sum);
    }

    public Integer totalBillsVoted() {
        return totalBillsVoted(Optional.empty());
    }

    public Integer totalBillsVoted(Optional<CommitteeId> committeeId) {
        return agendaVoteAddenda.values().stream()
            .flatMap(ia ->
                    committeeId.<Stream<AgendaVoteCommittee>>map(value -> (ia.getCommitteeVoteMap().containsKey(value))
                            ? Stream.of(ia.getCommitteeVoteMap().get(value))
                            : Stream.empty()).orElseGet(() -> ia.getCommitteeVoteMap().values().stream())
            )
            .map(ic -> ic.getVotedBills().size())
            .reduce(0, Integer::sum);
    }

    public Long totalCommittees() {
        return agendaInfoAddenda.values().stream()
            .flatMap(ia -> ia.getCommitteeInfoMap().keySet().stream())
            .distinct()
            .count();
    }

    /**
     * Returns true if there is data contained in this agenda for the given committee.
     */
    public boolean hasCommittee(CommitteeId committeeId) {
        return agendaInfoAddenda.values().stream()
            .map(a -> a.getCommitteeInfoMap().keySet())
            .anyMatch(a -> a.contains(committeeId));
    }

    /**
     * Returns a set of committees that have agenda info addenda.
     */
    public Set<CommitteeId> getCommittees() {
        return agendaInfoAddenda.values().stream().flatMap(a -> a.getCommitteeInfoMap().keySet().stream())
            .collect(Collectors.toSet());
    }

    /**
     * @return A set of all addenda in this Agenda for either votes or info
     */
    public ImmutableSet<String> getAddenda() {
        return ImmutableSet.copyOf(Sets.union(
                agendaInfoAddenda.keySet(),
                agendaVoteAddenda.keySet()
        ));
    }

    public void setId(AgendaId id) {
        this.id = id;
        this.setYear(id.getYear());
    }

    /** --- Basic Getters/Setters --- */

    public AgendaId getId() {
        return id;
    }

    public Map<String, AgendaInfoAddendum> getAgendaInfoAddenda() {
        return agendaInfoAddenda;
    }

    public void setAgendaInfoAddenda(Map<String, AgendaInfoAddendum> agendaInfoAddenda) {
        this.agendaInfoAddenda = agendaInfoAddenda;
    }

    public Map<String, AgendaVoteAddendum> getAgendaVoteAddenda() {
        return agendaVoteAddenda;
    }

    public void setAgendaVoteAddenda(Map<String, AgendaVoteAddendum> agendaVoteAddenda) {
        this.agendaVoteAddenda = agendaVoteAddenda;
    }

    /** --- Functional Getters --- */

    public List<CommitteeAgendaAddendumId> getCommitteeAgendaAddendumIds(){
        return agendaInfoAddenda.values().stream()
                .flatMap(agendaInfoAddendum -> agendaInfoAddendum.getCommitteeInfoMap().values().stream())
                .map(agendaInfoCommittee -> new CommitteeAgendaAddendumId(agendaInfoCommittee.getAgendaId(),
                        agendaInfoCommittee.getCommitteeId(),
                        agendaInfoCommittee.getAddendum())).toList();
    }

    /**
     * Gets a flat mapping of {@link CommitteeAgendaAddendumId} to {@link AgendaVoteCommittee}
     * which contains all votes in this agenda
     *
     * @return {@link Map<CommitteeAgendaAddendumId, AgendaVoteCommittee>}
     */
    public Map<CommitteeAgendaAddendumId, AgendaVoteCommittee> getVotes() {
        Map<CommitteeAgendaAddendumId, AgendaVoteCommittee> votes = new HashMap<>();
        for (AgendaVoteAddendum addendum : agendaVoteAddenda.values()) {
            for (AgendaVoteCommittee voteComm : addendum.getCommitteeVoteMap().values()) {
                CommitteeAgendaAddendumId voteId = new CommitteeAgendaAddendumId(
                        id, voteComm.getCommitteeId(), Version.of(addendum.getId()));
                votes.put(voteId, voteComm);
            }
        }
        return votes;
    }

    /**
     * Given a {@code committeeId}, returns all {@link AgendaVoteCommittee}'s for
     * that committee in this agenda.
     * @param committeeId
     * @return
     */
    public List<AgendaVoteCommittee> getVotesForCommittee(CommitteeId committeeId) {
        return this.getAgendaVoteAddenda().values().stream()
                .map(a -> a.getCommitteeVoteMap().get(committeeId))
                .filter(Objects::nonNull)
                .toList();
    }
}
