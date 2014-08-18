package gov.nysenate.openleg.model.daybreak;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.util.BillTextUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A DaybreakBill serves as a model to perform comparisons between the bill data extracted
 * from the DaybreakFragments and our own processed bill data.
 */
public class DaybreakBill
{
    /** Below are a subset of the fields in {@link gov.nysenate.openleg.model.bill.Bill} */

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

    /**
     * Creates a new DaybreakBill instance from the given Bill. The Bill
     * cannot be null or an IllegalArgumentException will be thrown.
     */
    public static DaybreakBill from(Bill bill) {
        if (bill != null) {
            DaybreakBill dBill = new DaybreakBill();
            dBill.baseBillId = bill.getBaseBillId();
            dBill.activeVersion = bill.getActiveVersion();
            dBill.title = bill.getTitle();
            dBill.sponsor = bill.getSponsor();
            if (bill.hasActiveAmendment()) {
                BillAmendment amend = bill.getActiveAmendment();
                dBill.cosponsors = amend.getCoSponsors();
                dBill.multiSponsors = amend.getMultiSponsors();
                dBill.sameAs = amend.getSameAs();
            }
            dBill.summary = bill.getSummary();
            dBill.lawSection = bill.getLawSection();
            dBill.lawCode = bill.getLaw();
            dBill.actions = bill.getActions();
            dBill.pageCounts = new HashMap<>();
            bill.getAmendmentMap().forEach((k,v) -> {
                dBill.pageCounts.put(k, BillTextUtils.getPageCount(v.getFullText()));
            });
            dBill.publishDates = new HashMap<>();
            bill.getAmendPublishStatusMap().forEach((k,v) -> {
                if (v.isPublished()) dBill.publishDates.put(k, v.getEffectDateTime().toLocalDate());
            });
            return dBill;
        }
        else {
            throw new IllegalArgumentException("Supplied Bill cannot be null");
        }
    }

    /** --- Basic Getters/Setters --- */

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
