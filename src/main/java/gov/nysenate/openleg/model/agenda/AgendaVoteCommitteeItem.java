package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.processors.SenagendaProcessor;

import java.util.ArrayList;
import java.util.List;

public class AgendaVoteCommitteeItem
{
    private Bill bill;
    private String billAmendment;
    private SenagendaProcessor.VoteAction action;
    private String referCommittee;
    private Boolean withAmd;
    private List<AgendaVoteCommitteeVote> votes;

    public AgendaVoteCommitteeItem()
    {
        this.setVotes(new ArrayList<AgendaVoteCommitteeVote>());
    }

    public AgendaVoteCommitteeItem(Bill bill, String billAmendment, SenagendaProcessor.VoteAction action, String referCommittee, Boolean withAmd)
    {
        this();
        this.setBill(bill);
        this.setBillAmendment(billAmendment);
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

    public List<AgendaVoteCommitteeVote> getVotes()
    {
        return votes;
    }

    public void setVotes(List<AgendaVoteCommitteeVote> votes)
    {
        this.votes = votes;
    }

    public void addVote(AgendaVoteCommitteeVote vote)
    {
        this.votes.add(vote);
    }

    public String getBillAmendment()
    {
        return billAmendment;
    }

    public void setBillAmendment(String billAmendment)
    {
        this.billAmendment = billAmendment;
    }
}
