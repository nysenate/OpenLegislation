package gov.nysenate.openleg.util;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillTextUtils
{
    protected static Pattern startPagePattern =
        Pattern.compile("(^\\s+\\w\\.\\s\\d+(--\\w)?\\s+\\d+(\\s+\\w\\.\\s\\d+(--\\w)?)?$|^\\s+\\d+\\s+\\d+\\-\\d+\\-\\d$|^\\s+\\d{1,4}$)");

    protected static Pattern endPagePattern = Pattern.compile("^\\s*(EXPLANATION--Matter|LBD[0-9-]+$)");

    protected static Pattern textLinePattern = Pattern.compile("^ {1,5}[0-9]+ ");

    protected static Pattern billTextPageStartPatern =
        Pattern.compile("^(\\s+\\w.\\s\\d+(--\\w)?)?\\s{10,}(\\d+)(\\s{10,}(\\w.\\s\\d+(--\\w)?)?(\\d+-\\d+-\\d(--\\w)?)?)?$");

    /**
     * Extracts a list of numbers which represent the line indices in which a
     * page break occurs. The indices start at 0 since they are extracted
     * from an array of lines.
     *
     * @param fullText String - Bill full text
     * @return List<Integer>
     */
    public static List<Integer> getNewPageLines(String fullText) {
        List<Integer> pageLines = new ArrayList<>();
        String[] lines = fullText.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (isFirstLineOfNextPage(lines[i], i)) {
                pageLines.add(i);
            }
        }
        return pageLines;
    }

    /**
     * Returns the number of pages contained within the supplied bill text. Although
     * we could have just used the {@link #getNewPageLines(String)} method, iterating
     * though the lines in reverse looking for the page number in the pattern is a few
     * times more efficient.
     *
     * @param fullText String - Bill full text
     * @return int
     */
    public static int getPageCount(String fullText) {
        // Short circuit
        if (Strings.isNullOrEmpty(fullText)) return 0;
        // Iterate through the lines in reverse order (until 10 to prevent errors)
        // looking for the last page number (e.g. A. 7461--A           2 ...)
        String[] lines = fullText.split("\n");
        for (int i = lines.length - 1; i > 10; i--) {
            Matcher billTextPageMatcher = billTextPageStartPatern.matcher(lines[i]);
            if (billTextPageMatcher.find()) {
                return Integer.parseInt(billTextPageMatcher.group(3));
            }
        }
        // Since there are no page indicators, just assume its a single page bill
        return 1;
    }

    /**
     * Checks if the given line matches the new page pattern.
     */
    private static boolean isFirstLineOfNextPage(String line, int lineNum) {
        Matcher billTextPageMatcher = billTextPageStartPatern.matcher(line);
        // Ignore erroneous result in first 10 lines.
        return lineNum > 10 && billTextPageMatcher.find();
    }
}
