package gov.nysenate.openleg.common.util;

import com.google.common.base.Splitter;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.processors.transcripts.hearing.PublicHearingCommitteeParser;
import gov.nysenate.openleg.processors.transcripts.hearing.PublicHearingDateParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PublicHearingTextUtils {
    public static PublicHearing getHearingFromText(PublicHearingId id, String fullText) {
        List<List<String>> pages = getPages(fullText);
        List<String> firstPage = pages.get(0);
        boolean isWrongFormat = pages.get(1).stream().anyMatch(str -> str.contains("Geneva Worldwide, Inc."));
        String pageText = firstPage.stream().map(PublicHearingTextUtils::stripLineNumber)
                .collect(Collectors.joining("\n")).split("PRESIDING|PRESENT|SPONSORS")[0];
        // Split on being before PRESENT or PRESIDING instead? Then split on other divisions?
        String splitPattern = isWrongFormat ? "\n{5,}" : "-{10,}";
        List<String> dashSplit = Arrays.stream(pageText.split(splitPattern, 3)).map(String::trim)
                .filter(str -> !str.isEmpty()).collect(Collectors.toList());

        String[] addrDateTime = getAddrDateTime(dashSplit.get(dashSplit.size() - 1), isWrongFormat);
        boolean hasAddress = addrDateTime.length > 1;
        var dateTimeParser = new PublicHearingDateParser(addrDateTime[hasAddress ? 1 : 0], pages.get(pages.size() - 1));
        var hearing = new PublicHearing(id, dateTimeParser.getDate(), fullText);
        hearing.setStartTime(dateTimeParser.getStartTime());
        hearing.setEndTime(dateTimeParser.getEndTime());
        hearing.setCommittees(PublicHearingCommitteeParser.parse(dashSplit.get(0)));
        hearing.setAddress(hasAddress ? addrDateTime[0] : "No address");
        String title = dashSplit.size() < 2 ? "No title" : dashSplit.get(dashSplit.size() - 2);
        hearing.setTitle(title.replaceAll("\\s+", " ").trim());
        return hearing;
    }

    private static String[] getAddrDateTime(String toSplit, boolean isWrongFormat) {
        if (isWrongFormat) {
            String reversed = new StringBuilder(toSplit).reverse().toString();
            reversed = reversed.replaceFirst("\n", "");
            toSplit = new StringBuilder(reversed).reverse().toString();
        }
        return toSplit.split("\n{2,}");
    }

    /**
     * Groups public hearing text into pages, which are lists of lines..
     * @param fullText of the hearing.
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
     */
    public static String stripLineNumber(String line) {
        return line.replaceFirst("^\\s*\\d{0,2}(\\s+|$)", "");
    }
}
