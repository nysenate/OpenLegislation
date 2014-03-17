package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.List;

public class SenagendaVoteItem
{
    public Bill bill;
    public String action;
    public String referCommittee;
    public Boolean withAmd;
    public List<MeetingVote> votes;

    public SenagendaVoteItem()
    {
        this.votes = new ArrayList<MeetingVote>();
    }

    public SenagendaVoteItem(Bill bill, String action, String referCommittee, Boolean withAmd)
    {
        super();
        this.bill = bill;
        this.action = action;
        this.withAmd = withAmd;
        this.referCommittee = referCommittee;
    }
}
