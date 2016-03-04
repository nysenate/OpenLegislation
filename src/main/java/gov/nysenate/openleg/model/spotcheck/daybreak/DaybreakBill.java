package gov.nysenate.openleg.model.spotcheck.daybreak;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A DaybreakBill serves as a model to store extracted bill content from the DaybreakFragments.
 */
public class DaybreakBill
{
    protected SpotCheckReferenceId referenceId;

    /** Date of the report that created this instance. */
    protected LocalDate reportDate;

    /** Below are a subset of fields similarly found in {@link gov.nysenate.openleg.model.bill.Bill} */

    protected BaseBillId baseBillId;
    protected Version activeVersion;
    protected String title;
    protected String sponsor;
    protected List<String> cosponsors;
    protected List<String> multiSponsors;
    protected String lawCodeAndSummary;
    protected String lawSection;
    protected List<BillAction> actions;

    /** Mapping of amendments to their version id. */
    protected Map<Version, DaybreakBillAmendment> amendments;

    /** --- Constructors --- */

    public DaybreakBill() {
        cosponsors = new ArrayList<>();
        multiSponsors = new ArrayList<>();
        actions = new ArrayList<>();
        amendments = new HashMap<>();
    }

    public DaybreakBill(DaybreakBillId daybreakBillId) {
        this();
        this.baseBillId = daybreakBillId.getBaseBillId();
        this.reportDate = daybreakBillId.getReportDate();
    }

    /** --- Functional Getters/Setters --- */

    public DaybreakBillId getDaybreakBillId(){
        return new DaybreakBillId(baseBillId, reportDate);
    }

    public SpotCheckReferenceId getReferenceId() {
        return new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, this.reportDate.atStartOfDay());
    }

    /** --- Basic Getters/Setters --- */

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public BaseBillId getBaseBillId() {
        return baseBillId;
    }

    public void setBaseBillId(BaseBillId baseBillId) {
        this.baseBillId = baseBillId;
    }

    public Version getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(Version activeVersion) {
        this.activeVersion = activeVersion;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public List<String> getCosponsors() {
        return cosponsors;
    }

    public void setCosponsors(List<String> cosponsors) {
        this.cosponsors = cosponsors;
    }

    public List<String> getMultiSponsors() {
        return multiSponsors;
    }

    public void setMultiSponsors(List<String> multiSponsors) {
        this.multiSponsors = multiSponsors;
    }

    public String getLawCodeAndSummary() {
        return lawCodeAndSummary;
    }

    public void setLawCodeAndSummary(String lawCodeAndSummary) {
        this.lawCodeAndSummary = lawCodeAndSummary;
    }

    public String getLawSection() {
        return lawSection;
    }

    public void setLawSection(String lawSection) {
        this.lawSection = lawSection;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<BillAction> getActions() {
        return actions;
    }

    public void setActions(List<BillAction> actions) {
        this.actions = actions;
    }

    public Map<Version, DaybreakBillAmendment> getAmendments() {
        return amendments;
    }

    public void setAmendments(Map<Version, DaybreakBillAmendment> amendments) {
        this.amendments = amendments;
    }
}