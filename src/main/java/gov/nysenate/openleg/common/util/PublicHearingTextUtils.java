package gov.nysenate.openleg.common.util;

import com.google.common.base.Splitter;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.processors.transcripts.hearing.PublicHearingAddressParser;
import gov.nysenate.openleg.processors.transcripts.hearing.PublicHearingCommitteeParser;
import gov.nysenate.openleg.processors.transcripts.hearing.PublicHearingDateParser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PublicHearingTextUtils {
    public static PublicHearing getHearingFromText(PublicHearingId id, String fullText) {
        List<List<String>> pages = getPages(fullText);
        List<String> firstPage = pages.get(0);
        String pageText = firstPage.stream().map(PublicHearingTextUtils::stripLineNumber)
                .filter(str -> !str.isEmpty()).collect(Collectors.joining(" "));
        // Split on being before PRESENT or PRESIDING instead? Then split on other divisions?
        String[] dashSplit = pageText.split("-{10,}");

        String dateTimePart = dashSplit.length == 1 ? dashSplit[0] : dashSplit[dashSplit.length - 1];
        var dateTimeParser = new PublicHearingDateParser(dateTimePart, pages.get(pages.size() - 1));
        var hearing = new PublicHearing(id, dateTimeParser.getDate(), fullText);
        hearing.setStartTime(dateTimeParser.getStartTime());
        hearing.setEndTime(dateTimeParser.getEndTime());
        hearing.setCommittees(PublicHearingCommitteeParser.parse(dashSplit[0]));
        hearing.setAddress(PublicHearingAddressParser.parse(firstPage));
        hearing.setTitle(dashSplit.length < 2 ? "No title" :
                dashSplit[dashSplit.length - 2].replaceAll(" {2,}", " ").trim());
        return hearing;
    }

    /**
     * Groups public hearing text into pages.
     * @param fullText
     */
    public static List<List<String>> getPages(String fullText) {
        fullText = fullText.replaceAll("\r\n", "\n");
        return Splitter.on("\f").splitToList(fullText).stream().map(PublicHearingTextUtils::getLines)
                .filter(page -> !page.isEmpty()).collect(Collectors.toList());
    }

    private static List<String> getLines(String page) {
        List<String> ret = Splitter.on("\n").trimResults().splitToList(page)
                .stream().dropWhile(String::isEmpty).collect(Collectors.toList());
        // Drops empty Strings from the end of the list as well.
        Collections.reverse(ret);
        ret = ret.stream().dropWhile(String::isEmpty).collect(Collectors.toList());
        Collections.reverse(ret);
        return ret;
    }

    /**
     * Determines if a public hearing text line contains content.
     * @param line PublicHearing line of text to check for content.
     * @return <code>true</code> if this line contains text excluding the line number.
     * <code>false</code> otherwise.
     */
    public static boolean hasContent(String line) {
        return !stripLineNumber(line).isEmpty();
    }

    /**
     * Returns a public hearing text line with the leading line number and whitespace removed.
     * @param line
     * @return
     */
    public static String stripLineNumber(String line) {
        return line.replaceFirst("^\\s*\\d{0,2}(\\s+|$)", "");
    }
}
