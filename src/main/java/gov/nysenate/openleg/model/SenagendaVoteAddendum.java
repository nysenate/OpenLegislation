package gov.nysenate.openleg.model;

import java.util.HashMap;

public class SenagendaVoteAddendum
{
    public String id;
    public HashMap<String, SenagendaVoteCommittee> committees;

    public SenagendaVoteAddendum()
    {
        this.committees = new HashMap<String, SenagendaVoteCommittee>();
    }

    public SenagendaVoteAddendum(String id)
    {
        this.id = id;
    }
}
