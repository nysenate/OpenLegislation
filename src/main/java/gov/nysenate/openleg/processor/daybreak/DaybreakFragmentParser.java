package gov.nysenate.openleg.processor.daybreak;

import com.google.common.collect.Maps;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.daybreak.*;
import gov.nysenate.openleg.model.entity.Chamber;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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
        daybreakBill.setLawCodeAndSummary(fragmentParts[4]);
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
        switch(daybreakBill.getBaseBillId().getChamber()){
            case SENATE:
                parseSenateSponsors(daybreakBill, sponsorLine); break;
            case ASSEMBLY:
                parseAssemblySponsors(daybreakBill, sponsorLine); break;
        }
    }

    /**
     * Parses sponsors for senate data, which is divided into primary sponsor and cosponsors
     * @param daybreakBill
     * @param sponsorLine
     */
    private static void parseSenateSponsors(DaybreakBill daybreakBill, String sponsorLine) {
        String[] sponsorsByType = sponsorLine.split("CO:");

        daybreakBill.setSponsor(sponsorsByType[0].trim());
        if(sponsorsByType.length > 1) {
            daybreakBill.setCosponsors(parseCSV(sponsorsByType[1]));
        }
    }

    /**
     * Parses sponsors for assembly data, which consists of a primary sponsor, cosponsors and multisponsors
     * @param daybreakBill
     * @param sponsorLine
     */
    private static void parseAssemblySponsors(DaybreakBill daybreakBill, String sponsorLine){
        sponsorLine = sponsorLine.replaceAll("([A-Z])\\.[¦ ]([A-Z'-]+)", "$2 $1");
        String[] sponsorsByType = sponsorLine.split("; M-S:");

        // Get the primary sponsor and co sponsors as one list
        List<String> sponsors = parseCSV(sponsorsByType[0]);

        daybreakBill.setSponsor(sponsors.remove(0));    // remove the primary sponsor
        daybreakBill.setCosponsors(sponsors);

        if(sponsorsByType.length > 1){
            daybreakBill.setMultiSponsors(parseCSV(sponsorsByType[1]));
        }
    }

    /**
     * Parses a comma separated value string, used to extract sponsors from a csv list
     * @param csvString
     * @return
     */
    private static List<String> parseCSV(String csvString){
        return Arrays.asList(csvString.split(",")).stream()
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * Parses the action lines of a daybreak fragment, yielding a list of BillActions for a daybreak bill
     * @param daybreakBill
     * @param actionLines
     */
    private static void parseActions(DaybreakBill daybreakBill, String[] actionLines){
        List<BillAction> billActions = new ArrayList<>();

        for(int i = 0; i<actionLines.length; i++){
            // Check to see if it is a valid action line
            Matcher billActionMatcher = billActionPattern.matcher(actionLines[i]);
            if(billActionMatcher.matches()) {
                try {
                    // Get BillAction fields from the match
                    LocalDate actionDate = billActionDateFormat.parse(billActionMatcher.group(1))
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    String actionText = billActionMatcher.group(2);
                    Chamber actionChamber = StringUtils.isAllUpperCase(actionText.replaceAll("[^a-zA-Z]+", "")) ?
                                                Chamber.SENATE :
                                                Chamber.ASSEMBLY ;
                    billActions.add(
                            new BillAction(actionDate, actionText, actionChamber, i+1, daybreakBill.getBaseBillId())
                    );
                } catch (ParseException ex) {
                    logger.error("Could not parse date " + billActionMatcher.group(1) + " for " + daybreakBill.getDaybreakBillId());
                }
            }
            else{   // Stop once an invalid line has been reached
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
