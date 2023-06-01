package gov.nysenate.openleg.legislation.attendance;

import gov.nysenate.openleg.legislation.bill.BillVote;
import gov.nysenate.openleg.legislation.bill.BillVoteType;

import java.time.LocalDate;
import java.util.Objects;

public class VoteId {
    private LocalDate voteDate;
    private int sequenceNo;
    private BillVoteType voteType;

    public VoteId(BillVote billVote) {
        this(billVote.getVoteDate(), billVote.getSequenceNo(), billVote.getVoteType());
    }

    public VoteId(LocalDate voteDate, int sequenceNo, BillVoteType voteType) {
        this.voteDate = voteDate;
        this.sequenceNo = sequenceNo;
        this.voteType = voteType;
    }

    public LocalDate getVoteDate() {
        return voteDate;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public BillVoteType getVoteType() {
        return voteType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoteId voteId = (VoteId) o;
        return sequenceNo == voteId.sequenceNo && Objects.equals(voteDate, voteId.voteDate) && voteType == voteId.voteType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(voteDate, sequenceNo, voteType);
    }
}
