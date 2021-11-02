package gov.nysenate.openleg.legislation.bill;

import gov.nysenate.openleg.legislation.BaseLegislativeContent;
import gov.nysenate.openleg.legislation.PublishStatus;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.legislation.bill.exception.BillAmendNotFoundEx;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import gov.nysenate.openleg.legislation.member.SessionMember;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.legislation.bill.BillTextFormat.PLAIN;

/**
 * The Bill class serves as a container for all the entities that can be classified under a print number
 * and session year. It contains a collection of amendments (including the base amendment) as well as
 * shared information such as the sponsor, actions, etc.
 */
public class Bill extends BaseLegislativeContent implements Serializable, Comparable<Bill>, Cloneable
{
    @Serial
    private static final long serialVersionUID = 2925424993477789289L;

    /** The base bill id which should be used to uniquely identify this instance. */
    protected BaseBillId baseBillId;

    /** Starting with the terms "An act", it's a short description about the topic of the bill. */
    protected String title = "";

    /** An overview of a bill that list's specific sections of NYS law to be amended by the bill. */
    protected String summary = "";

    /** The status of the bill which is derived via the actions list. */
    protected BillStatus status;

    /** Holds the contents of the LDBlurb field */
    protected String ldblurb = "";

    /** A set of statuses that are considered milestones. */
    protected List<BillStatus> milestones = Collections.synchronizedList(new LinkedList<>());

    /** A mapping of amendment versions to BillAmendment instances (includes base amendment). */
    protected Map<Version, BillAmendment> amendmentMap = Collections.synchronizedMap(new EnumMap<>(Version.class));

    /** Publish status mapped by amendment versions. */
    protected Map<Version, PublishStatus> amendPublishStatusMap = Collections.synchronizedMap(new EnumMap<>(Version.class));

    /** A list of veto messages for this bill */
    protected Map<VetoId, VetoMessage> vetoMessages = Collections.synchronizedMap(new HashMap<>());

    /** An approval message for the bill, null if non existent */
    protected ApprovalMessage approvalMessage = null;

    /** Indicates the amendment version that is currently active for this bill. */
    protected Version activeVersion = BillId.DEFAULT_VERSION;

    /** The Legislator who formally introduced the bill. */
    protected BillSponsor sponsor;

    /** A list of co-sponsors that will be given preferential display treatment. */
    protected List<SessionMember> additionalSponsors = Collections.synchronizedList(new ArrayList<>());

    /** A list of committees this bill has been referred to. */
    protected SortedSet<CommitteeVersionId> pastCommittees = Collections.synchronizedSortedSet(new TreeSet<>());

    /** A list of actions that have been made on this bill. */
    protected List<BillAction> actions = Collections.synchronizedList(new ArrayList<>());

    /** If the bill has been substituted by another, store the reference of that bill's id. */
    protected BaseBillId substitutedBy;

    /** If this bill was a reprint of another bill*/
    protected BillId reprintOf;

    /** A list of ids for versions of this legislation in previous sessions.
     *  This set of will contain only previous versions that have been directly linked to this bill*/
    protected BillId directPreviousVersion;

    /** A list of ids for versions of this legislation in previous sessions.
     *  This set will contain all previous versions, even those that are indirectly linked
     *  e.g. the previous version of a previous version*/
    protected Set<BillId> allPreviousVersions =  Collections.synchronizedSortedSet(new TreeSet<>());

    /** Designates the type of program bill, if applicable. */
    protected ProgramInfo programInfo;

    /** Links to committee agendas that involve this bill. */
    protected List<CommitteeAgendaId> committeeAgendas = Collections.synchronizedList(new ArrayList<>());

    /** Associated floor calendar ids. */
    protected List<CalendarId> calendars =  Collections.synchronizedList(new ArrayList<>());

    /** Bills that are passed are assigned a chapter number. */
    protected Integer chapterNum;

