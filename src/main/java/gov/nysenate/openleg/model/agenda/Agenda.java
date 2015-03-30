package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An Agenda is essentially a list of items (bills) that are brought up for discussion in
 * committees. This Agenda class models the agendas closely to how LBDC sends the source data.
 * It is comprised of a collection of addenda which either contains committee meeting information
 * including bills that are to be brought up, or committee votes.
 */
public class Agenda extends BaseLegislativeContent implements Serializable
{
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
        this.setYear(id.getYear());
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
            .filter(ia -> ia.getWeekOf() != null)
            .map(ia -> ia.getWeekOf())
            .findAny();
    }

    public Integer totalBillsConsidered() {
        return totalBillsConsidered(Optional.empty());
    }

    public Integer totalBillsConsidered(Optional<CommitteeId> committeeId) {
        return agendaInfoAddenda.values().stream()
            .flatMap(ia ->
                    (committeeId.isPresent())
                    ? (ia.getCommitteeInfoMap().containsKey(committeeId.get()))
                        ? Stream.of(ia.getCommitteeInfoMap().get(committeeId.get()))
                        : Stream.empty()
                    : ia.getCommitteeInfoMap().values().stream()
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
                (committeeId.isPresent())
                    ? (ia.getCommitteeVoteMap().containsKey(committeeId.get()))
                        ? Stream.of(ia.getCommitteeVoteMap().get(committeeId.get()))
                        : Stream.empty()
                    : ia.getCommitteeVoteMap().values().stream()
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
            .filter(a -> a.contains(committeeId))
            .findAny().isPresent();
    }

    /**
     * Returns a set of committees that have agenda info addenda.
     */
    public Set<CommitteeId> getCommittees() {
        return agendaInfoAddenda.values().stream().flatMap(a -> a.getCommitteeInfoMap().keySet().stream())
            .collect(Collectors.toSet());
    }

    /** --- Basic Getters/Setters --- */

    public AgendaId getId() {
        return id;
    }

    public void setId(AgendaId id) {
        this.id = id;
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
}
