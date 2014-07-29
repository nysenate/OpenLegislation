package gov.nysenate.openleg.model.agenda;

import com.google.common.base.Objects;
import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.util.DateHelper;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * An Agenda is essentially a list of items (bills) that are brought up for discussion in
 * committees. This Agenda class models the agendas closely to how LBDC formats its source data.
 * It is comprised of a collection of addenda which either contains committee meeting information
 * including bills that are to be brought up, or committee votes.
 */
public class Agenda extends BaseLegislativeContent implements Serializable
{
    private static final long serialVersionUID = -6763891242038699549L;

    /** The agenda id. */
    private AgendaId id;

    /** The map of committee info updates keyed by the addendum id. */
    private Map<String, AgendaInfoAddendum> agendaInfoAddendum;

    /** The map of committee vote updates keyeed by the addendum id. */
    private Map<String, AgendaVoteAddendum> agendaVoteAddendum;

    /** --- Constructors --- */

    public Agenda() {
        super();
        this.setAgendaInfoAddendum(new TreeMap<String, AgendaInfoAddendum>());
        this.setAgendaVoteAddendum(new TreeMap<String, AgendaVoteAddendum>());
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
        if (agendaInfoAddendum != null ? !agendaInfoAddendum.equals(agenda.agendaInfoAddendum) : agenda.agendaInfoAddendum != null)
            return false;
        if (agendaVoteAddendum != null ? !agendaVoteAddendum.equals(agenda.agendaVoteAddendum) : agenda.agendaVoteAddendum != null)
            return false;
        if (id != null ? !id.equals(agenda.id) : agenda.id != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (agendaInfoAddendum != null ? agendaInfoAddendum.hashCode() : 0);
        result = 31 * result + (agendaVoteAddendum != null ? agendaVoteAddendum.hashCode() : 0);
        return result;
    }

    /** --- Functional Getters/Setters --- */

    public AgendaInfoAddendum getAgendaInfoAddendum(String addendumId) {
        return this.agendaInfoAddendum.get(addendumId);
    }

    public void putAgendaInfoAddendum(AgendaInfoAddendum addendum) {
        this.agendaInfoAddendum.put(addendum.getId(), addendum);
    }

    public AgendaVoteAddendum getAgendaVoteAddendum(String addendumId) {
        return this.agendaVoteAddendum.get(addendumId);
    }

    public void putAgendaVoteAddendum(AgendaVoteAddendum addendum) {
        this.agendaVoteAddendum.put(addendum.getId(), addendum);
    }

    /** --- Basic Getters/Setters --- */

    public AgendaId getId() {
        return id;
    }

    public void setId(AgendaId id) {
        this.id = id;
    }

    public Map<String, AgendaInfoAddendum> getAgendaInfoAddendum() {
        return agendaInfoAddendum;
    }

    public void setAgendaInfoAddendum(Map<String, AgendaInfoAddendum> agendaInfoAddendum) {
        this.agendaInfoAddendum = agendaInfoAddendum;
    }

    public Map<String, AgendaVoteAddendum> getAgendaVoteAddendum() {
        return agendaVoteAddendum;
    }

    public void setAgendaVoteAddendum(Map<String, AgendaVoteAddendum> agendaVoteAddendum) {
        this.agendaVoteAddendum = agendaVoteAddendum;
    }
}
