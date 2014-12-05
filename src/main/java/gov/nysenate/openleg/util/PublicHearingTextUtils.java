package gov.nysenate.openleg.util;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;

public class PublicHearingTextUtils
{

    /**
     * Groups public hearing text into pages.
     * @param fullText
     */
    public static List<List<String>> getPages(String fullText) {
        List<List<String>> pages = new ArrayList<>();
        List<String> page = new ArrayList<>();

        fullText = replaceCarriageReturns(fullText);
        List<String> lines = Splitter.on("\n").splitToList(fullText);
        for (String line : lines) {
            page.add(line);
            if (endOfPage(line)) {
                pages.add(page);
                page = new ArrayList<>();
            }
        }
        return pages;
    }

    private static String replaceCarriageReturns(String fullText) {
        return fullText.replaceAll("\r\n", "\n");
    }

    private static boolean endOfPage(String line) {
        // Check for form feed character.
        if (line.contains("\f")) {
            return true;
        }
        return false;
    }

    /**
     * Determines if a public hearing text line contains content.
     * @param line PublicHearing line of text to check for content.
     * @return <code>true</code> if this line contains text excluding the line number.
     * <code>false</code> otherwise.
     */
    public static boolean hasContent(String line) {
        String blankLine = "^\\s*(\\d+)?\\s*$";
        return !line.matches(blankLine);
    }

    /**
     * Returns a public hearing text line with the leading line number and whitespace removed.
     * @param line
     * @return
     */
    public static String stripLineNumber(String line) {
        return line.replaceAll("^\\s*(\\d+)?\\s{2,}(\\w*)", "$2");
    }
}
