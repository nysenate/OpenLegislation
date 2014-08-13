package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Votes during committee meetings are sent via these agenda vote addenda.
 */
public class AgendaVoteAddendum extends BaseLegislativeContent implements Serializable
{
    private static final long serialVersionUID = -4592278008570984247L;

    /** Reference to the parent agenda. */
    private AgendaId agendaId;

    /** Each addendum has a character designator. */
    private String id;

    /** Committee vote information keyed by the committee id. */
    private Map<CommitteeId, AgendaVoteCommittee> committeeVoteMap;

    /** --- Constructors --- */

    public AgendaVoteAddendum() {
        super();
        this.committeeVoteMap = new HashMap<>();
    }

    public AgendaVoteAddendum(AgendaId agendaId, String addendumId, LocalDateTime pubDate) {
        this();
        this.setAgendaId(agendaId);
        this.setId(addendumId);
        this.setYear(agendaId.getYear());
        this.setSession(SessionYear.of(this.getYear()));
        this.setModifiedDateTime(pubDate);
        this.setPublishedDateTime(pubDate);
    }

    /** --- Functional Getters/Setters --- */

    public void putCommittee(AgendaVoteCommittee committee) {
        this.committeeVoteMap.put(committee.getCommitteeId(), committee);
    }

    public void removeCommittee(CommitteeId committeeId) {
        this.committeeVoteMap.remove(committeeId);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AgendaVoteAddendum other = (AgendaVoteAddendum) obj;
        return Objects.equals(this.agendaId, other.agendaId) &&
               Objects.equals(this.id, other.id) &&
               Objects.equals(this.committeeVoteMap, other.committeeVoteMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agendaId, id, committeeVoteMap);
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

    public Map<CommitteeId, AgendaVoteCommittee> getCommitteeVoteMap () {
        return committeeVoteMap;
    }

    public void setCommitteeVoteMap (Map<CommitteeId, AgendaVoteCommittee> committeeVoteMap) {
        this.committeeVoteMap = committeeVoteMap;
    }
}
