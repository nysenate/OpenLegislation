package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.BaseLegislativeContent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgendaInfoAddendum extends BaseLegislativeContent
{
    private String id;
    private Date weekOf;
    private Integer agendaNumber;
    private Map<String, AgendaInfoCommittee> committees;

    public AgendaInfoAddendum()
    {
        super();
        this.setCommittees(new HashMap<String, AgendaInfoCommittee>());
    }

    public AgendaInfoAddendum(String id, Date weekOf, Date pubDate)
    {
        this();
        this.setId(id);
        this.setWeekOf(weekOf);
        this.setPublishDate(pubDate);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Date getWeekOf()
    {
        return weekOf;
    }

    public void setWeekOf(Date weekOf)
    {
        this.weekOf = weekOf;
    }

    public Map<String, AgendaInfoCommittee> getCommittees()
    {
        return committees;
    }

    public void setCommittees(Map<String, AgendaInfoCommittee> committees)
    {
        this.committees = committees;
    }

    public void putCommittee(AgendaInfoCommittee committee)
    {
        this.committees.put(committee.getName(), committee);
    }

    public AgendaInfoCommittee getCommittee(String name)
    {
        return this.committees.get(name);
    }

    public void removeCommittee(String name)
    {
        this.committees.remove(name);
    }

    public Integer getAgendaNumber()
    {
        return agendaNumber;
    }

    public void setAgendaNumber(Integer agendaNumber)
    {
        this.agendaNumber = agendaNumber;
    }
}
