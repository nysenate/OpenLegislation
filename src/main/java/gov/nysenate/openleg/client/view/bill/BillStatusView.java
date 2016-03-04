package gov.nysenate.openleg.client.view.bill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillStatus;
import org.apache.commons.lang3.text.WordUtils;

import java.time.LocalDate;

public class BillStatusView implements ViewObject
{
    protected String statusType;
    protected String statusDesc;
    protected LocalDate actionDate;
    protected String committeeName;
    protected Integer billCalNo;

    protected BillStatusView() {}

    public BillStatusView(BillStatus billStatus) {
        if(billStatus != null) {
            if (billStatus.getStatusType() != null) {
                this.statusType = billStatus.getStatusType().name();
                this.statusDesc = billStatus.getStatusType().getDesc();
                this.actionDate = billStatus.getActionDate();
            }
            this.committeeName = billStatus.getCommitteeId() != null ?
                WordUtils.capitalizeFully(billStatus.getCommitteeId().getName()) : null;
            this.billCalNo = billStatus.getCalendarNo();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillStatusView)) return false;
        BillStatusView that = (BillStatusView) o;
        return Objects.equal(statusType, that.statusType) &&
                Objects.equal(statusDesc, that.statusDesc) &&
                Objects.equal(actionDate, that.actionDate) &&
                Objects.equal(committeeName, that.committeeName) &&
                Objects.equal(billCalNo, that.billCalNo);
    }

    @Override
    @JsonIgnore
    public int hashCode() {
        return Objects.hashCode(statusType, statusDesc, actionDate, committeeName, billCalNo);
    }

    public String getStatusType() {
        return statusType;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public LocalDate getActionDate() {
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
