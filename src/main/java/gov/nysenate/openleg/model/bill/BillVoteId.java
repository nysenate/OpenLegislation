package gov.nysenate.openleg.model.bill;

import java.io.Serializable;
import java.util.Date;

/**
 * Used to uniquely identify a vote taken on a bill.
 */
public class BillVoteId implements Serializable, Comparable
{
    private static final long serialVersionUID = 4633347135274137824L;

    private BillId billId;
    private Date voteDate;
    private BillVoteType voteType;
    private int sequenceNo;

    public BillVoteId(BillId billId, Date voteDate, BillVoteType voteType, int sequenceNo) {
        this.billId = billId;
        this.voteDate = voteDate;
        this.voteType = voteType;
        this.sequenceNo = sequenceNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillVoteId)) return false;
        BillVoteId that = (BillVoteId) o;
        if (sequenceNo != that.sequenceNo) return false;
        if (billId != null ? !billId.equals(that.billId) : that.billId != null) return false;
        if (voteDate != null ? !voteDate.equals(that.voteDate) : that.voteDate != null) return false;
        if (voteType != that.voteType) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = billId != null ? billId.hashCode() : 0;
        result = 31 * result + (voteDate != null ? voteDate.hashCode() : 0);
        result = 31 * result + (voteType != null ? voteType.hashCode() : 0);
        result = 31 * result + sequenceNo;
        return result;
    }

    @Override
    public int compareTo(Object o) {
        BillVoteId oBillVoteId = (BillVoteId) o;
        int res = this.billId.compareTo(oBillVoteId.billId);
        if (res == 0) res = this.voteDate.compareTo(oBillVoteId.voteDate);
        if (res == 0) res = this.voteType.compareTo(oBillVoteId.voteType);
        if (res == 0) res = Integer.compare(this.sequenceNo , oBillVoteId.sequenceNo);
        return res;
    }
}