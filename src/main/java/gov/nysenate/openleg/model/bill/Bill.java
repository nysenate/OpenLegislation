package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.util.DateHelper;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.*;

/**
 * The Bill class serves as a container for all the entities that can be classified under a print number
 * and session year. It contains a collection of amendments (including the base amendment) as well as
 * shared information such as the sponsor or actions.
 */
public class Bill extends BaseLegislativeContent implements Serializable, Comparable<Bill>
{
    private static final long serialVersionUID = 2925424993477789289L;

    /** A number assigned to a bill when it's introduced in the Legislature. Each printNo begins with a
     *  letter (A for Assembly, S for Senate) followed by 1 to 5 digits. This printNo is valid only for the
     *  2 year session period. */
    protected String printNo = "";

    /** Starting with the terms "An act", it's a short description about the topic of the bill. */
    protected String title = "";

    /** The section of the law the bill affects. */
    protected String lawSection = "";

    /** The law code of the bill. */
    protected String law = "";

    /** An overview of a bill that list's specific sections of NYS law to be amended by that bill. */
    protected String summary = "";

    /** A letter at the end of the printNo indicates the amendment version.
     *  This is a mapping of amendment versions to Amendment objects (includes base amendment). */
    protected Map<String, BillAmendment> amendmentMap = new TreeMap<>();

    /** Indicates the amendment version that is currently active for this bill. */
    protected String activeVersion = BillId.BASE_VERSION;

    /** The Legislator who formally introduced the bill. */
    protected BillSponsor sponsor;

    /** A list of coSponsors to be given preferential display treatment. */
    protected List<Member> additionalSponsors = new ArrayList<>();

    /** A list of committees this bill has been referred to. */
    protected SortedSet<CommitteeVersionId> pastCommittees = new TreeSet<>();

    /** A list of actions that have been made on this bill. */
    protected List<BillAction> actions = new ArrayList<>();

    /** A list of ids for versions of this legislation in previous sessions. */
    protected Set<BillId> previousVersions = new HashSet<>();

    /** Designates the type of program bill, if applicable. */
    protected String programInfo = "";

    /** --- Constructors --- */

    public Bill() {
        super();
    }

    public Bill(String printNo, int sessionYear) {
        this();
        this.printNo = printNo;
        this.session = DateHelper.resolveSession(sessionYear);
    }

