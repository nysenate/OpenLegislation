package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.base.Version;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Contains a sample of the fields in {@link gov.nysenate.openleg.model.bill.Bill}.
 * This is useful for retrieving a quick summary of a bill instead of composing
 * the entire Bill model which may take longer to create.
 */
public class BillInfo
{
    protected BillId billId;
    protected Version activeVersion;
    protected int year;
    protected LocalDateTime publishedDateTime;
    protected String title;
    protected String summary;
    protected BillStatus status;
    protected BillSponsor sponsor;
    protected List<BillStatus> milestones = new ArrayList<>();
    protected List<BillAction> actions = new ArrayList<>();
    protected Map<Version, PublishStatus> amendPublishStatusMap = new EnumMap<>(Version.class);
    protected BillId substitutedBy;
    protected ProgramInfo programInfo;
    protected BillId reprintOf;

    /* --- Constructors --- */

    public BillInfo() {}

    public BillInfo(Bill bill) {
        this.billId = bill.getBaseBillId();
        this.activeVersion = bill.getActiveVersion();
        this.year = bill.getYear();
        this.publishedDateTime = bill.getPublishedDateTime();
        this.title = bill.getTitle();
        this.summary = bill.getSummary();
        this.status = bill.getStatus();
        this.sponsor = bill.getSponsor();
        this.substitutedBy = bill.getSubstitutedBy();
        this.programInfo = bill.getProgramInfo();
        this.milestones = bill.getMilestones();
        this.actions = bill.getActions();
        this.amendPublishStatusMap = new TreeMap<>(bill.getAmendPublishStatusMap());
        this.reprintOf = bill.getReprintOf();
    }

    /* --- Functional Getters / Setters --- */

    /**
     * Indicates if the base amendment is published.
     * @return boolean
     */
    public boolean isBaseVersionPublished() {
        return (this.amendPublishStatusMap.containsKey(Version.ORIGINAL) &&
                this.amendPublishStatusMap.get(Version.ORIGINAL).isPublished());
    }

    /* --- Basic Getters/Setters --- */

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

    public int getYear() {
        return year;
    }

    public LocalDateTime getPublishedDateTime() {
        return publishedDateTime;
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

    public List<BillStatus> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<BillStatus> milestones) {
        this.milestones = milestones;
    }

    public List<BillAction> getActions() {
        return actions;
    }

    public ProgramInfo getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(ProgramInfo programInfo) {
        this.programInfo = programInfo;
    }

    public BillId getSubstitutedBy() {
        return substitutedBy;
    }

    public void setSubstitutedBy(BillId substitutedBy) {
        this.substitutedBy = substitutedBy;
    }

    public Map<Version, PublishStatus> getAmendPublishStatusMap() {
        return amendPublishStatusMap;
    }

    public void setAmendPublishStatusMap(Map<Version, PublishStatus> amendPublishStatusMap) {
        this.amendPublishStatusMap = amendPublishStatusMap;
    }

    public BillId getReprintOf() {return reprintOf;}

    public void setReprintOf(BillId reprintOf) {this.reprintOf = reprintOf;}
}
