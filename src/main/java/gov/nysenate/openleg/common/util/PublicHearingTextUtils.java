package gov.nysenate.openleg.common.util;

import com.google.common.base.Splitter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PublicHearingTextUtils {
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

    public static String parseTitle(List<String> firstPage) {
        String pageText = firstPage.stream().map(PublicHearingTextUtils::stripLineNumber)
                .collect(Collectors.joining(" "));
        String[] dashSplit = pageText.split("-{10,}");
        if (dashSplit.length < 2)
            return "No title.";
        return dashSplit[dashSplit.length - 2].replaceAll(" {2,}", " ").trim();
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
