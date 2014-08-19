package gov.nysenate.openleg.model.daybreak;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillSponsor;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A DaybreakBill serves as a model to store extracted bill content from the DaybreakFragments.
 */
public class DaybreakBill implements SpotCheckReference
{
    /** Id of the fragment that created this instance. */
    protected DaybreakFragmentId daybreakFragmentId;

    /** Below are a subset of fields similarly found in {@link gov.nysenate.openleg.model.bill.Bill} */

    protected BaseBillId baseBillId;
    protected Version activeVersion;
    protected String title;
    protected BillSponsor sponsor;
    protected List<Member> cosponsors;
    protected List<Member> multiSponsors;
    protected String summary;
    protected String lawSection;
    protected String lawCode;
    protected List<BillAction> actions;
    protected Set<BillId> sameAs;

    /** Mapping of amendment versions to the number of pages in the full text. */
    protected Map<Version, Integer> pageCounts;

    /** Mapping of amendment versions to the publish date of that amendment. */
    protected Map<Version, LocalDate> publishDates;

    /** --- Constructors --- */

    public DaybreakBill() {}

    public DaybreakBill(DaybreakFragmentId daybreakFragmentId, BaseBillId baseBillId) {
        this.daybreakFragmentId = daybreakFragmentId;
        this.baseBillId = baseBillId;
    }

    /** --- Implemented Methods --- */

    @Override
    public SpotCheckRefType getRefType() {
        return SpotCheckRefType.LBDC_DAYBREAK;
    }

    @Override
    public String getRefId() {
        return daybreakFragmentId + ":" + baseBillId;
    }

    @Override
    public LocalDateTime getRefActiveDate() {
        return daybreakFragmentId.getReportDate().atStartOfDay();
    }

    /** --- Basic Getters/Setters --- */

    public DaybreakFragmentId getDaybreakFragmentId() {
        return daybreakFragmentId;
    }

    public void setDaybreakFragmentId(DaybreakFragmentId daybreakFragmentId) {
        this.daybreakFragmentId = daybreakFragmentId;
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

    public BillSponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(BillSponsor sponsor) {
        this.sponsor = sponsor;
    }

    public List<Member> getCosponsors() {
        return cosponsors;
    }

    public void setCosponsors(List<Member> cosponsors) {
        this.cosponsors = cosponsors;
    }

    public List<Member> getMultiSponsors() {
        return multiSponsors;
    }

    public void setMultiSponsors(List<Member> multiSponsors) {
        this.multiSponsors = multiSponsors;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLawCode() {
        return lawCode;
    }

    public void setLawCode(String lawCode) {
        this.lawCode = lawCode;
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

    public Set<BillId> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<BillId> sameAs) {
        this.sameAs = sameAs;
    }

    public Map<Version, Integer> getPageCounts() {
        return pageCounts;
    }

    public void setPageCounts(Map<Version, Integer> pageCounts) {
        this.pageCounts = pageCounts;
    }

    public Map<Version, LocalDate> getPublishDates() {
        return publishDates;
    }

    public void setPublishDates(Map<Version, LocalDate> publishDates) {
        this.publishDates = publishDates;
    }
}
