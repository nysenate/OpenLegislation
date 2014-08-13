package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.processor.base.ParseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Performs parsing of bill action source data and outputs several derived properties that can be
 * applied to the appropriate Bill/BillAmendment reference.
 *
 * Once the parser is constructed via the required input arguments you must run the
 * {@link #parseActions()} method prior to retrieving any of the output values via the getters.
 */
public class BillActionParser
{
    private static final Logger logger = LoggerFactory.getLogger(BillActionParser.class);

    /** --- Patterns --- */

    /** Date format found in SobiBlock[4] bill event blocks. e.g. 02/04/13 */
    protected static final DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("MM/dd/yy");

    /** The expected format for actions recorded in the bill events [4] block. e.g. 02/04/13 Event Text Here */
    protected static final Pattern billEventPattern = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{2}) (.*)");

    /** Pattern for extracting the committee from matching bill events. */
    protected static final Pattern committeeEventTextPattern =
        Pattern.compile("(REFERRED|COMMITTED|RECOMMIT) TO ([A-Z, ]*[A-Z]+)\\s?([0-9]+[A-Z]?)?");

    /** Pattern for detecting calendar events in bill action lists. */
    protected static final Pattern floorEventTextPattern = Pattern.compile("(REPORT CAL|THIRD READING|RULES REPORT)");

    /** Pattern to detect a bill being delivered/returned from one chamber to another */
    protected static final Pattern chamberDeliverPattern = Pattern.compile("(DELIVERED|RETURNED) TO (SENATE|ASSEMBLY)");

    /** Pattern to detect a bill being recalled from one chamber and returned to the other */
    protected static final Pattern chamberRecallPattern = Pattern.compile("RECALLED FROM (SENATE|ASSEMBLY)");

    /** Pattern for extracting the substituting bill printNo from matching bill events. */
    protected static final Pattern substitutionPattern = Pattern.compile("SUBSTITUTED (FOR|BY) (.*)");

    /** Pattern to extract bill number and version when in the format 1234A. */
    protected static final String simpleBillRegex = "([0-9]{2,})([ a-zA-Z]?)";

    /** Patterns for bill actions that indicate that the specified bill amendment should be published. */
    protected static final List<Pattern> publishBillEventPatterns = Arrays.asList(
        Pattern.compile("PRINT NUMBER " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? ON THIRD READING " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? ON THIRD READING \\(T\\) " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? AND RECOMMIT(?:TED) TO RULES " + simpleBillRegex)
    );

    /** Patterns for bill actions that indicate that the specified bill amendment should be unpublished. */
    protected static final List<Pattern> unpublishBillEventPatterns = Arrays.asList(
        Pattern.compile("AMEND(?:ED)? BY RESTORING TO PREVIOUS PRINT " + simpleBillRegex),
        Pattern.compile("AMEND(?:ED)? BY RESTORING TO ORIGINAL PRINT " + simpleBillRegex)
    );

    /** --- Input --- */

    private final BillId billId;
    private final String data;

    /** --- Output --- */

    /** The list of extract BillActions */
    private List<BillAction> billActions;

    /** The last published amendment version found via the billActions list. */
    private Version activeVersion;

    /** PublishStatus associated with each non-base amendment version listed in the actions. */
    private TreeMap<Version, PublishStatus> publishStatusMap = new TreeMap<>();

    /** True if the last action encountered was an enacting clause stricken. */
    private boolean stricken = false;

    /** If the bill is in a committee, this reference will indicate the current committee. */
    private CommitteeVersionId currentCommittee = null;

    /** All prior committees encountered while parsing the actions will be set here. */
    private SortedSet<CommitteeVersionId> pastCommittees = new TreeSet<>();

    /** Same as bill id references associated with any non-base versions. We use a map
     *  here because the substitution actions target a specific amendment version. */
    private TreeMap<Version, BillId> sameAsMap = new TreeMap<>();

    /** --- Constructors --- */

    public BillActionParser(BillId specifiedBillId, String data) {
        this.billId = specifiedBillId;
        this.data = data;
    }

    /** --- Methods --- */

    /**
     * Parses the bill actions source data and extracts the BillActions as well as various other
     * properties that are derived from the actions.
     *
     * @throws ParseError
     */
    public void parseActions() throws ParseError {
        this.billActions = new ArrayList<>();
        // Each action typically originates from a specific chamber
        Chamber currentChamber = billId.getChamber();
        // The current bill amendment can change throughout the course of the actions list.
        activeVersion = BillId.DEFAULT_VERSION;
        // Impose a strict order to the actions.
        int sequenceNo = 0;
        // Each action should be on its own line
        for (String line : this.data.split("\n")) {
            Matcher billEvent = billEventPattern.matcher(line);
            if (billEvent.find()) {
                LocalDate eventDate;
                // Attempt to parse the event date
                try { eventDate = LocalDate.from(eventDateFormat.parse(billEvent.group(1))); }
                // Fail fast otherwise
                catch (DateTimeParseException ex) {
                    throw new ParseError("eventDateFormat parse failure: " + billEvent.group(1));
                }
                // Normalize the bill event text to facilitate pattern matching.
                String eventText = billEvent.group(2).trim().toUpperCase();
                // Construct and append bill action to list.
                BillAction action = new BillAction(eventDate, eventText, currentChamber, ++sequenceNo, billId);
                billActions.add(action);
                // Set stricken status if this action has a stricken clause
                updateStrickenStatus(action);
                // Update the publish status map via content from this action (if applicable)
                updatePublishStatus(action);
                // The latest active amendment version so far
                updateActiveAmendVersion();
                // Update the same as map if a substitution event was detected
                updateSameAs(action);
                // Update the committee info if the action indicates a committee referral
                updateCommitteeStatus(action);
                // Identify the target chamber for the next action
                currentChamber = extractTargerChamber(action).orElse(currentChamber);
            }
            else {
                throw new ParseError("billEventPattern not matched: " + line);
            }
        }
    }

    /**
     * Determines if the chamber is to be switched via a deliver/return action.
     * If the chamber is not switched via this action, an empty Optional will be returned instead.
     *
     * @param action BillAction
     * @return Optional<Chamber>
     */
    protected Optional<Chamber> extractTargerChamber(BillAction action) {
        Chamber currentChamber = null;
        Matcher matcher = chamberDeliverPattern.matcher(action.getText());
        if (matcher.find()) {
            currentChamber = Chamber.valueOf(matcher.group(2));
        }
        return Optional.ofNullable(currentChamber);
    }

    /**
     * The BillActions dictate which non-base versions of an amendment (e.g. 'A','B') should be published.
     * There are certain actions that indicate a version should be published (e.g print number 1234a) and other
     * actions that indicate that the versions should be reverted (e.g. amend by restoring to original print 1234).
     * This method will iterate through the actions list in chronological order and determine the publish status
     * for each non-base amendment and return them in as a map of version -> PublishStatus.
     *
     * @param action BillAction
     */
    protected void updatePublishStatus(BillAction action) {
        boolean foundPublishPattern = false;
        // Check if the action matches a publish event
        for (Pattern pattern : publishBillEventPatterns) {
            Matcher matcher = pattern.matcher(action.getText());
            if (matcher.find()) {
                foundPublishPattern = true;
                Version version = Version.of(matcher.group(2));
                PublishStatus status =
                    new PublishStatus(true, action.getDate().atStartOfDay(), false, action.getText());
                publishStatusMap.put(version, status);
                break;
            }
        }
        // Otherwise check if it's an un-publish event
        if (!foundPublishPattern) {
            for (Pattern pattern : unpublishBillEventPatterns) {
                Matcher matcher = pattern.matcher(action.getText());
                if (matcher.find()) {
                    // The version matched here refers to the latest version that should be published.
                    // In other words, all versions after this one should be marked as unpublished.
                    Version version = Version.of(matcher.group(2));
                    publishStatusMap.forEach((k,v) -> {
                        if (k.compareTo(version) > 0) {
                            publishStatusMap.put(k, new PublishStatus(false, action.getDate().atStartOfDay(),
                                                                      false, action.getText()));
                        }
                    });
                    break;
                }
            }
        }
    }

    /**
     * Sets the active amendment version as the latest version that is published or
     * the base version otherwise.
     */
    protected void updateActiveAmendVersion() {
        this.activeVersion = this.publishStatusMap.descendingKeySet().stream()
                                 .filter(k -> publishStatusMap.get(k).isPublished())
                                 .findFirst().orElse(BillId.DEFAULT_VERSION);
    }

    /**
     * Determine if the given action contains a stricken clause and set the stricken flag
     * accordingly. There are usually no actions following a stricken clause.
     *
     * @param action BillAction
     */
    protected void updateStrickenStatus(BillAction action) {
        this.stricken = action.getText().contains("ENACTING CLAUSE STRICKEN");
    }

    /**
     * The bill actions can contain substitution events which indicate that a same-as bill
     * linkage should be applied. We take care here to make sure we set same as references
     * only for the currently active amendment.
     *
     * @param action BillAction
     */
    protected void updateSameAs(BillAction action) {
        Matcher substituteEventText = substitutionPattern.matcher(action.getText());
        if (substituteEventText.find()) {
            BillId sameAsBillId = new BillId(substituteEventText.group(2), billId.getSession());
            this.sameAsMap.put(this.activeVersion, sameAsBillId);
        }
    }

    /**
     *
     * @param action BillAction
     */
    protected void updateCommitteeStatus(BillAction action) {
        Matcher matcher = committeeEventTextPattern.matcher(action.getText());
        if (matcher.find()) {
            this.currentCommittee = new CommitteeVersionId(action.getChamber(),
               matcher.group(2), this.billId.getSession(), action.getDate());
            pastCommittees.add(this.currentCommittee);
        }
        else {
            // Once reported to the floor, the bill is no longer held in a committee
            matcher = floorEventTextPattern.matcher(action.getText());
            if (matcher.find()) {
                currentCommittee = null;
            }
        }
    }

    /** --- Basic Getters --- */

    public List<BillAction> getBillActions() {
        return billActions;
    }

    public Version getActiveVersion() {
        return activeVersion;
    }

    public TreeMap<Version, PublishStatus> getPublishStatusMap() {
        return publishStatusMap;
    }

    public boolean isStricken() {
        return stricken;
    }

    public Map<Version, BillId> getSameAsMap() {
        return sameAsMap;
    }

    public CommitteeVersionId getCurrentCommittee() {
        return currentCommittee;
    }

    public SortedSet<CommitteeVersionId> getPastCommittees() {
        return pastCommittees;
    }
}