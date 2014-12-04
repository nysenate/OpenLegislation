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
            "(?<title>" +
            "((NEW YORK STATE )?FORUM/TOWN HALL" +
            "|PUBLIC (HEARING|FORUM)" +
            "|ROUNDTABLE DISCUSSION" +
            "|A NEW YORK STATE SENATE HEARING" +
            "|NEW YORK STATE \\d{4})" +
            ".+?) " + // Title body
            "*(?=-{10,})"); // Marks the end of title.

    /**
     * Extracts the PublicHearing title from the first page of the PublicHearingFile.
     * @param firstPage
     * @return
     */
    public String parse(List<String> firstPage) {
        String pageText = turnPageIntoString(firstPage);
        Matcher matchTitle = TITLE.matcher(pageText);
        if (!matchTitle.find()) {
            return null;
        }
        return matchTitle.group("title");
    }

    /**
     * Turns a list of String's into a single String with
     * whitespace and line numbers removed.
     * @param firstPage
     * @return
     */
    private String turnPageIntoString(List<String> firstPage) {
        String pageText = "";
        for (String line : firstPage) {
            if (PublicHearingTextUtils.hasContent(line)) {
                pageText += " " + PublicHearingTextUtils.stripLineNumber(line);
            }
        }
        return pageText;
    }
}
