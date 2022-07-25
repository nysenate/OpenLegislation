package gov.nysenate.openleg.legislation.agenda;

import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillVote;
import gov.nysenate.openleg.legislation.committee.CommitteeId;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @param voteAction Indicates the outcome of the vote.
 * @param referCommittee Specifies the committee the bill was referred to (if applicable).
 * @param isWithAmendment If the bill was amended before being reported out.
 * @param billVote Details regarding the vote.
 */
public record AgendaVoteBill(AgendaVoteAction voteAction, CommitteeId referCommittee,
                             boolean isWithAmendment, BillVote billVote) implements Serializable {
    @Serial
    private static final long serialVersionUID = 8418895620868449773L;

    public AgendaVoteBill withBillVote(BillVote billVote) {
        return new AgendaVoteBill(voteAction, referCommittee, isWithAmendment, billVote);
    }

    /** --- Functional Getters/Setters --- */

    public BillId getBillId() {
        return this.billVote().getBillId();
    }
}