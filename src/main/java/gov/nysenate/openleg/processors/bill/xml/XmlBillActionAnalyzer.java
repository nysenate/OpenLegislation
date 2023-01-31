package gov.nysenate.openleg.processors.bill.xml;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import gov.nysenate.openleg.legislation.PublishStatus;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.legislation.bill.BillStatusType.*;

/**
 * Performs pattern matching against a list of BillActions to determine various derived properties
 * such as the status of a bill, same as references, etc.
 */
public class XmlBillActionAnalyzer
{
    private static final Logger logger = LoggerFactory.getLogger(XmlBillActionAnalyzer.class);

    /** Pattern for extracting the committee from matching bill events. */
    private static final Pattern committeeEventTextPattern =
        Pattern.compile("(REFERRED|COMMITTED|RECOMMIT) TO (.*)");

    /** Pattern that indicates that the bill has passed a certain house. */
    private static final Pattern passedHousePattern = Pattern.compile("PASSED (ASSEMBLY|SENATE)");

    /** Pattern that indicates that the resolution has been adopted. */
    private static final Pattern adoptedPattern = Pattern.compile("ADOPTED");

    /** Pattern for detecting calendar events in bill action lists. */
    private static final Pattern floorEventPattern = Pattern.compile("(REPORT CAL|THIRD READING|3RD READING|RULES REPORT)");

    /** Pattern for matching the bill calendar number from a floor calendar event. */
    private static final Pattern floorCalPattern = Pattern.compile("CAL\\.(\\d+)");

    /** Pattern that indicates that bill has been delivered to the governor. */
    private static final Pattern deliveredGovPattern = Pattern.compile("DELIVERED TO GOVERNOR");

    /** Pattern for when bill has been signed into law by the governor. */
    private static final Pattern signedPattern = Pattern.compile("SIGNED CHAP\\.(\\d+)");

    /** Pattern for when the bill is vetoed. */
    private static final Pattern vetoedPattern = Pattern.compile("VETO(?:ED)? MEMO");
    private static final Pattern pocketVetoPattern = Pattern.compile("POCKET VETO");
    private static final Pattern pocketApprovalPattern = Pattern.compile("CHAPTER \\d+");

    /** Pattern to detect a bill being delivered/returned from one chamber to another */
    private static final Pattern chamberDeliverPattern = Pattern.compile("(DELIVERED|RETURNED) TO (SENATE|ASSEMBLY)");

    /** Pattern to detect a bill being recalled from one chamber and returned to the other */
    private static final Pattern chamberRecallPattern = Pattern.compile("RECALLED FROM (SENATE|ASSEMBLY)");

    /** Pattern for extracting the substituting bill printNo from matching bill events. */
    private static final Pattern substitutionPattern = Pattern.compile("SUBSTITUTED (FOR|BY) (.*)");

    /** Pattern for removing prior substitution. */
    private static final Pattern subsReconsideredPattern = Pattern.compile("SUBSTITUTION RECONSIDERED");

    /** Pattern to extract bill number and version when in the format 1234A. */
    private static final String simpleBillRegex = "([0-9]{2,})([ a-zA-Z]?)";

    /** Patterns for bill actions that indicate that the specified bill amendment should be published. */
    private static final List<Pattern> publishBillEventPatterns = Arrays.asList(
        Pattern.compile("PRINT NUMBER " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? (?:ON THIRD READING )?(?:\\(T\\) )?" + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? (?:\\(T\\) )?AND RECOMMIT(?:TED)? TO RULES " + simpleBillRegex)
    );

