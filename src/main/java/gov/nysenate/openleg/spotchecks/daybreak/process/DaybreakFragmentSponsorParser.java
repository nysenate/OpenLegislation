package gov.nysenate.openleg.spotchecks.daybreak.process;

import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakBill;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DaybreakFragmentSponsorParser {

    /**
     * Pattern for detecting short names that contain the member's initials
     * Examples: P. LOPEZ -> LOPEZ P
     *           M. G. MILLER -> MILLER MG
     */
    private static String shortNameInitialRegex = "((?<firstInitial>[A-Z])\\. )?(?<secondInitial>[A-Z])\\. (?<shortName>[A-Za-z\\-' ]*)";
    /** Pattern for extracting sponsors from Rules sponsors */
    private static Pattern rulesSponsorPattern = Pattern.compile("RULES COM \\(Request of ([A-Za-z\\-\\.', ]*)\\)");

    /**
     * Given a line containing sponsor data, calls the correct parser depending on the bill's chamber
     * @param daybreakBill
     * @param sponsorLine
     */
    public static void parseSponsors(DaybreakBill daybreakBill, String sponsorLine){
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
        List<String> sponsors = parseCSVSponsors(sponsorLine);
        daybreakBill.setSponsor(sponsors.remove(0));
        daybreakBill.setCosponsors(sponsors);
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
     * Also handles M. G. MILLER and M. L. MILLER edge cases.
     * @param rawShortName
     * @return
     */
    private static String formatShortName(String rawShortName){
        return rawShortName.trim().replaceAll(shortNameInitialRegex, "${shortName} ${firstInitial}${secondInitial}");
    }

    /**
     * Parses a comma separated value string, used to extract sponsors from a csv list
     * @param csvString
     * @return
     */
    private static List<String> parseCSVSponsors(String csvString){
        return Arrays.asList(csvString.split(",")).stream()
                .map(DaybreakFragmentSponsorParser::formatShortName)
                .toList();
    }
}
