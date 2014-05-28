package gov.nysenate.openleg.model.agenda;


import gov.nysenate.openleg.model.BaseObject;

import java.util.Map;
import java.util.TreeMap;


public class Agenda extends BaseObject
{
    /**
     * The agenda's calendar number. Starts at 1 at the beginning of each calendar year.
     */
    private Integer number;

    /**
     * The list of addendum to the agenda.
     */
    private Map<String, AgendaInfoAddendum> agendaInfoAddendum;

    /**
     * The list of vote addendum to the agenda.
     */
    private Map<String, AgendaVoteAddendum> agendaVoteAddendum;

    public Agenda()
    {
        super();
        this.setAgendaInfoAddendum(new TreeMap<String, AgendaInfoAddendum>());
        this.setAgendaVoteAddendum(new TreeMap<String, AgendaVoteAddendum>());
    }

    public Agenda(Integer number, Integer session, Integer year)
    {
        this();
        this.setNumber(number);
        this.setSession(session);
        this.setYear(year);
    }

    public Integer getNumber()
    {
        return number;
    }

    public void setNumber(Integer number)
    {
        this.number = number;
    }

    public String getOid()
    {
        return this.getOtype()+"-"+this.getYear()+"-"+this.getNumber();
    }

    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Agenda) {
            return ((Agenda)obj).getOid().equals(this.getOid());
        }
        return false;
    }

    public String getOtype()
    {
        return "agenda";
    }

    public Map<String, AgendaVoteAddendum> getAgendaVoteAddendum()
    {
        return agendaVoteAddendum;
    }

    public void setAgendaVoteAddendum(Map<String, AgendaVoteAddendum> agendaVoteAddendum)
    {
        this.agendaVoteAddendum = agendaVoteAddendum;
    }

    public AgendaVoteAddendum getAgendaVoteAddendum(String id)
    {
        return this.agendaVoteAddendum.get(id);
    }

    public void putAgendaVoteAddendum(AgendaVoteAddendum addendum)
    {
        this.agendaVoteAddendum.put(addendum.getId(), addendum);
    }

    public void removeAgendaVoteAddendum(String id)
    {
        this.agendaVoteAddendum.remove(id);
    }

    public Map<String, AgendaInfoAddendum> getAgendaInfoAddendum()
    {
        return agendaInfoAddendum;
    }

    public void setAgendaInfoAddendum(Map<String, AgendaInfoAddendum> agendaAddendum)
    {
        this.agendaInfoAddendum = agendaAddendum;
    }

    public AgendaInfoAddendum getAgendaInfoAddendum(String id)
    {
        return this.agendaInfoAddendum.get(id);
    }

    public void putAgendaInfoAddendum(AgendaInfoAddendum addendum)
    {
        this.agendaInfoAddendum.put(addendum.getId(), addendum);
    }

    public void removeAgendaInfoAddendum(String id)
    {
        this.agendaInfoAddendum.remove(id);
    }
}
