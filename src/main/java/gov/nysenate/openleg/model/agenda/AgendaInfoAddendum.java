package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalDate weekOf;

    /** Committee information including bills up for consideration. */
    private Map<CommitteeId, AgendaInfoCommittee> committeeInfoMap;

    /** --- Constructors --- */

    public AgendaInfoAddendum() {
        super();
        this.committeeInfoMap = new HashMap<>();
    }

    public AgendaInfoAddendum(AgendaId agendaId, String addendumId, LocalDate weekOf, LocalDateTime pubDate) {
        this();
        this.setAgendaId(agendaId);
        this.setId(addendumId);
        this.setWeekOf(weekOf);
        this.setYear(agendaId.getYear());
        this.setSession(SessionYear.of(this.getYear()));
        this.setModifiedDateTime(pubDate);
        this.setPublishedDateTime(pubDate);
    }

    /** --- Functional Getters/Setters --- */

    public void putCommittee(AgendaInfoCommittee infoCommittee) {
        this.committeeInfoMap.put(infoCommittee.getCommitteeId(), infoCommittee);
    }

    public AgendaInfoCommittee getCommittee(CommitteeId committeeId) {
        return this.committeeInfoMap.get(committeeId);
    }

    public void removeCommittee(CommitteeId committeeId) {
        this.committeeInfoMap.remove(committeeId);
    }

    /** --- Overrides --- */

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
        return Objects.hash(year, agendaId, id, weekOf, committeeInfoMap);
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

    public LocalDate getWeekOf() {
        return weekOf;
    }

    public void setWeekOf(LocalDate weekOf) {
        this.weekOf = weekOf;
    }

    public Map<CommitteeId, AgendaInfoCommittee> getCommitteeInfoMap() {
        return committeeInfoMap;
    }

    public void setCommitteeInfoMap(Map<CommitteeId, AgendaInfoCommittee> committeeInfoMap) {
        this.committeeInfoMap = committeeInfoMap;
    }
}
