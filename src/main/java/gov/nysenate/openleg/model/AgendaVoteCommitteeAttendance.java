package gov.nysenate.openleg.model;

public class AgendaVoteCommitteeAttendance
{
    private String name;
    private String rank;
    private String party;
    private String attendance;

    public AgendaVoteCommitteeAttendance()
    {

    }

    public AgendaVoteCommitteeAttendance(String name, String rank, String party, String attendance)
    {
        this();
        this.setName(name);
        this.setRank(rank);
        this.setParty(party);
        this.setAttendance(attendance);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getRank()
    {
        return rank;
    }

    public void setRank(String rank)
    {
        this.rank = rank;
    }

    public String getParty()
    {
        return party;
    }

    public void setParty(String party)
    {
        this.party = party;
    }

    public String getAttendance()
    {
        return attendance;
    }

    public void setAttendance(String attendance)
    {
        this.attendance = attendance;
    }
}
