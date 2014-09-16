package gov.nysenate.openleg.service.bill.data;


import gov.nysenate.openleg.model.bill.ApprovalId;
import gov.nysenate.openleg.model.bill.BaseBillId;

public class ApprovalNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1195994446973944621L;

    protected ApprovalId approvalId;
    protected BaseBillId baseBillId;
    protected int year;

    public ApprovalNotFoundException(Throwable cause, ApprovalId approvalId) {
        super(
                "ApprovalMessage "+ approvalId + " could not be retrieved"
                , cause);
        this.approvalId = approvalId;
    }

    public ApprovalNotFoundException(Throwable cause, BaseBillId baseBillId) {
        super(
                "Could not retrieve approvals for bill " + baseBillId
                , cause);
        this.baseBillId = baseBillId;
    }

    public ApprovalNotFoundException(Throwable cause, int year) {
        super(
                "Could not retrieve approvals for year " + year
                , cause);
        this.baseBillId = baseBillId;
    }

    public ApprovalId getApprovalId() {
        return approvalId;
    }

    public BaseBillId getBaseBillId() {
        return baseBillId;
    }
}

