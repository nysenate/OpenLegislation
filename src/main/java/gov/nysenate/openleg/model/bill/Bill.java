package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.entity.Member;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * The Bill class serves as a container for all the entities that can be classified under a print number
 * and session year. It contains a collection of amendments (including the base amendment) as well as
 * shared information such as the sponsor, actions, etc.
 */
public class Bill extends BaseLegislativeContent implements Serializable, Comparable<Bill>
{
    private static final long serialVersionUID = 2925424993477789289L;

    /** The base bill id which should be used to uniquely identify this instance. */
    protected BaseBillId baseBillId;

    /** Starting with the terms "An act", it's a short description about the topic of the bill. */
    protected String title = "";

    /** The section of the law the bill affects. e.g (Vehicle And Traffic) */
    protected String lawSection = "";

    /** The law code of the bill. e.g (Amd §1373, Pub Health L) */
    protected String law = "";

    /** An overview of a bill that list's specific sections of NYS law to be amended by the bill. */
    protected String summary = "";

    /** A mapping of amendment versions to BillAmendment instances (includes base amendment). */
    protected Map<Version, BillAmendment> amendmentMap = new TreeMap<>();

    /** Publish status mapped by amendment versions. */
    protected Map<Version, PublishStatus> amendPublishStatusMap = new TreeMap<>();

    /** A list of veto messages for this bill */
    protected Map<VetoId, VetoMessage> vetoMessages = new HashMap<>();

    /** Indicates the amendment version that is currently active for this bill. */
    protected Version activeVersion = BillId.DEFAULT_VERSION;

    /** The Legislator who formally introduced the bill. */
    protected BillSponsor sponsor;

    /** A list of co-sponsors that will be given preferential display treatment. */
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

    public Bill() {}

    public Bill(BaseBillId baseBillId) {
        this.setBaseBillId(baseBillId);
        this.setSession(baseBillId.getSession());
    }

    /** --- Overrides --- */

    @Override
    public int compareTo(Bill other) {
        return this.getBaseBillId().compareTo(other.getBaseBillId());
    }

    /**
     * Set the publish date of the bill container and use that to set the active year of the bill.
     */
    @Override
    public void setPublishedDateTime(LocalDateTime publishDateTime) {
        super.setPublishedDateTime(publishDateTime);
        if (super.publishedDateTime != null) {
            // Sometimes bills are pre-filed before the session actually starts so we account for this.
            super.setYear(Integer.max(this.session.getYear(), publishDateTime.getYear()));
        }
        else {
            super.setYear(this.session.getYear());
        }
    }

    @Override
    public String toString() {
        return this.getBaseBillId().toString();
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Delegate to retrieve print no.
     */
    public String getBasePrintNo() {
        return this.getBaseBillId().getBasePrintNo();
    }

    /**
     * Returns the BillType which contains info such as the prefix and chamber.
     */
    public BillType getBillType() {
        return this.getBaseBillId().getBillType();
    }

    /**
     * Returns true if this bill is a resolution of some sort.
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
    public BillAmendment getAmendment(Version version) throws BillAmendNotFoundEx {
        if (this.hasAmendment(version)) {
            return this.amendmentMap.get(version);
        }
        throw new BillAmendNotFoundEx(baseBillId.withVersion(version));
    }

    /**
     * Retrieves a list of all amendments stored in this bill.
     */
    public List<BillAmendment> getAmendmentList() {
        return new ArrayList<>(this.amendmentMap.values());
    }

    /**
     * Associate an amendment with this bill. Replaces existing instance of the same version.
     *
     * @param billAmendment - Amendment to add to this bill. Cannot be null.
     */
    public void addAmendment(BillAmendment billAmendment) {
        if (billAmendment != null) {
            this.amendmentMap.put(billAmendment.getVersion(), billAmendment);
        }
        else {
            throw new IllegalArgumentException("Supplied BillAmendment cannot be null.");
        }
    }

    /**
     * Associate a list of amendments with this bill.
     *
     * @param billAmendments - List<Amendment> - Amendments to add to this bill
     */
    public void addAmendments(List<BillAmendment> billAmendments) {
        billAmendments.forEach(this::addAmendment);
    }

    /**
     * Retrieve a PublishStatus for a specific amendment version.
     *
     * @param version String - Amendment version
     * @return Optional<PublishStatus> - Value will be set if mapping exists.
     */
    public Optional<PublishStatus> getPublishStatus(Version version) {
        if (this.amendPublishStatusMap.containsKey(version)) {
            return Optional.of(this.amendPublishStatusMap.get(version));
        }
        return Optional.empty();
    }

    /**
     * Associate the publish status to a particular bill amendment. The bill amendment
     * instance does not necessarily have to exist prior to updating its publish status.
     *
     * @param version Version - Amendment version
     * @param publishStatus PublishStatus - The publish status of the bill amendment
     */
    public void updatePublishStatus(Version version, PublishStatus publishStatus) {
        if (publishStatus != null) {
            this.amendPublishStatusMap.put(version, publishStatus);
        }
        else {
            throw new IllegalArgumentException("Supplied PublishStatus cannot be null.");
        }
    }

    /**
     * Clears the existing publishStatusMap and then delegates each entry to
     * {@link #updatePublishStatus(Version, PublishStatus)}
     *
     * @param publishStatusMap Map<String, PublishStatus>
     */
    public void setPublishStatuses(Map<Version, PublishStatus> publishStatusMap) {
        this.amendPublishStatusMap.clear();
        if (publishStatusMap != null) {
            publishStatusMap.forEach(this::updatePublishStatus);
        }
        else {
            throw new IllegalArgumentException("Supplied PublishStatusMap cannot be null.");
        }
    }

    /**
     * Indicate whether the bill has a reference to a given amendment version.
     *
     * @param version String - Amendment version
     * @return boolean - true if amendment exists, false otherwise
     */
    public boolean hasAmendment(Version version) {
        return this.amendmentMap.containsKey(version) &&
               this.amendmentMap.get(version) != null;
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
     * Add the bill id to the previous bill versions set.
     */
    public void addPreviousVersion(BillId previousVersion) {
        previousVersions.add(previousVersion);
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

    /** --- Delegates --- */

    public String getFullText() {
        if (this.hasActiveAmendment()) {
            return this.getActiveAmendment().getFullText();
        }
        return "";
    }

    /** --- Basic Getters/Setters --- */

    public BaseBillId getBaseBillId() {
        return baseBillId;
    }

    public void setBaseBillId(BaseBillId baseBillId) {
        this.baseBillId = baseBillId;
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

    public Version getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(Version activeVersion) {
        this.activeVersion = activeVersion;
    }

    public Map<Version, BillAmendment> getAmendmentMap() {
        return amendmentMap;
    }

    public Map<Version, PublishStatus> getAmendPublishStatusMap() {
        return amendPublishStatusMap;
    }

    public Map<VetoId,VetoMessage> getVetoMessages() {
        return vetoMessages;
    }

    public void setVetoMessages(Map<VetoId,VetoMessage> vetoMessages) {
        this.vetoMessages = vetoMessages;
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

    public void setPastCommittees(SortedSet<CommitteeVersionId> pastCommittees) {
        this.pastCommittees = pastCommittees;
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