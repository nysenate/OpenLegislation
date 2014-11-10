package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillStatus;

public class BillStatusView implements ViewObject
{
    protected String statusType;
    protected String statusDesc;
    protected String actionDate;
    protected String committeeName;
    protected Integer billCalNo;

    public BillStatusView(BillStatus billStatus) {
        if(billStatus != null) {
            if (billStatus.getStatusType() != null) {
                this.statusType = billStatus.getStatusType().name();
                this.statusDesc = billStatus.getStatusType().getDesc();
                this.actionDate = billStatus.getActionDate().toString();
            }
            this.committeeName = billStatus.getCommitteeId() != null ? billStatus.getCommitteeId().getName() : null;
            this.billCalNo = billStatus.getCalendarNo();
        }
    }

    public String getStatusType() {
        return statusType;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public String getActionDate() {
        return actionDate;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public Integer getBillCalNo() {
        return billCalNo;
    }

    @Override
    public String getViewType() {
        return "bill-status";
    }
}
