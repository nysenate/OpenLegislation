package gov.nysenate.openleg.legislation.attendance;

import gov.nysenate.openleg.legislation.bill.BillVoteType;

import java.time.LocalDate;

public class VoteId {
    private LocalDate voteDate;
    private int sequenceNo;
    private BillVoteType voteType;

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
}
