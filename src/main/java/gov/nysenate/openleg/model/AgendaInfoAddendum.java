package gov.nysenate.openleg.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgendaInfoAddendum extends BaseObject
{
    private String id;
    private Date weekOf;
    private Date pubDate;
    private Integer agendaNumber;
    private Map<String, AgendaInfoCommittee> committees;

    public AgendaInfoAddendum()
    {
        this.setCommittees(new HashMap<String, AgendaInfoCommittee>());
    }

    public AgendaInfoAddendum(String id, Date weekOf, Date pubDate)
    {
        super();
        this.setId(id);
        this.setWeekOf(weekOf);
        this.setPubDate(pubDate);
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

    public Date getPubDate()
    {
        return pubDate;
    }

    public void setPubDate(Date pubDate)
    {
        this.pubDate = pubDate;
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

    @Override
    public String getOid()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOtype()
    {
        return "agendainfo";
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
