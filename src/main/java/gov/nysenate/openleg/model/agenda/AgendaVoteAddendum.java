package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.util.DateHelper;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 *
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
        this.setId(id);
        this.setYear(agendaId.getYear());
        this.setSession(DateHelper.resolveSession(this.getYear()));
        this.setModifiedDate(pubDate);
        this.setPublishDate(pubDate);
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
