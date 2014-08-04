package gov.nysenate.openleg.model.agenda;

import com.google.common.base.Objects;
import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.util.DateHelper;

import java.io.Serializable;
import java.util.Map;
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
        return Objects.toStringHelper(this)
                .add("id", id)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agenda)) return false;
        if (!super.equals(o)) return false;
        Agenda agenda = (Agenda) o;
        if (agendaInfoAddenda != null ? !agendaInfoAddenda.equals(agenda.agendaInfoAddenda) : agenda.agendaInfoAddenda != null)
            return false;
        if (agendaVoteAddenda != null ? !agendaVoteAddenda.equals(agenda.agendaVoteAddenda) : agenda.agendaVoteAddenda != null)
            return false;
        if (id != null ? !id.equals(agenda.id) : agenda.id != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (agendaInfoAddenda != null ? agendaInfoAddenda.hashCode() : 0);
        result = 31 * result + (agendaVoteAddenda != null ? agendaVoteAddenda.hashCode() : 0);
        return result;
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
