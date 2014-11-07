package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.CommitteeId;

/**
 * Contains a sample of the fields in {@link gov.nysenate.openleg.model.bill.Bill}.
 * This is useful for retrieving a quick summary of a bill instead of composing
 * the entire Bill model which may take longer to create.
 */
public class BillInfo
{
    protected BillId billId;
    protected Version activeVersion;
    protected String title;
    protected String summary;
    protected BillStatus status;
    protected BillSponsor sponsor;
    protected CommitteeId currentCommittee;
    protected ProgramInfo programInfo;

    /** --- Constructors --- */

    public BillInfo() {}

    public BillInfo(Bill bill) {
        this.billId = bill.getBaseBillId();
        this.activeVersion = bill.getActiveVersion();
        this.title = bill.getTitle();
        this.summary = bill.getSummary();
        this.status = bill.getStatus();
        this.sponsor = bill.getSponsor();
        this.programInfo = bill.getProgramInfo();
        if (bill.hasActiveAmendment()) {
            this.currentCommittee = bill.getActiveAmendment().getCurrentCommittee();
        }
    }

    /** --- Basic Getters/Setters --- */

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }

    public Version getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(Version activeVersion) {
        this.activeVersion = activeVersion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public BillSponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(BillSponsor sponsor) {
        this.sponsor = sponsor;
    }

    public CommitteeId getCurrentCommittee() {
        return currentCommittee;
    }

    public void setCurrentCommittee(CommitteeId currentCommittee) {
        this.currentCommittee = currentCommittee;
    }

    public ProgramInfo getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(ProgramInfo programInfo) {
        this.programInfo = programInfo;
    }
}
