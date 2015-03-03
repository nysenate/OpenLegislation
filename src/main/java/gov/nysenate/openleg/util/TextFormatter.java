package gov.nysenate.openleg.util;

import gov.nysenate.openleg.model.Bill;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormatter {

    public static Pattern startPagePattern = Pattern.compile("(^\\s+\\w\\.\\s\\d+(--\\w)?\\s+\\d+(\\s+\\w\\.\\s\\d+(--\\w)?)?$|^\\s+\\d+\\s+\\d+\\-\\d+\\-\\d$|^\\s+\\d{1,4}$)");
    public static Pattern endPagePattern = Pattern.compile("^\\s*(EXPLANATION--Matter|LBD[0-9-]+$)");
    public static Pattern textLinePattern = Pattern.compile("^ {1,5}[0-9]+ ");
    private static Pattern billTextPageStartPattern = Pattern.compile("^(\\s+[A-z]\\.\\s\\d+(--\\w)?)?\\s{10,}\\d+(\\s{10,}(\\w.\\s\\d+(--\\w)?)?(\\d+-\\d+-\\d(--\\w)?)?)?$");

    public static String append(Object... objects) {
        StringBuilder sb = new StringBuilder();
        for(Object o:objects) {
            sb.append(o);
        }
        return sb.toString();
    }

    /**
     * Inserts page breaks into the bill text for printing.
     *
     * @param bill
     * @return
     */
    public static String originalTextPrintable(Bill bill)
    {
        StringBuffer text = new StringBuffer();
        if (bill != null && bill.getFulltext() != null) {
            int linenum = 1;
            for (String line : bill.getFulltext().split("\n")) {
                Matcher startPageMatcher = startPagePattern.matcher(line);
                if(linenum++ > 10 && startPageMatcher.find()) {
                    text.append("<div class=\"hidden\" style=\"page-break-after:always\"></div>");
                }
                text.append(line).append("\n");
            }
        }
        return text.toString();
    }

    public static List<List<String>> pdfPrintablePages(Bill bill) {
        List<List<String>> pages = new ArrayList<List<String>>();

        if (bill != null && bill.getFulltext() != null) {
            pages = generatePages(bill);
        }
        return pages;
    }

    private static List<List<String>> generatePages(Bill bill) {
        List<List<String>> pages = new ArrayList<List<String>>();
        List<String> page = new ArrayList<String>();
        int lineNum = 1;
        String[] lines = bill.getFulltext().split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (isFirstLineOfNextPage(line, lineNum)) {
                pages.add(page);
                page = new ArrayList<String>();
            }
            page.add(line);
            lineNum++;

            if (i == lines.length - 1) {
                pages.add(page);
            }
        }

        return pages;
    }

    private static boolean isFirstLineOfNextPage(String line, int lineNum) {
        Matcher billTextPageMatcher = billTextPageStartPattern.matcher(line);
        return lineNum > 10 && billTextPageMatcher.find(); // Ignore erroneous result in first 10 lines.
    }

    /**
     * Inserts page breaks, <del> and <add> tags, strips line numbers, formats bill headers, ...
     *
     * @param bill
     * @return
     */
    public static String htmlTextPrintable(Bill bill)
    {
        StringBuffer text = new StringBuffer();
        if (bill != null && bill.getFulltext() != null) {
            if (bill.isResolution()) {
                // We don't modify resolutions at all
                return bill.getFulltext();
            }

            String origText = bill.getFulltext();
            origText = origText.replaceAll("I +N +S E N A T E","IN SENATE");
            origText = origText.replaceAll("I +N +A S S E M B L Y","IN ASSEMBLY");
            origText = origText.replaceAll("S T A T E +O F +N E W +Y O R K","STATE OF NEW YORK");

            boolean inText = false;
            boolean inHeader = true;
            boolean inPagebreak = false;
            text.append("<div class='billHeader'>");
            for (String line : origText.split("\n")) {
                Matcher startPageMatcher = startPagePattern.matcher(line);
                Matcher endPageMatcher = endPagePattern.matcher(line);
                Matcher textLineMatcher = textLinePattern.matcher(line);

                if (inHeader) {
                    // Header lines should be trimmed
                    line = line.trim();
                    if (line.matches(".*(Introduced +by|IN +SENATE +--|IN +ASSEMBLY +--).*")) {
                        text.append("</div>");
                        inHeader = false;
                    }
                }
                else if (inText) {
                    if (line.trim().isEmpty()) {
                        // Skip Empty lines in text, all meaningful lines will have #s or text
                        continue;
                    }
                    else if (textLineMatcher.find()) {
                        // Numbered lines mark the end of a page break
                        if (inPagebreak) {
                            inPagebreak = false;
                            text.append("\n</div><hr class='page-break'>");
                        }

                        line = line.replace("[", "<del>[").replace("]", "]</del>");
                    }
                    else if (inPagebreak == false && endPageMatcher.find()) {
                        // Marks the beginning of a page break
                        text.append("<div class=\"hidden\">\n");
                        inPagebreak = true;
                    }
                    else if (startPageMatcher.find()) {
                        // Marks a page break point for printing
                        text.append("<div class=\"hidden\" style=\"page-break-after:always\"></div>");
                        if (inPagebreak == false) {
                            text.append("<div class=\"hidden\">");
                            inPagebreak = true;
                        }
                    }

                    // Trim the line numbers
                    line = line.substring(7);
                }
                else { // Must be in the pre-amble: Introduced By, AN ACT to, THE PEOPLE..
                    // These lines aren't always long enough to trim by 7
                    line = line.substring(Math.min(7, line.length()));
                    if (line.matches(".*BLY, DO ENACT AS FOLLOWS.*")) {
                        // The extra spacing looks good
                        line += "\n";
                        inText = true;
                    }
                }

                text.append(line).append("\n");
            }
        }
        else {
            text.append("Not Available.");
        }
        return text.toString();
    }
}
