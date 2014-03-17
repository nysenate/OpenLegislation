package gov.nysenate.openleg.model;

import gov.nysenate.openleg.processors.SenagendaProcessor;

import java.util.ArrayList;
import java.util.List;

public class MeetingItem
{
    private Bill bill;
    private SenagendaProcessor.VoteAction action;
    private String referCommittee;
    private Boolean withAmd;
    private List<MeetingVote> votes;

    public MeetingItem()
    {
        this.setVotes(new ArrayList<MeetingVote>());
    }

    public MeetingItem(Bill bill, SenagendaProcessor.VoteAction action, String referCommittee, Boolean withAmd)
    {
        super();
        this.setBill(bill);
        this.setAction(action);
        this.setReferCommittee(referCommittee);
        this.setWithAmd(withAmd);
    }

    public Bill getBill()
    {
        return bill;
    }

    public void setBill(Bill bill)
    {
        this.bill = bill;
    }

    public SenagendaProcessor.VoteAction getAction()
    {
        return action;
    }

    public void setAction(SenagendaProcessor.VoteAction action)
    {
        this.action = action;
    }

    public String getReferCommittee()
    {
        return referCommittee;
    }

    public void setReferCommittee(String referCommittee)
    {
        this.referCommittee = referCommittee;
    }

    public Boolean getWithAmd()
    {
        return withAmd;
    }

    public void setWithAmd(Boolean withAmd)
    {
        this.withAmd = withAmd;
    }

    public List<MeetingVote> getVotes()
    {
        return votes;
    }

    public void setVotes(List<MeetingVote> votes)
    {
        this.votes = votes;
    }
}