    /** Patterns for bill actions indicating that the specified version should be the new active version. */
    private static final List<Pattern> amendmentRestoreBillEventPatterns = Arrays.asList(
        Pattern.compile("AMEND(?:ED)? BY RESTORING TO PREVIOUS PRINT " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? BY RESTORING TO ORIGINAL PRINT " + simpleBillRegex)
    );

    private static final List<BillStatusType> senateMilestones = Arrays.asList(
        IN_SENATE_COMM, SENATE_FLOOR, PASSED_SENATE, IN_ASSEMBLY_COMM, ASSEMBLY_FLOOR, PASSED_ASSEMBLY,
        DELIVERED_TO_GOV, SIGNED_BY_GOV, POCKET_APPROVAL, VETOED
    );

    private static final List<BillStatusType> assemblyMilestones = Arrays.asList(
        IN_ASSEMBLY_COMM, ASSEMBLY_FLOOR, PASSED_ASSEMBLY, IN_SENATE_COMM, SENATE_FLOOR, PASSED_SENATE,
        DELIVERED_TO_GOV, SIGNED_BY_GOV, POCKET_APPROVAL, VETOED
    );

    /** --- Input --- */

    private final List<BillAction> actions;
    private final BillId billId;

    /** --- Derived properties --- */

    /** The last published amendment version found via the billActions list. */
    private Version activeVersion = BillId.DEFAULT_VERSION;

    /** The milestones indicate key actions that have taken place on the bill. */
    private final LinkedList<BillStatus> statuses = new LinkedList<>();

    /** The bill status should reflect the latest action. */
    private BillStatus billStatus;

    /** Keeps a reference to the bill calendar number for both years of the session for both
     *  chambers. This is because the floor calendar events don't always have the cal no. */
    private final Table<Integer, Chamber, Integer> calNoTable = HashBasedTable.create(2, 2);

    /** PublishStatus associated with each non-base amendment version listed in the actions. */
    private final Map<Version, PublishStatus> publishStatusMap = new EnumMap<>(Version.class);

    /** True if the last action encountered was an enacting clause stricken. */
    private boolean stricken = false;

    /** All prior committees encountered while parsing the actions will be set here. */
    private final SortedSet<CommitteeVersionId> pastCommittees = new TreeSet<>();

    /** Same as bill id references associated with any non-base versions. We use a map
     *  here because the substitution actions target a specific amendment version. */
    private final Map<Version, BillId> sameAsMap = new EnumMap<>(Version.class);

    /** If the bill is substituted by another bill, that bill's bill id will be set here. */
    private BaseBillId substitutedBy = null;

    /** --- Constructors --- */

    /**
     * Construct the BillActionAnalyzer
     *
     * @param actions List<BillAction> - The list of bill actions to analyze
     * @param defaultPubStatus Optional<PublishStatus> - Set the publish map on the action parser to include
     *                                                   existing default amendment publish status if it exists
     */
    public XmlBillActionAnalyzer(BillId billId, List<BillAction> actions, Optional<PublishStatus> defaultPubStatus) {
        this.actions = actions;
        this.billId = billId;
        if (defaultPubStatus.isPresent()) {
            this.publishStatusMap.put(Version.ORIGINAL, defaultPubStatus.get());
            this.billStatus = new BillStatus(INTRODUCED, defaultPubStatus.get().getEffectDateTime().toLocalDate());
        }
    }

    /** --- Methods --- */

    public void analyze() {
        this.actions.forEach(a -> {
            updatePublishStatus(a);
            updateBillStatus(a);
            updateSubstituted(a);
        });
    }


    /**
     * The BillActions dictate which non-base versions of an amendment (e.g. 'A','B') should be published.
     * There are certain actions that indicate a version should be published (e.g print number 1234a) and other
     * actions that indicate that the versions should be reverted (e.g. amend by restoring to original print 1234).
     * This method will iterate through the actions list in chronological order and determine the publish status
     * for each non-base amendment and update the publish status map accordingly.
     *
     * This method will also set the active amendment, which is either the default version or the last published
     * version.
     *
     * @param action BillAction
     */
    protected void updatePublishStatus(BillAction action) {
        boolean foundPublishPattern = false;
        Version publishVersion = this.activeVersion;
        // Check if the action matches a publish event
        for (Pattern pattern : publishBillEventPatterns) {
            Matcher matcher = pattern.matcher(action.getText());
            if (matcher.find()) {
                foundPublishPattern = true;
                // Mark this version as published
                publishVersion = Version.of(matcher.group(2));
                PublishStatus status =
                        new PublishStatus(true, action.getDate().atStartOfDay(), false, action.getText());
                publishStatusMap.put(publishVersion, status);
                // Also make sure that all previous versions are also published
                for (Version v : Version.before(publishVersion)) {
                    if (!publishStatusMap.containsKey(v) || !publishStatusMap.get(v).isPublished()) {
                        publishStatusMap.put(v, status);
                    }
                }
                break;
            }
        }
        // Otherwise check for amendment restore bill event patterns
        if (!foundPublishPattern) {
            for (Pattern pattern : amendmentRestoreBillEventPatterns) {
                Matcher matcher = pattern.matcher(action.getText());
                if (matcher.find()) {
                    // The version matched here refers to the latest version that should be active after the revert.
                    publishVersion = Version.of(matcher.group(2));
                    break;
                }
            }
        }
        this.activeVersion = publishVersion;
        action.setBillId(BaseBillId.of(billId).withVersion(activeVersion));
    }

    /**
     * Generate BillStatus references from the action as well as some other metadata.
     *
     * @param action BillAction
     */
    protected void updateBillStatus(BillAction action) {
        final String text = action.getText();
        BillStatus currStatus = null;
        Matcher committeeMatcher = committeeEventTextPattern.matcher(text);
        Matcher passedHouseMatcher = passedHousePattern.matcher(text);
        if (billId.getBillType().isResolution() && adoptedPattern.matcher(text).find()) {
            currStatus = new BillStatus(ADOPTED, action.getDate());
        }
        else if (committeeMatcher.find()) {
            CommitteeVersionId currentCommittee = new CommitteeVersionId(action.getChamber(),
                committeeMatcher.group(2), action.getBillId().getSession(), action.getDate().atStartOfDay());
            this.pastCommittees.add(currentCommittee);
            currStatus = new BillStatus(
                (action.getChamber().equals(Chamber.SENATE)) ? IN_SENATE_COMM : IN_ASSEMBLY_COMM, action.getDate());
            currStatus.setCommitteeId(currentCommittee);
        }
        else if (floorEventPattern.matcher(text).find()) {
            // Once reported to the floor, the bill is no longer held in a committee
            currStatus = new BillStatus(
                (action.getChamber().equals(Chamber.SENATE)) ? SENATE_FLOOR : ASSEMBLY_FLOOR, action.getDate());
            Matcher calMatcher = floorCalPattern.matcher(text);
            // Set the bill calendar number that's referenced either in this action or a prior floor action
            // within the same year and chamber.
            int year = action.getDate().getYear();
            if (calMatcher.find()) {
                currStatus.setCalendarNo(Integer.parseInt(calMatcher.group(1)));
                calNoTable.put(year, action.getChamber(), currStatus.getCalendarNo());
            }
            else if (calNoTable.contains(year, action.getChamber())) {
                currStatus.setCalendarNo(calNoTable.get(year, action.getChamber()));
            }
        }
        else if (passedHouseMatcher.find()) {
            Chamber chamber = Chamber.getValue(passedHouseMatcher.group(1));
            currStatus = new BillStatus(
                (chamber.equals(Chamber.SENATE)) ? PASSED_SENATE : PASSED_ASSEMBLY, action.getDate());
        }
        else if (deliveredGovPattern.matcher(text).find()) {
            currStatus = new BillStatus(DELIVERED_TO_GOV, action.getDate());
        }
        else if (signedPattern.matcher(text).find()) {
            currStatus = new BillStatus(SIGNED_BY_GOV, action.getDate());
        }
        else if (pocketApprovalPattern.matcher(text).find()) {
            currStatus = new BillStatus(POCKET_APPROVAL, action.getDate());
        }
        else if (vetoedPattern.matcher(text).find() || pocketVetoPattern.matcher(text).find()) {
            // Ignore line item vetoes, since the bill would still have been signed.
            if (!text.contains("LINE")) {
                currStatus = new BillStatus(VETOED, action.getDate());
            }
        }
        else if (text.contains("ENACTING CLAUSE STRICKEN")) {
            currStatus = new BillStatus(STRICKEN, action.getDate());
            this.stricken = true;
        }
        else if (text.trim().equals("LOST")) {
            currStatus = new BillStatus(LOST, action.getDate());
        }

        if (currStatus != null) {
            this.billStatus = currStatus;
            this.billStatus.setActionSequenceNo(action.getSequenceNo());
            this.statuses.add(currStatus);
        }
    }

    /**
     * Often times a bill is substituted for another bill and effectively stops getting updates.
     * This is referenced in the actions list as 'Substituted By {printNo}'.
     *
     * @param action BillAction
     */
    protected void updateSubstituted(BillAction action) {
        Matcher matcher = substitutionPattern.matcher(action.getText());
        if (matcher.find()) {
            this.sameAsMap.put(this.activeVersion, new BillId(matcher.group(2), action.getBillId().getSession()));
            if (matcher.group(1).equals("BY")) {
                substitutedBy = new BaseBillId(matcher.group(2), action.getBillId().getSession());
            }
        }
        else {
            // A substitution reconsidered action will nullify the prior substitution
            matcher = subsReconsideredPattern.matcher(action.getText());
            if (matcher.find()) {
                substitutedBy = null;
            }
        }
    }

    /** --- Functional Getters --- */

    /**
     * Compute the legislative milestones based on the status list.
     * @return List<BillStatus>
     */
    public LinkedList<BillStatus> getMilestones() {
        LinkedList<BillStatus> milestones = new LinkedList<>();
        if (actions.isEmpty()) {
            return milestones;
        }
        List<BillStatusType> milestoneTypes;
        // Resolutions have a different set of milestones.
        if (billId.getBillType().isResolution()) {
            milestoneTypes = Collections.singletonList(ADOPTED);
        }
        // Assembly and senate bills have their milestones ordered accordingly.
        else {
            milestoneTypes = (billId.getChamber().equals(Chamber.SENATE)) ? senateMilestones : assemblyMilestones;
        }
        int lastSequenceNo = 0;
        List<BillStatus> statusList = new ArrayList<>(statuses);
        // Keep track of milestones that didn't match, so they can be back-filled if a later milestone is detected.
        var skippedMilestones = new LinkedHashSet<BillStatusType>();
        // Search through the actions list from most recent to oldest.
        statusList.sort((a, b) -> Integer.compare(b.getActionSequenceNo(), a.getActionSequenceNo()));
        for (BillStatusType milestoneType : milestoneTypes) {
            for (BillStatus status : statusList) {
                if (status.getActionSequenceNo() <= lastSequenceNo) {
                    // Allow for detecting a vetoed status
                    if (milestoneType.equals(SIGNED_BY_GOV)) {
                        break;
                    }
                    skippedMilestones.add(milestoneType);
                }
                else if (status.getStatusType().equals(milestoneType)) {
                    skippedMilestones.forEach(s -> milestones.add(new BillStatus(s, status.getActionDate())));
                    skippedMilestones.clear();
                    milestones.add(status);
                    lastSequenceNo = status.getActionSequenceNo();
                    break;
                }
            }
        }
        return milestones;
    }

    /** --- Basic Getters --- */

    public Version getActiveVersion() {
        return activeVersion;
    }

    public Map<Version, PublishStatus> getPublishStatusMap() {
        return publishStatusMap;
    }

    public BillStatus getBillStatus() {
        return billStatus;
    }

    public List<BillStatus> getStatuses() {
        return statuses;
    }

    public boolean isStricken() {
        return stricken;
    }

    public Map<Version, BillId> getSameAsMap() {
        return sameAsMap;
    }

    public SortedSet<CommitteeVersionId> getPastCommittees() {
        return pastCommittees;
    }

    public BaseBillId getSubstitutedBy() {
        return substitutedBy;
    }
}
