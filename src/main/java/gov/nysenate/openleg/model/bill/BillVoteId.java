package gov.nysenate.openleg.model.bill;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Used to uniquely identify a vote taken on a bill. The primary purpose of this
 * should be to identify BillVote objects regardless of their actual vote tallies.
 */
public class BillVoteId implements Serializable, Comparable<BillVoteId>
{
    private static final long serialVersionUID = 4633347135274137824L;

    private BillId billId;
    private LocalDate voteDate;
    private BillVoteType voteType;
    private int sequenceNo;
    private CommitteeId committeeId;

    public BillVoteId(BillId billId, LocalDate voteDate, BillVoteType voteType, int sequenceNo, CommitteeId committeeId) {
        this.billId = billId;
        this.voteDate = voteDate;
        this.voteType = voteType;
        this.sequenceNo = sequenceNo;
        this.committeeId = committeeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BillVoteId other = (BillVoteId) obj;
        return Objects.equals(this.billId, other.billId) &&
               Objects.equals(this.voteDate, other.voteDate) &&
               Objects.equals(this.voteType, other.voteType) &&
               Objects.equals(this.sequenceNo, other.sequenceNo) &&
               Objects.equals(this.committeeId, other.committeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId, voteDate, voteType, sequenceNo, committeeId);
    }

    @Override
    public int compareTo(BillVoteId o) {
        return ComparisonChain.start()
            .compare(this.billId, o.billId)
            .compare(this.voteDate, o.voteDate)
            .compare(this.voteType, o.voteType)
            .compare(this.sequenceNo, o.sequenceNo)
            // Properly handle case when committee is not present, which would be the case of a floor vote.
            // The floor vote should come after a same-day committee vote.
            .compare(this.committeeId != null ? this.committeeId.toString() : "Z", o.committeeId != null ? o.committeeId.toString() : "Z")
            .result();
    }

    @Override
    public String toString() {
        return "BillVoteId{" +
                "billId=" + billId +
                ", voteDate=" + voteDate +
                ", voteType=" + voteType +
                ", sequenceNo=" + sequenceNo +
                ", committeeId=" + committeeId +
                '}';
    }

    public BillId getBillId() {
        return billId;
    }

    public LocalDate getVoteDate() {
        return voteDate;
    }

    public BillVoteType getVoteType() {
        return voteType;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public CommitteeId getCommitteeId() {
        return committeeId;
    }
}