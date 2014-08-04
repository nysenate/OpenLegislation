package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillVote;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;

public class AgendaVoteBill implements Serializable
{
    private static final long serialVersionUID = 8418895620868449773L;

    /** Indicates the outcome of the vote. */
    private AgendaVoteAction voteAction;

    /** Specifies the committee the bill was referred to (if applicable). */
    private CommitteeId referCommittee;

    /** Indicates if the bill was amended before being reported out. */
    private boolean withAmendment;

    /** Details regarding the vote. */
    private BillVote billVote;

    /** --- Constructors --- */

    public AgendaVoteBill() {}

    public AgendaVoteBill(AgendaVoteAction voteAction, CommitteeId referCommittee, boolean withAmendment) {
        this.voteAction = voteAction;
        this.referCommittee = referCommittee;
        this.withAmendment = withAmendment;
    }

    /** --- Functional Getters/Setters --- */

    public BillId getBillId() {
        return this.getBillVote().getBillId();
    }

    /** --- Basic Getters/Setters --- */

    public AgendaVoteAction getVoteAction() {
        return voteAction;
    }

    public void setVoteAction(AgendaVoteAction voteAction) {
        this.voteAction = voteAction;
    }

    public CommitteeId getReferCommittee() {
        return referCommittee;
    }

    public void setReferCommittee(CommitteeId referCommittee) {
        this.referCommittee = referCommittee;
    }

    public boolean isWithAmendment() {
        return withAmendment;
    }

    public void setWithAmendment(boolean withAmendment) {
        this.withAmendment = withAmendment;
    }

    public BillVote getBillVote() {
        return billVote;
    }

    public void setBillVote(BillVote billVote) {
        this.billVote = billVote;
    }
}