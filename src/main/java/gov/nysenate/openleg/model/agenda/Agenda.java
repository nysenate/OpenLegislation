package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.util.DateHelper;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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
        this.setSession(DateHelper.resolveSession(this.getYear()));
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "Agenda " + id + " (" + year + ")";
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
               Objects.equals(this.publishDate, other.publishDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, id, agendaInfoAddenda, agendaVoteAddenda, publishDate);
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
