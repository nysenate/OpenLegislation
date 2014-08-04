package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.util.DateHelper;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
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
    private HashMap<CommitteeId, AgendaVoteCommittee> committees;

    /** --- Constructors --- */

    public AgendaVoteAddendum() {
        super();
        this.committees = new HashMap<>();
    }

    public AgendaVoteAddendum(AgendaId agendaId, String addendumId, Date pubDate) {
        this();
        this.setAgendaId(agendaId);
        this.setId(addendumId);
        this.setYear(agendaId.getYear());
        this.setSession(DateHelper.resolveSession(this.getYear()));
        this.setModifiedDate(pubDate);
        this.setPublishDate(pubDate);
    }

    /** --- Functional Getters/Setters --- */

    public void putCommittee(AgendaVoteCommittee committee) {
        this.committees.put(committee.getCommitteeId(), committee);
    }

    public void removeCommittee(CommitteeId committeeId) {
        this.committees.remove(committeeId);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AgendaVoteAddendum other = (AgendaVoteAddendum) obj;
        return Objects.equals(this.agendaId, other.agendaId) &&
               Objects.equals(this.id, other.id) &&
               Objects.equals(this.committees, other.committees);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agendaId, id, committees);
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

    public HashMap<CommitteeId, AgendaVoteCommittee> getCommittees() {
        return committees;
    }

    public void setCommittees(HashMap<CommitteeId, AgendaVoteCommittee> committees) {
        this.committees = committees;
    }
}
