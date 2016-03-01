package gov.nysenate.openleg.model.spotcheck.senatesite.bill;

import gov.nysenate.openleg.client.view.bill.BillStatusView;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contains data for a bill as it exists on nysenate.gov
 */
public class SenateSiteBill {

    /** The date time when this reference was generated */
    protected LocalDateTime referenceDateTime;

    protected String basePrintNo;
    protected String printNo;
    protected String chamber;
    protected int sessionYear;
    protected String activeVersion;
    protected List<BillId> sameAs;
    protected List<BillId> previousVersions;
    protected boolean isAmended;
    protected boolean hasSameAs;

    protected LocalDateTime publishDate;

    protected List<BillAction> actions;

    protected List<BillStatusView> milestones;
    protected String lastStatus;
    protected String latestStatusCommittee;
    protected LocalDateTime lastStatusDate;

    protected String sponsor;
    protected List<String> coSponsors;
    protected List<String> multiSponsors;

    protected String title;
    protected String summary;
    protected String memo;
    protected String text;

    protected String lawCode;
    protected String lawSection;

    public SenateSiteBill(LocalDateTime referenceDateTime) {
        this.referenceDateTime = referenceDateTime;
    }

    /** --- Functional Getters --- */

    public BillId getBillId() {
        return new BillId(printNo, sessionYear);
    }

    public BaseBillId getBaseBillId() {
        return new BaseBillId(printNo, sessionYear);
    }

    public SpotCheckReferenceId getReferenceId() {
        return new SpotCheckReferenceId(SpotCheckRefType.SENATE_SITE_BILLS, referenceDateTime);
    }

    /** --- Getters / Setters --- */

    public String getLawSection() {
        return lawSection;
    }

    public void setLawSection(String lawSection) {
        this.lawSection = lawSection;
    }

    public String getLawCode() {
        return lawCode;
    }

    public void setLawCode(String lawCode) {
        this.lawCode = lawCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getMultiSponsors() {
        return multiSponsors;
    }

    public void setMultiSponsors(List<String> multiSponsors) {
        this.multiSponsors = multiSponsors;
    }

    public List<String> getCoSponsors() {
        return coSponsors;
    }

    public void setCoSponsors(List<String> coSponsors) {
        this.coSponsors = coSponsors;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public LocalDateTime getLastStatusDate() {
        return lastStatusDate;
    }

    public void setLastStatusDate(LocalDateTime lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }

    public String getLatestStatusCommittee() {
        return latestStatusCommittee;
    }

    public void setLatestStatusCommittee(String latestStatusCommittee) {
        this.latestStatusCommittee = latestStatusCommittee;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public List<BillStatusView> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<BillStatusView> milestones) {
        this.milestones = milestones;
    }

    public List<BillAction> getActions() {
        return actions;
    }

    public void setActions(List<BillAction> actions) {
        this.actions = actions;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public boolean isHasSameAs() {
        return hasSameAs;
    }

    public void setHasSameAs(boolean hasSameAs) {
        this.hasSameAs = hasSameAs;
    }

    public boolean isAmended() {
        return isAmended;
    }

    public void setAmended(boolean amended) {
        isAmended = amended;
    }

    public List<BillId> getPreviousVersions() {
        return previousVersions;
    }

    public void setPreviousVersions(List<BillId> previousVersions) {
        this.previousVersions = previousVersions;
    }

    public List<BillId> getSameAs() {
        return sameAs;
    }

    public void setSameAs(List<BillId> sameAs) {
        this.sameAs = sameAs;
    }

    public String getBasePrintNo() {
        return basePrintNo;
    }

    public void setBasePrintNo(String basePrintNo) {
        this.basePrintNo = basePrintNo;
    }

    public String getPrintNo() {
        return printNo;
    }

    public void setPrintNo(String printNo) {
        this.printNo = printNo;
    }

    public int getSessionYear() {
        return sessionYear;
    }

    public void setSessionYear(int sessionYear) {
        this.sessionYear = sessionYear;
    }

    public String getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(String activeVersion) {
        this.activeVersion = activeVersion;
    }

    public String getChamber() {
        return chamber;
    }

    public void setChamber(String chamber) {
        this.chamber = chamber;
    }
}
