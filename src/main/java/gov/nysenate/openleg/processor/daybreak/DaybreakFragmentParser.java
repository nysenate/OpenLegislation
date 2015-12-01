package gov.nysenate.openleg.processor.daybreak;

import com.google.common.collect.Maps;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakBill;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakBillAmendment;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakFragment;
import gov.nysenate.openleg.model.spotcheck.daybreak.PageFileEntry;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.util.DateUtils;
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
import java.util.stream.Collectors;

/**
 * Contains a method that parses a daybreak fragment into a daybreak bill
 */
public class DaybreakFragmentParser {

    private static Logger logger = LoggerFactory.getLogger(DaybreakFragmentParser.class);

    /** Patterns for extracting metadata from bill actions */
    private static Pattern billActionPattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{2}) (.*)");
    private static SimpleDateFormat billActionDateFormat = new SimpleDateFormat("MM/dd/yy");

    /** Pattern for detecting short names that contain the member's first initial */
    private static String shortNameInitialRegex = "([A-Z])\\. ([A-Za-z\\-' ]*)";

    /** Pattern for extracting sponsors from Rules sponsors */
    private static Pattern rulesSponsorPattern = Pattern.compile("RULES COM \\(Request of ([A-Za-z\\-\\.', ]*)\\)");

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
        parseSponsors(daybreakBill, fragmentParts[1]);
        daybreakBill.setTitle(fragmentParts[2]);
        daybreakBill.setLawSection(fragmentParts[3]);
        daybreakBill.setLawCodeAndSummary(fragmentParts[4].replaceAll("BILL SUMMARY NOT FOUND", ""));
        parseActions(daybreakBill, Arrays.copyOfRange(fragmentParts, 5, fragmentParts.length));

        // Convert the page file entries into amendments
        daybreakBill.setAmendments(parsePageFileEntries(daybreakFragment.getPageFileEntries()));

        return daybreakBill;
    }

    /**
     * Given a line containing sponsor data, calls the correct parser depending on the bill's chamber
     * @param daybreakBill
     * @param sponsorLine
     */
    private static void parseSponsors(DaybreakBill daybreakBill, String sponsorLine){
        if(sponsorLine.startsWith("RULES")){
            parseRulesSponsors(daybreakBill, sponsorLine);
        }
        else {
            switch (daybreakBill.getBaseBillId().getChamber()) {
                case SENATE:
                    parseSenateSponsors(daybreakBill, sponsorLine); break;
                case ASSEMBLY:
                    parseAssemblySponsors(daybreakBill, sponsorLine); break;
            }
        }
    }

    /**
     * Parses sponsors for senate data, which is divided into primary sponsor and cosponsors
     * @param daybreakBill
     * @param sponsorLine
     */
    private static void parseSenateSponsors(DaybreakBill daybreakBill, String sponsorLine) {
        String[] sponsorsByType = sponsorLine.split("CO:");

        daybreakBill.setSponsor(formatShortName(sponsorsByType[0]));
        if(sponsorsByType.length > 1) {
            daybreakBill.setCosponsors(parseCSVSponsors(sponsorsByType[1]));
        }
    }

    /**
     * Parses sponsors for assembly data, which consists of a primary sponsor, cosponsors and multisponsors
     * @param daybreakBill
     * @param sponsorLine
     */
    private static void parseAssemblySponsors(DaybreakBill daybreakBill, String sponsorLine){
        String[] sponsorsByType = sponsorLine.split("; M-S:");

        // Get the primary sponsor and co sponsors as one list
        List<String> sponsors = parseCSVSponsors(sponsorsByType[0]);

        daybreakBill.setSponsor(sponsors.remove(0));    // remove the primary sponsor and set it as the daybreak bill sponsor
        daybreakBill.setCosponsors(sponsors);

        if(sponsorsByType.length > 1){
            daybreakBill.setMultiSponsors(parseCSVSponsors(sponsorsByType[1]));
        }
    }

    /**
     * Parses sponsors when the sponsor is a Rules committee
     * @param daybreakBill
     * @param sponsorLine
     */
    private static void parseRulesSponsors(DaybreakBill daybreakBill, String sponsorLine){
        Matcher rulesSponsorMatcher = rulesSponsorPattern.matcher(sponsorLine);
        if(rulesSponsorMatcher.matches()){
            List<String> sponsors = parseCSVSponsors(rulesSponsorMatcher.group(1));
            daybreakBill.setSponsor("RULES (" + sponsors.remove(0) + ")");  //Set the first sponsor as the main sponsor
            daybreakBill.setCosponsors(sponsors);
        }
        else {
            daybreakBill.setSponsor("RULES");
        }
    }

    /**
     * Ensures that certain sponsors' shortnames are stored in the proper format.  Im looking at you P.�Lopez
     * @param rawShortName
     * @return
     */
    private static String formatShortName(String rawShortName){
        return rawShortName.trim().replaceAll(shortNameInitialRegex, "$2 $1");
    }

    /**
     * Parses a comma separated value string, used to extract sponsors from a csv list
     * @param csvString
     * @return
     */
    private static List<String> parseCSVSponsors(String csvString){
        return Arrays.asList(csvString.split(",")).stream()
                .map(DaybreakFragmentParser::formatShortName)
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());
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
