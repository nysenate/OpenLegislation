package gov.nysenate.openleg.spotchecks.daybreak.process;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.bill.BillAction;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.spotchecks.daybreak.DaybreakFragment;
import gov.nysenate.openleg.spotchecks.daybreak.PageFileEntry;
import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakBill;
import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakBillAmendment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains a method that parses a daybreak fragment into a daybreak bill
 */
public class DaybreakFragmentParser {

    private static Logger logger = LoggerFactory.getLogger(DaybreakFragmentParser.class);

    /** Patterns for extracting metadata from bill actions */
    private static Pattern billActionPattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{2}) (.*)");
    private static SimpleDateFormat billActionDateFormat = new SimpleDateFormat("MM/dd/yy");

    /**
     * Parses the text of a daybreak fragment into the fields of a daybreak bill
     * @param daybreakFragment
     * @return
     */
    public static DaybreakBill extractDaybreakBill(DaybreakFragment daybreakFragment){
        // Set active billid from the daybreak fragment
        DaybreakBill daybreakBill = new DaybreakBill(daybreakFragment.getDaybreakBillId());
        daybreakBill.setActiveVersion(daybreakFragment.getBillId().getVersion());

        // Split the fragment text into lines
        String[] fragmentParts = daybreakFragment.getDaybreakText().split("\\n");

        // Parse each fragment line accordingly
        DaybreakFragmentSponsorParser.parseSponsors(daybreakBill, fragmentParts[1]);
        daybreakBill.setTitle(fragmentParts[2]);
        daybreakBill.setLawSection(fragmentParts[3]);
        daybreakBill.setLawCodeAndSummary(fragmentParts[4].replaceAll("BILL SUMMARY NOT FOUND", ""));
        parseActions(daybreakBill, Arrays.copyOfRange(fragmentParts, 5, fragmentParts.length));

        // Convert the page file entries into amendments
        if (daybreakFragment.getPageFileEntries() == null) {
            // If bill is not in page file. Set it to an empty map.
            daybreakBill.setAmendments(ImmutableMap.of());
        } else {
            // Otherwise include all amendments in the page file.
            daybreakBill.setAmendments(parsePageFileEntries(daybreakFragment.getPageFileEntries()));
        }

        return daybreakBill;
    }

    /**
     * Parses the action lines of a daybreak fragment, yielding a list of BillActions for a daybreak bill
     * @param daybreakBill
     * @param actionLines
     */
    private static void parseActions(DaybreakBill daybreakBill, String[] actionLines){
        List<BillAction> billActions = new ArrayList<>();
        int sequenceNo = 0; // Sequence no for the action
        for(int i = 0; i<actionLines.length; i++){
            // Check to see if it is a valid action line
            Matcher billActionMatcher = billActionPattern.matcher(actionLines[i]);
            if(billActionMatcher.matches()) {
                try {
                    // Get BillAction fields from the match
                    LocalDate actionDate = LocalDate.from(DateUtils.LRS_ACTIONS_DATE.parse(billActionMatcher.group(1)));
                    String actionText = billActionMatcher.group(2);
                    Chamber actionChamber = StringUtils.isAllUpperCase(actionText.replaceAll("[^a-zA-Z]+", "")) ?
                                                Chamber.SENATE : Chamber.ASSEMBLY ;
                    billActions.add(
                            new BillAction(actionDate, actionText, actionChamber, ++sequenceNo, daybreakBill.getBaseBillId())
                    );
                } catch (DateTimeParseException ex) {
                    logger.error("Could not parse date " + billActionMatcher.group(1) + " for " + daybreakBill.getDaybreakBillId());
                    logger.error(ex.getMessage());
                }
            }
            else if (billActions.size() > 0) {  // If an invalid line is detected after actions have been read, stop
                break;
            }
        }
        daybreakBill.setActions(billActions);
    }

    /**
     * Converts the map of page file entries found in a daybreak fragment
     * into a map of versions to daybreak bill amendments for use in a daybreak bill
     * @param pageFileEntries
     * @return
     */
    private static Map<Version, DaybreakBillAmendment> parsePageFileEntries(Map<BillId, PageFileEntry> pageFileEntries){
        List<DaybreakBillAmendment> amendments = pageFileEntries.entrySet().stream()
                .map(entry -> parsePageFileEntry(entry.getKey(), entry.getValue()))
                .toList();
        return Maps.uniqueIndex(amendments, amend -> amend.getBillId().getVersion());
    }

    /**
     * Creates a DaybreakBillAmendment from the fields of a PageFileEntry
     * @param billId
     * @param pageFileEntry
     * @return
     */
    private static DaybreakBillAmendment parsePageFileEntry(BillId billId, PageFileEntry pageFileEntry){
        return new DaybreakBillAmendment(
                billId,
                billId.getChamber()== Chamber.SENATE ?
                        pageFileEntry.getAssemblyBillId() :
                        pageFileEntry.getSenateBillId(),
                pageFileEntry.getPageCount(),
                pageFileEntry.getPublishedDate()
        );
    }
}
