package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.bill.view.BillIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BillVoteView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeIdView;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteBill;

public class AgendaVoteBillView implements ViewObject
{
    private BillIdView bill;
    private String action;
    private CommitteeIdView referCommittee;
    private boolean amended;
    private BillVoteView vote;

    public AgendaVoteBillView(AgendaVoteBill voteBill) {
        if (voteBill != null) {
            this.bill = new BillIdView(voteBill.getBillId());
            this.action = voteBill.getVoteAction().name();
            this.referCommittee = voteBill.getReferCommittee() != null
                                  ? new CommitteeIdView(voteBill.getReferCommittee()) : null;
            this.amended = voteBill.isWithAmendment();
            this.vote = new BillVoteView(voteBill.getBillVote());
        }
    }

    //Added for Json Deserialization
    protected AgendaVoteBillView() {}

    public BillIdView getBill() {
        return bill;
    }

    public String getAction() {
        return action;
    }

    public CommitteeIdView getReferCommittee() {
        return referCommittee;
    }

    public boolean isAmended() {
        return amended;
    }

    public BillVoteView getVote() {
        return vote;
    }

    @Override
    public String getViewType() {
        return "agenda-vote-bill";
    }
}
