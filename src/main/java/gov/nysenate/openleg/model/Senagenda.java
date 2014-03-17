package gov.nysenate.openleg.model;


import java.util.Map;


public class Senagenda extends BaseObject
{
    /**
     * The agenda's calendar number. Starts at 1 at the beginning of each calendar year.
     */
    private int number;

    /**
     * The list of addendum to the agenda.
     */
    private Map<String, SenagendaInfoAddendum> senagendaAddendum;

    /**
     * The list of vote addendum to the agenda.
     */
    private Map<String, SenagendaVoteAddendum> senagendaVoteAddendum;

    public Senagenda()
    {
    }

    public Senagenda(Integer number, Integer session, Integer year)
    {
        super();
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
        return "senagenda-"+this.session+"-"+this.year;
    }

    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Senagenda) {
            return ((Senagenda)obj).getOid().equals(this.getOid());
        }
        return false;
    }

    public String getOtype()
    {
        return "senagenda";
    }

    public Map<String, SenagendaVoteAddendum> getSenagendaVoteAddendum()
    {
        return senagendaVoteAddendum;
    }

    public void setSenagendaVoteAddendum(Map<String, SenagendaVoteAddendum> senagendaVoteAddendum)
    {
        this.senagendaVoteAddendum = senagendaVoteAddendum;
    }

    public Map<String, SenagendaInfoAddendum> getSenagendaAddendum()
    {
        return senagendaAddendum;
    }

    public void setSenagendaAddendum(Map<String, SenagendaInfoAddendum> senagendaAddendum)
    {
        this.senagendaAddendum = senagendaAddendum;
    }
}
