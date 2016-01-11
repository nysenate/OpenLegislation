package gov.nysenate.openleg.util;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillTextUtils
{
    protected static Pattern startPagePattern =
        Pattern.compile("(^\\s+\\w\\.\\s\\d+(--\\w)?\\s+\\d+(\\s+\\w\\.\\s\\d+(--\\w)?)?$|^\\s+\\d+\\s+\\d+\\-\\d+\\-\\d$|^\\s+\\d{1,4}$)");

    protected static Pattern endPagePattern = Pattern.compile("^\\s*(EXPLANATION--Matter|LBD[0-9-]+$)");

    protected static Pattern textLinePattern = Pattern.compile("^ {1,5}[0-9]+ ");

    protected static Pattern billTextPageStartPattern =
        Pattern.compile("^(\\s+\\w.\\s\\d+(--\\w)?)?\\s{10,}(\\d+)(\\s{10,}(\\w.\\s\\d+(--\\w)?)?(\\d+-\\d+-\\d(--\\w)?)?)?$");

    protected static Integer MAX_LINES_RES_PAGE = 60;

    /**
     * Extracts a list of numbers which represent the line indices in which a
     * page break occurs. The indices start at 0 since they are extracted
     * from an array of lines.
     *
     * @param fullText String - Bill full text
     * @return List<Integer>
     */
    public static List<Integer> getNewPageLines(String fullText) {
        List<String> lines = Splitter.on("\n").splitToList(fullText);
        return getNewPageLines(lines);
    }

    /**
     * Uses the new page lines to generate a list of pages from the bill text.
     *
     * @param fullText String - String - Bill full text
     * @return List<List<String>>
     */
    public static List<List<String>> getBillPages(String fullText) {
        List<List<String>> pages = new ArrayList<>();
        List<String> lines = Splitter.on("\n").splitToList(fullText);
        int startLine = 0;
        for (int newPageLine : getNewPageLines(lines)) {
            pages.add(lines.subList(startLine, newPageLine));
            startLine = newPageLine;
        }
        pages.add(lines.subList(startLine, lines.size()));
        return pages;
    }

    /**
     * Returns the pages for resolution full text. Since resolutions don't have the same
     * formatting cues as bills, we just cap the pages to a certain number of lines.
     * @param fullText
     * @return
     */
    public static List<List<String>> getResolutionPages(String fullText) {
        List<List<String>> pages = new ArrayList<>();
        List<String> lines = Splitter.on("\n").splitToList(fullText);
        int numPages = new Double(Math.ceil((double) lines.size() / MAX_LINES_RES_PAGE)).intValue();
        for (int page = 0; page < numPages; page++) {
            int pageStart = page * MAX_LINES_RES_PAGE;
            int pageEnd = Math.min(pageStart + MAX_LINES_RES_PAGE, lines.size());
            pages.add(lines.subList(pageStart, pageEnd));
        }
        return pages;
    }

    private static List<Integer> getNewPageLines(List<String> lines) {
        List<Integer> pageLines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (isFirstLineOfNextPage(lines.get(i), i)) {
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
            Matcher billTextPageMatcher = billTextPageStartPattern.matcher(lines[i]);
            if (billTextPageMatcher.find()) {
                return Integer.parseInt(billTextPageMatcher.group(3));
            }
        }
        // Since there are no page indicators, just assume its a single page bill
        return 1;
    }

    /** WIP */
    public static String formatBillText(boolean isResolution, String fullText) {
        if (!isResolution && fullText != null && !fullText.isEmpty()) {
            List<String> lines = Splitter.on("\n").splitToList(fullText);
            StringBuilder formattedFullText = new StringBuilder();
            lines.forEach(line -> {
                if (line.length() > 7) {
                    formattedFullText.append(line.substring(7)).append("\n");
                }
                else {
                    formattedFullText.append(line).append("\n");
                }
            });
            return formattedFullText.toString();
        }
        return fullText;
    }

    /**
     * Checks if the given line matches the new page pattern.
     */
    private static boolean isFirstLineOfNextPage(String line, int lineNum) {
        Matcher billTextPageMatcher = billTextPageStartPattern.matcher(line);
        // Ignore erroneous result in first 10 lines.
        return lineNum > 10 && billTextPageMatcher.find();
    }
}
