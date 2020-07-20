package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.util.PublicHearingTextUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PublicHearingTitleParser
{
    private static final Pattern TITLE = Pattern.compile(
            ".*(?<prefix>" +
            "((NEW YORK STATE |PUBLIC )?FORUM/TOWN HALL" +
            "|PUBLIC (HEARINGS?|FORUM)" +
            "|(A )?ROUNDTABLE DISCUSSION" +
            "|(A NEW YORK STATE SENATE|JUDICIAL) HEARING" +
            "|TO EXAMINE THE ISSUES FACING COMMUNITIES IN THE WAKE" +
            "|.*NOMINATION(S:)?" +
            "|NEW YORK STATE \\d{4}" +
            "|-{10,})[ :]*)" +
            "(?<title>.+?) " + // Title body
            "(-{10,})"); // Marks the end of title.

    /**
     * Extracts the PublicHearing title from the first page of the PublicHearingFile.
     * @param firstPage of the hearing.
     * @return the title.
     */
    public String parse(List<String> firstPage) {
        String pageText = turnPageIntoString(firstPage);
        Matcher matchTitle = TITLE.matcher(pageText);
        if (!matchTitle.find())
            return null;

        String title = matchTitle.group("title").trim();
        String prefix = matchTitle.group("prefix").trim();
        // Uncomment this if you want prefixes to be excluded.
        //if (title.matches("^(TO|ON|FOR|OF).*") && prefix != null)
            title = prefix + " " + title;
        return title;
    }

    /**
     * Turns a list of String's into a single String with
     * whitespace and line numbers removed.
     * @param firstPage of the hearing.
     * @return a single string of the first page.
     */
    private String turnPageIntoString(List<String> firstPage) {
        StringBuilder pageText = new StringBuilder();
        for (String line : firstPage) {
            if (PublicHearingTextUtils.hasContent(line)) {
                pageText.append(" ").append(PublicHearingTextUtils.stripLineNumber(line));
            }
        }
        return pageText.toString();
    }
}
