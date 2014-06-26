package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.BaseLegContent;

import java.util.HashMap;

public class AgendaVoteAddendum extends BaseLegContent
{
    private String id;
    private Integer agendaNumber;
    private HashMap<String, AgendaVoteCommittee> committees;

    public AgendaVoteAddendum()
    {
        super();
        this.setCommittees(new HashMap<String, AgendaVoteCommittee>());
    }

    public AgendaVoteAddendum(String id, Integer year, Integer session)
    {
        this();
        this.setId(id);
        this.setYear(year);
        this.setSession(session);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public HashMap<String, AgendaVoteCommittee> getCommittees()
    {
        return committees;
    }

    public void setCommittees(HashMap<String, AgendaVoteCommittee> committees)
    {
        this.committees = committees;
    }

    public AgendaVoteCommittee getCommittee(String name)
    {
        return this.committees.get(name);
    }

    public void putCommittee(AgendaVoteCommittee committee)
    {
        this.committees.put(committee.getName(), committee);
    }

    public void removeCommittee(String name)
    {
        this.committees.remove(name);
    }

    public Integer getAgendaNumber()
    {
        return this.agendaNumber;
    }
    public void setAgendaNumber(Integer id)
    {
        this.agendaNumber = id;
    }
}
