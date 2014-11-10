package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.processor.base.ParseError;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a bill actions list string into a list of BillAction objects.
 */
public class BillActionParser
{
    private static final Logger logger = LoggerFactory.getLogger(BillActionParser.class);

    /** --- Patterns --- */

    /** Date format found in SobiBlock[4] bill event blocks. e.g. 02/04/13 */
    protected static final DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("MM/dd/yy");

    /** The expected format for actions recorded in the bill events [4] block. e.g. 02/04/13 Event Text Here */
    protected static final Pattern billEventPattern = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{2}) (.*)");

    /** --- Methods --- */

    public static List<BillAction> parseActionsList(BillId billId, String data) throws ParseError {
        List<BillAction> billActions = new ArrayList<>();
        // Impose a strict order to the actions.
        int sequenceNo = 0;
        // Each action should be on its own line
        for (String line : data.split("\n")) {
            Matcher billEvent = billEventPattern.matcher(line);
            if (billEvent.find()) {
                LocalDate eventDate;
                try {
                    eventDate = LocalDate.from(eventDateFormat.parse(billEvent.group(1)));
                }
                // Fail fast otherwise
                catch (DateTimeParseException ex) {
                    throw new ParseError("eventDateFormat parse failure: " + billEvent.group(1));
                }
                String eventText = billEvent.group(2).trim();
                // Each action is designated to a specific chamber. The actions belonging to the assembly will be
                // all lowercase and the senate will be all uppercase.
                Chamber eventChamber = StringUtils.isAllUpperCase(eventText.replaceAll("[^a-zA-Z]+", ""))
                        ? Chamber.SENATE : Chamber.ASSEMBLY;
                // Uppercase the action text to aid with regex matching
                eventText = eventText.toUpperCase();
                // Construct and append bill action to list.
                BillAction action = new BillAction(eventDate, eventText, eventChamber, ++sequenceNo, billId);
                billActions.add(action);
            }
            else {
                throw new ParseError("Bill Action Pattern not matched: " + line);
            }
        }
        return billActions;
    }
}