    public Bill(BillId billId) {
        this(billId.getBasePrintNo(), billId.getSession());
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Bill) {
            Bill other = (Bill)obj;
            return this.getBillId().equals(other.getBillId());
        }
        else {
            return false;
        }
    }

    @Override
    public int compareTo(Bill bill) {
        return this.getBillId().compareTo(bill.getBillId());
    }

    @Override
    public Date getPublishDate() {
        return this.publishDate;
    }

    /**
     * Set the publish date of the bill container.
     */
    @Override
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
        this.year = new LocalDate(publishDate).getYear();
    }

    @Override
    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    @Override
    public String toString() {
        return this.getBillId().toString();
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Returns a reference that identifies this base bill.
     */
    public BaseBillId getBillId() {
        return new BaseBillId(this.printNo, this.session);
    }

    /**
     * Returns the BillType which contains info such as the prefix and chamber.
     */
    public BillType getBillType() {
        return this.getBillId().getBillType();
    }

    /**
     * Indicate if this bill is a resolution.
     *
     * @return - True if this bill is a resolution of some sort.
     */
    public boolean isResolution() {
        return getBillType().isResolution();
    }

    /**
     * Retrieves an amendment stored in this bill using the version as the key.
     *
     * @param version - The amendment version of the bill (e.g "A", "B", etc)
     * @return BillAmendment
     * @throws BillAmendNotFoundEx if the bill amendment does not exist
     */
    public BillAmendment getAmendment(String version) throws BillAmendNotFoundEx {
        if (this.hasAmendment(version)) {
            return this.amendmentMap.get(version.toUpperCase());
        }
        throw new BillAmendNotFoundEx(new BillId(printNo, session, version));
    }

    /**
     * Retrieves a list of all amendments stored in this bill.
     */
    public List<BillAmendment> getAmendmentList() {
        return new ArrayList<>(this.amendmentMap.values());
    }

    /**
     * Associate an amendment with this bill.
     *
     * @param billAmendment - Amendment to add to this bill.
     */
    public void addAmendment(BillAmendment billAmendment) {
        this.amendmentMap.put(billAmendment.getVersion().toUpperCase(), billAmendment);
    }

    /**
     * Associate a list of amendments with this bill.
     *
     * @param billAmendments - List<Amendment> - Amendments to add to this bill
     */
    public void addAmendments(List<BillAmendment> billAmendments) {
        for (BillAmendment billAmendment : billAmendments) {
            this.addAmendment(billAmendment);
        }
    }

    /**
     * Indicate whether the bill has a reference to a given amendment version.
     *
     * @param version String - Amendment version
     * @return boolean - true if amendment exists, false otherwise
     */
    public boolean hasAmendment(String version) {
        return this.amendmentMap.containsKey(version.toUpperCase()) &&
               this.amendmentMap.get(version.toUpperCase()) != null;
    }

    /**
     * Indicate if the bill has a reference to the active amendment version.
     */
    public boolean hasActiveAmendment() {
        return hasAmendment(this.activeVersion);
    }

    /**
     * Convenience method to retrieve the currently active Amendment object.
     *
     * @return BillAmendment
     * @throws BillAmendNotFoundEx if the bill amendment does not exist
     */
    public BillAmendment getActiveAmendment() throws BillAmendNotFoundEx {
        return this.getAmendment(this.getActiveVersion());
    }

    /**
     * Add the bill id to the previous bill versions list.
     */
    public void addPreviousVersion(BillId previousVersion) {
        if(!previousVersions.contains(previousVersion)) {
            previousVersions.add(previousVersion);
        }
    }

    /**
     * Add an action to the list of actions.
     */
    public void addAction(BillAction action) {
        actions.add(action);
    }

    /**
     * Adds a committee to the list of past committees.
     */
    public void addPastCommittee(CommitteeVersionId committeeVersionId) {
        pastCommittees.add(committeeVersionId);
    }

    public void setPastCommittees(SortedSet<CommitteeVersionId> pastCommittees) {
        this.pastCommittees.addAll(pastCommittees);
    }

    /** --- Delegates --- */

    public String getFulltext() {
        return this.getActiveAmendment().getFulltext();
    }

    /** --- Basic Getters/Setters --- */

    public String getPrintNo() {
        return printNo;
    }

    public void setPrintNo(String printNo) {
        this.printNo = printNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLawSection() {
        return lawSection;
    }

    public void setLawSection(String lawSection) {
        this.lawSection = lawSection;
    }

    public String getLaw() {
        return law;
    }

    public void setLaw(String law) {
        this.law = law;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(String activeVersion) {
        this.activeVersion = activeVersion;
    }

    public Map<String, BillAmendment> getAmendmentMap() {
        return amendmentMap;
    }

    public Set<BillId> getPreviousVersions() {
        return previousVersions;
    }

    public void setPreviousVersions(Set<BillId> previousVersions) {
        this.previousVersions = previousVersions;
    }

    public List<BillAction> getActions() {
        return actions;
    }

    public void setActions(List<BillAction> actions) {
        this.actions = actions;
    }

    public BillSponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(BillSponsor sponsor) {
        this.sponsor = sponsor;
    }

    public SortedSet<CommitteeVersionId> getPastCommittees() {
        return pastCommittees;
    }

    public List<Member> getAdditionalSponsors() {
        return additionalSponsors;
    }

    public void setAdditionalSponsors(List<Member> additionalSponsors) {
        this.additionalSponsors = additionalSponsors;
    }

    public String getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(String programInfo) {
        this.programInfo = programInfo;
    }
}