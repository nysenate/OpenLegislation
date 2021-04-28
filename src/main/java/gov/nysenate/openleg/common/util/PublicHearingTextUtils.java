package gov.nysenate.openleg.common.util;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PublicHearingTextUtils {
    private static final Pattern BLANK_LINE = Pattern.compile("^\\s*(\\d+)?\\s*$");

    /**
     * Groups public hearing text into pages.
     * @param fullText
     */
    public static List<List<String>> getPages(String fullText) {
        List<List<String>> pages = new ArrayList<>();
        List<String> page = new ArrayList<>();

        fullText = fullText.replaceAll("\r\n", "\n");
        List<String> lines = Splitter.on("\n").splitToList(fullText);
        boolean lastLineBlank = true;
        for (String line : lines) {
            if (line.contains("\f")) {
                pages.add(page);
                page = new ArrayList<>();
            }
            else if (!line.isBlank() || !lastLineBlank)
                page.add(line);
            lastLineBlank = line.isBlank();
        }
        return pages;
    }

    /**
     * Determines if a public hearing text line contains content.
     * @param line PublicHearing line of text to check for content.
     * @return <code>true</code> if this line contains text excluding the line number.
     * <code>false</code> otherwise.
     */
    public static boolean hasContent(String line) {
        return !BLANK_LINE.matcher(line).matches();
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
