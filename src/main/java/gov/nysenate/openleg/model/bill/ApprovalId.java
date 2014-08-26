package gov.nysenate.openleg.model.bill;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;

public class ApprovalId implements Serializable, Comparable<ApprovalId>{

    private static final long serialVersionUID = -814579920389237357L;

    /** The year in which the approval was signed */
    private int year;
    
    /** The number id for the approval */
    private int approvalNumber;
    
    /** --- Constructors --- */

    public ApprovalId(int year, int approvalNumber) {
        this.year = year;
        this.approvalNumber = approvalNumber;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApprovalId approvalId = (ApprovalId) o;

        if (approvalNumber != approvalId.approvalNumber) return false;
        if (year != approvalId.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + approvalNumber;
        return result;
    }

    @Override
    public int compareTo(ApprovalId o) {
        return ComparisonChain.start()
                .compare(this.year, o.year)
                .compare(this.approvalNumber, o.approvalNumber)
                .result();
    }

    @Override
    public String toString() {
        return year + "-" + approvalNumber;
    }

    /** --- Getters/Setters --- */

    public int getYear() {
        return year;
    }

    public int getApprovalNumber() {
        return approvalNumber;
    }
}
