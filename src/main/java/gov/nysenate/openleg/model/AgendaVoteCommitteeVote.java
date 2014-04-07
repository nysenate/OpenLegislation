package gov.nysenate.openleg.model;

public class AgendaVoteCommitteeVote
{
    private String name;
    private String rank;
    private String party;
    private String vote;

    public AgendaVoteCommitteeVote()
    {

    }

    public AgendaVoteCommitteeVote(String name, String rank, String party, String vote)
    {
        this();
        this.setName(name);
        this.setRank(rank);
        this.setParty(party);
        this.setVote(vote);
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

    public String getVote()
    {
        return vote;
    }

    public void setVote(String vote)
    {
        this.vote = vote;
    }

}