    /** Year this bill was signed into law. */
    protected Integer chapterYear;

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

    }

    @Override
    public String toString() {
        return this.getBaseBillId().toString();
    }

    /**
     * Creates a shallow clone for caching purposes. This is not a true clone so references will stay
     * intact except for the amendment list.
     * @return Bill
     * @throws CloneNotSupportedException
     */
    public Bill shallowClone() throws CloneNotSupportedException {
        Bill cloneBill = (Bill) this.clone();
        cloneBill.amendmentMap = new TreeMap<>();
        this.getAmendmentList().forEach(a -> cloneBill.addAmendment(a.shallowClone()));
        return cloneBill;
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Return a bill info object for this bill
     */
    public BillInfo getBillInfo() {
        return new BillInfo(this);
    }

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
     * @return the Chamber of this bill
     */
    public Chamber getChamber() {
        return this.baseBillId.getChamber();
    }

    /**
     * @return the LDBlurb contents of this bill
     */
    public String getLDBlurb() {
        return this.ldblurb;
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
        return Collections.synchronizedList(new ArrayList<>(this.amendmentMap.values()));
    }

    /**
     * @return a set containing the bill ids of this bill's amendments
     */
    public SortedSet<BillId> getAmendmentIds() {
        return Collections.synchronizedSortedSet(this.amendmentMap.values().stream()
                .map(BillAmendment::getBillId)
                .collect(Collectors.toCollection(TreeSet::new)));
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
     * Indicates if the base amendment is published.
     * @return boolean
     */
    public boolean isBaseVersionPublished() {
        return (this.amendPublishStatusMap.containsKey(Version.ORIGINAL) &&
                this.amendPublishStatusMap.get(Version.ORIGINAL).isPublished());
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
     * Sets the active version, creating a new BillAmendment instance if the reference does
     * not exist.
     *
     * @param activeVersion Version
     */
    public void setActiveVersion(Version activeVersion) {
        this.activeVersion = activeVersion;
        if (!this.amendmentMap.containsKey(activeVersion)) {
            this.amendmentMap.put(activeVersion, new BillAmendment(this.baseBillId, activeVersion));
        }
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
    public String getFullTextPlain() {
        if (this.hasActiveAmendment()) {
            return this.getActiveAmendment().getFullText(PLAIN);
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Version getActiveVersion() {
        return activeVersion;
    }

    public Map<Version, BillAmendment> getAmendmentMap() {
        return amendmentMap;
    }

    public Map<Version, PublishStatus> getAmendPublishStatusMap() {
        return amendPublishStatusMap;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public List<BillStatus> getMilestones() {
        return milestones;
    }

    public void setMilestones(LinkedList<BillStatus> milestones) {
        this.milestones = milestones;
    }

    public Map<VetoId,VetoMessage> getVetoMessages() {
        return vetoMessages;
    }

    public void setVetoMessages(Map<VetoId,VetoMessage> vetoMessages) {
        this.vetoMessages = vetoMessages;
    }

    public ApprovalMessage getApprovalMessage() {
        return approvalMessage;
    }

    public void setApprovalMessage(ApprovalMessage approvalMessage) {
        this.approvalMessage = approvalMessage;
    }

    public BillId getDirectPreviousVersion() {
        return directPreviousVersion;
    }

    public void setDirectPreviousVersion(BillId directPreviousVersion) {
        this.directPreviousVersion = directPreviousVersion;
    }

    public Set<BillId> getAllPreviousVersions() {
        return allPreviousVersions;
    }

    public void setAllPreviousVersions(Set<BillId> previousVersions) {
        this.allPreviousVersions = previousVersions;
    }

    public BaseBillId getSubstitutedBy() {
        return substitutedBy;
    }

    public void setSubstitutedBy(BaseBillId substitutedBy) {
        this.substitutedBy = substitutedBy;
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

    public void setLDBlurb(String blurb){this.ldblurb=blurb;}

    public SortedSet<CommitteeVersionId> getPastCommittees() {
        return pastCommittees;
    }

    public void setPastCommittees(SortedSet<CommitteeVersionId> pastCommittees) {
        this.pastCommittees = pastCommittees;
    }

    public List<SessionMember> getAdditionalSponsors() {
        return additionalSponsors;
    }

    public void setAdditionalSponsors(List<SessionMember> additionalSponsors) {
        this.additionalSponsors = additionalSponsors;
    }

    public ProgramInfo getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(ProgramInfo programInfo) {
        this.programInfo = programInfo;
    }

    public List<CommitteeAgendaId> getCommitteeAgendas() {
        return committeeAgendas;
    }

    public void setCommitteeAgendas(List<CommitteeAgendaId> committeeAgendas) {
        this.committeeAgendas = committeeAgendas;
    }

    public List<CalendarId> getCalendars() {
        return calendars;
    }

    public void setCalendars(List<CalendarId> calendars) {
        this.calendars = calendars;
    }

    public Integer getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(Integer chapterNum) {
        this.chapterNum = chapterNum;
    }

    public Integer getChapterYear() {
        return chapterYear;
    }

    public void setChapterYear(Integer chapterYear) {
        this.chapterYear = chapterYear;
    }

    public void setReprintOf(BillId reprintOf) {
        this.reprintOf = reprintOf;
    }

    public BillId getReprintOf() {
        return reprintOf;
    }

    public boolean hasValidLaws(Version version)  {
        // Indicates whether the laws referenced by this bill version exist in our database.
        if (version == null || !hasAmendment(version) || amendPublishStatusMap.get(version) == null)
            return false;
        LocalDateTime publishDate = amendPublishStatusMap.get(version).getEffectDateTime();
        LocalDateTime lawStartDate = Year.of(2014).atDay(1).atStartOfDay();
        return publishDate != null && publishDate.isAfter(lawStartDate);
    }
}
