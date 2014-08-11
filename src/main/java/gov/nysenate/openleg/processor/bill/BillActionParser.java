package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.processor.base.ParseError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillActionParser
{
    /** --- Patterns --- */

    /** Date format found in SobiBlock[4] bill event blocks. e.g. 02/04/13 */
    protected static final DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("MM/dd/yy");

    /** The expected format for actions recorded in the bill events [4] block. e.g. 02/04/13 Event Text Here */
    protected static final Pattern billEventPattern = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{2}) (.*)");

    /** Pattern for extracting the committee from matching bill events. */
    public static final Pattern committeeEventTextPattern =
            Pattern.compile("(REFERRED|COMMITTED|RECOMMIT) TO ([A-Z, ]*[A-Z]+)\\s?([0-9]+[A-Z]?)?");

    /** Pattern for detecting calendar events in bill action lists. */
    public static final Pattern floorEventTextPattern = Pattern.compile("(REPORT CAL|THIRD READING|RULES REPORT)");

    /** Pattern to detect a bill being delivered/returned from one chamber to another */
    public static final Pattern chamberSwitchEventTextPattern = Pattern.compile("(DELIVERED|RETURNED) TO (SENATE|ASSEMBLY)");

    /** Pattern for extracting the substituting bill printNo from matching bill events. */
    public static final Pattern substituteEventTextPattern = Pattern.compile("SUBSTITUTED (FOR|BY) (.*)");

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

    /** --- Instance variables --- */

    private Bill baseBill;
    private BillAmendment specifiedAmendment;
    private LocalDateTime publishedDateTime;
    private String data;

    /** --- Constructors --- */

    public BillActionParser(Bill baseBill, BillAmendment specifiedAmendment, LocalDateTime publishedDateTime,
                            String data) {
        this.baseBill = baseBill;
        this.specifiedAmendment = specifiedAmendment;
        this.publishedDateTime = publishedDateTime;
        this.data = data;
    }

    /** --- Methods --- */

    /**
     *
     */
    public List<BillAction> extractActions() throws ParseError {
        List<BillAction> actions = new ArrayList<>();
        BillId billId = this.specifiedAmendment.getBillId();
        Chamber currentChamber = billId.getChamber();
        int sequenceNo = 0;

        for (String line : this.data.split("\n")) {
            Matcher billEvent = billEventPattern.matcher(line);
            if (billEvent.find()) {
                LocalDate eventDate;
                try {
                    eventDate = LocalDate.from(eventDateFormat.parse(billEvent.group(1)));
                }
                catch (DateTimeParseException ex) {
                    throw new ParseError("eventDateFormat parse failure: " + billEvent.group(1));
                }

                // Normalize the bill event text to facilitate pattern matching.
                String eventText = billEvent.group(2).trim().toUpperCase();
                // Construct and append bill action to list.
                BillAction action = new BillAction(eventDate, eventText, currentChamber, ++sequenceNo, billId);
                action.setModifiedDateTime(this.publishedDateTime);
                action.setPublishedDateTime(this.publishedDateTime);
                actions.add(action);
                // Identify any changes to the chamber
                Matcher chamberSwitchEventText = chamberSwitchEventTextPattern.matcher(eventText);
                if (chamberSwitchEventText.find()) {
                    currentChamber = Chamber.valueOf(chamberSwitchEventText.group(2));
                }
            }
            else {
                throw new ParseError("billEventPattern not matched: " + line);
            }
        }
        return actions;
    }
   /*
    private void applyBillAction(String data, List<BillAction> billActions) {
        ArrayList<BillAction> actions = new ArrayList<>();
        Boolean stricken = false;
        BillId sameAsBillId = null;

        CommitteeVersionId currentCommittee = null;
        SortedSet<CommitteeVersionId> pastCommittees = new TreeSet<>();
        BillId billId = specifiedAmendment.getBillId();
        int sequenceNo = 0;

        for (String line : data.split("\n")) {
            Matcher billEvent = billEventPattern.matcher(line);
            if (billEvent.find()) {
                LocalDate eventDate;
                try {
                    eventDate = LocalDate.from(eventDateFormat.parse(billEvent.group(1)));
                }
                catch (DateTimeParseException ex) {
                    throw new ParseError("eventDateFormat parse failure: " + billEvent.group(1));
                }

                String eventText = billEvent.group(2).trim().toUpperCase();
                BillAction action = new BillAction(eventDate, eventText, currentChamber, ++sequenceNo, billId);
                action.setModifiedDateTime(date);
                action.setPublishedDateTime(date);
                actions.add(action);

                Matcher committeeEventText = committeeEventTextPattern.matcher(eventText);
                Matcher substituteEventText = substituteEventTextPattern.matcher(eventText);
                Matcher floorEventText = floorEventTextPattern.matcher(eventText);
                Matcher chamberSwitchEventText = chamberSwitchEventTextPattern.matcher(eventText);

                if (eventText.contains("ENACTING CLAUSE STRICKEN")) {
                    stricken = true;
                }
                else if (committeeEventText.find()) {
                    currentCommittee = new CommitteeVersionId(
                            currentChamber, committeeEventText.group(2), billId.getSession(), eventDate);
                    pastCommittees.add(currentCommittee);
                }
                else if (floorEventText.find()) {
                    currentCommittee = null;
                }
                else if (chamberSwitchEventText.find()) {
                    currentChamber = Chamber.valueOf(chamberSwitchEventText.group(2));
                }
                else if (substituteEventText.find()) {
                    // Note: Does not account for multiple same-as here.
                    sameAsBillId = new BillId(substituteEventText.group(2), baseBill.getSession());
                }
            }
            else {
                throw new ParseError("billEventPattern not matched: " + line);
            }
        }

        baseBill.setActions(actions);
        baseBill.setPastCommittees(pastCommittees);
        baseBill.setModifiedDateTime(date);

        if (sameAsBillId != null) {
            specifiedAmendment.getSameAs().clear();
            specifiedAmendment.getSameAs().add(sameAsBillId);
        }

        specifiedAmendment.setCurrentCommittee(currentCommittee);
        specifiedAmendment.setStricken(stricken);
        specifiedAmendment.setModifiedDateTime(date);
    }      */
}
