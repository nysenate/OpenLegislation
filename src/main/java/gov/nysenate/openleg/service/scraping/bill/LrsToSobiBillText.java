package gov.nysenate.openleg.service.scraping.bill;

import gov.nysenate.openleg.model.entity.Chamber;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This performs some modifications to the LRS scraped bill text to account for
 * differences in bill text between the LRS website and SOBI data.
 */
@Component
public class LrsToSobiBillText {

    private static final Pattern resolutionStartPattern = Pattern.compile("^\\s+([A-z]{2,})");

    /**
     * Converts the HTML version of bill text into the sobi version.
     * If text is for a resolution, use {@link #resolutionText}
     * @param text
     * @return
     */
    public String billText(String text) {
        text = text.replaceAll("[\r\\uFEFF-\\uFFFF]|(?<=\n) ", "");
        text = text.replaceAll("§", "S");
        text = text.replaceFirst("^\n\n[ ]{12}STATE OF NEW YORK(?=\n)",
                "\n                           S T A T E   O F   N E W   Y O R K");
        text = text.replaceFirst("(?<=\\n)[ ]{16}IN SENATE(?=\\n)",
                "                                   I N  S E N A T E");
        text = text.replaceFirst("(?<=\\n)[ ]{15}IN ASSEMBLY(?=\\n)",
                "                                 I N  A S S E M B L Y");
        text = text.replaceFirst("(?<=\\n)[ ]{12}SENATE - ASSEMBLY(?=\\n)",
                "                             S E N A T E - A S S E M B L Y");
        return text;
    }

    /**
     * Converts the HTML version of resolution text into the SOBI version.
     * @param text
     * @param chamber
     * @return
     */
    public String resolutionText(String text, Chamber chamber) {
        text = text.replaceAll("[\r\\uFEFF-\\uFFFF]|(?<=\n) ", ""); // replace "\r" with "\n"
        text = text.replaceAll("§", "S");
        text = text.replaceFirst("^\n\n[\\w \\.-]+\n\n[\\w '\\.\\-:]+\n", "");
        text = text.replaceFirst("^\\s+PROVIDING", String.format("\n%s RESOLUTION providing", chamber));
        Matcher resoStartMatcher = resolutionStartPattern.matcher(text);
        if (resoStartMatcher.find()) {
            text = text.replaceFirst(resolutionStartPattern.pattern(),
                    "\nLEGISLATIVE RESOLUTION " + resoStartMatcher.group(1).toLowerCase());
        }
        return text;
    }
}
