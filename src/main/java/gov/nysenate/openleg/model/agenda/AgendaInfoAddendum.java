package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.util.DateHelper;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Info for an Agenda is constructed via a series of addenda that are either
 * added, updated, or removed from the parent Agenda container.
 */
public class AgendaInfoAddendum extends BaseLegislativeContent implements Serializable
{
    private static final long serialVersionUID = 8661290465080663674L;

    /** Reference to the parent agenda. */
    private AgendaId agendaId;

    /** Each addendum has a character designator. */
    private String id;

    /** The week this agenda is for. */
    private Date weekOf;

    /** Committee information including bills up for consideration. */
    private Map<CommitteeId, AgendaInfoCommittee> committeeInfoMap;

    /** --- Constructors --- */

    public AgendaInfoAddendum() {
        super();
        this.committeeInfoMap = new HashMap<>();
    }

    public AgendaInfoAddendum(AgendaId agendaId, String addendumId, Date weekOf, Date pubDate) {
        this();
        this.setAgendaId(agendaId);
        this.setId(addendumId);
        this.setWeekOf(weekOf);
        this.setYear(agendaId.getYear());
        this.setSession(DateHelper.resolveSession(this.getYear()));
        this.setModifiedDate(pubDate);
        this.setPublishDate(pubDate);
    }

    /** --- Functional Getters/Setters --- */

    public void putCommittee(AgendaInfoCommittee infoCommittee) {
        this.committeeInfoMap.put(infoCommittee.getCommitteeId(), infoCommittee);
    }

    public AgendaInfoCommittee getCommittee(String name) {
        return this.committeeInfoMap.get(name);
    }

    public void removeCommittee(String name) {
        this.committeeInfoMap.remove(name);
    }

    /** --- Overrides --- */

    /**
     * AgendaInfoAddendums should ignore the modified/published dates during equality testing.
     * This is because previous addendum are resent every time there is a new one to add, and
     * we want to preserve the existing one if nothing else changed.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AgendaInfoAddendum other = (AgendaInfoAddendum) obj;
        return Objects.equals(this.year, other.year) &&
               Objects.equals(this.agendaId, other.agendaId) &&
               Objects.equals(this.id, other.id) &&
               Objects.equals(this.weekOf, other.weekOf) &&
               Objects.equals(this.committeeInfoMap, other.committeeInfoMap);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(year, agendaId, id, weekOf, committeeInfoMap);
    }

    /** --- Basic Getters/Setters --- */

    public AgendaId getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(AgendaId agendaId) {
        this.agendaId = agendaId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getWeekOf() {
        return weekOf;
    }

    public void setWeekOf(Date weekOf) {
        this.weekOf = weekOf;
    }

    public Map<CommitteeId, AgendaInfoCommittee> getCommitteeInfoMap() {
        return committeeInfoMap;
    }

    public void setCommitteeInfoMap(Map<CommitteeId, AgendaInfoCommittee> committeeInfoMap) {
        this.committeeInfoMap = committeeInfoMap;
    }
}
