package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.bill.BillText;
import gov.nysenate.openleg.model.bill.TextDiff;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;

public class BillTextDiffProcessor {

    /**
     * Accepts raw HTML string and converts it into a ArrayList&lt;TextDiff&gt;.
     *
     * @param rawHTML String
     * @return ArrayList&lt;TextDiff&gt;
     */
    public BillText processBillText(final String rawHTML) {
        if (rawHTML == null) {
            return new BillText(new ArrayList<>());
        }
        ArrayList<TextDiff> textDiff = new ArrayList<>();
        String unformatted = rawHTML;
        if (unformatted.contains("</STYLE>")) {
            // TODO Implement more accurate way of remove <STYLE> element.
            unformatted = unformatted.substring(unformatted.indexOf("</STYLE>") + 8);
        }
        //unformatted = unformatted.trim();
        int lastLength;
        while (unformatted.length() > 0) {
            lastLength = unformatted.length();
            int[] toAdd = addIndex(unformatted);
            int[] toRemove = removeIndex(unformatted);
            int action = -1;//1 = add, 0 = remove
            if (toAdd[0] == -1 && toRemove[0] == -1) {//no more text added or removed
                textDiff.add(new TextDiff(0, unformatted));
                unformatted = "";
            }
            else {
                if (toAdd[0] != -1 && toRemove[0] != -1) {
                    // If add tag comes before remove tag
                    if (toAdd[0] < toRemove[0]) action = 1;
                    else action = 0;
                }
                // No remove tag present
                else if (toAdd[0] != -1) action = 1;
                    // No tags present
                else action = 0;
            }
            if (action == 1) {
                textDiff.add(new TextDiff(0, unformatted.substring(0, toAdd[0])));
                textDiff.add(new TextDiff(1, unformatted.substring(toAdd[1], toAdd[2])));
                unformatted = unformatted.substring(toAdd[3]);
            }
            else if (action == 0) {
                textDiff.add(new TextDiff(0, unformatted.substring(0, toRemove[0])));
                textDiff.add(new TextDiff(-1, unformatted.substring(toRemove[1], toRemove[2])));
                unformatted = unformatted.substring(toRemove[3]);
            }
            if (lastLength == unformatted.length()) {//unformatted remained the same 2 loop iterations in a row
                textDiff.add(new TextDiff(0, unformatted));
                unformatted = "";
            }
        }
        // Remove erroneous xml elements and empty text diffs.
        for (int i = 0; i < textDiff.size(); ++i) {
            textDiff.get(i).setRawText(jsoupParsePreserveNewline(textDiff.get(i).rawText()));
            if (textDiff.get(i).rawText().equals("")) {
                textDiff.remove(i--);
            }
        }
        return new BillText(textDiff);
    }

    private static final String ADDTAGOPEN = "<B><U>";
    private static final String ADDTAGCLOSE = "</U></B>";

    /**
     * Detects HTML text marked as an addition.
     *
     * @param text HTML String
     * @return int[4], where int[0] is the index before the initial tags,
     * int[1] is the index after the initial tags,
     * int[2] is the index before the final tags,
     * and int[3] is the index after the final tags.
     */
    private static int[] addIndex(final String text) {
        final int startIndex = text.indexOf(ADDTAGOPEN);
        int endIndex = text.indexOf(ADDTAGCLOSE);
        int cpos = 0;
        while (endIndex <= startIndex && endIndex != -1 && startIndex != -1) {
            endIndex = text.indexOf(ADDTAGCLOSE, cpos);
            cpos = endIndex + 8;
        }
        if (startIndex == -1 || endIndex == -1) return new int[]{-1, -1, -1, -1};
        else return new int[]{startIndex, startIndex + ADDTAGOPEN.length(), endIndex, endIndex + ADDTAGCLOSE.length()};
    }


    private static final String REMOVETAGOPEN1 = "<B><S>";
    private static final String REMOVETAGOPEN2 = "[<B><S>";
    private static final String REMOVETAGCLOSE1 = "</S></B>";
    private static final String REMOVETAGCLOSE2 = "</S></B>]";
    /**
     * Detects HTML text marked for removal.
     *
     * @param text HTML String
     * @return int[4], where int[0] is the index before the initial tags,
     * int[1] is the index after the initial tags,
     * int[2] is the index before the final tags,
     * and int[3] is the index after the final tags.
     */
    private static int[] removeIndex(final String text) {
        final int[] start = specialMin(text.indexOf(REMOVETAGOPEN1), text.indexOf(REMOVETAGOPEN2));
        int[] end = specialMin(text.indexOf(REMOVETAGCLOSE1), text.indexOf(REMOVETAGCLOSE2));
        int cpos = 0;
        while (end[0] <= start[0] && end[0] != -1 && start[0] != -1) {
            end = specialMin(text.indexOf(REMOVETAGCLOSE1, cpos), text.indexOf(REMOVETAGCLOSE2, cpos));
            cpos = end[0] + (end[1] == 2 ? REMOVETAGCLOSE2.length() : REMOVETAGCLOSE1.length());
        }
        if (start[0] == -1 || end[0] == -1) return new int[]{-1, -1, -1, -1};
        else return new int[]{start[0],
                start[0] + (start[1] == 2 ? REMOVETAGOPEN2.length() : REMOVETAGOPEN1.length()),
                end[0],
                end[0] + (end[1] == 2 ? REMOVETAGCLOSE2.length() : REMOVETAGCLOSE1.length())};
    }

    /**
     * Converts html to plaintext while attempting to preserve all line breaks.
     *
     * @param html String
     * @return String
     */
    private static String jsoupParsePreserveNewline(String html) {
        final String leadingNewlines = leadingWhitespace(html);
        html = html.substring(leadingNewlines.length());
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return leadingNewlines + Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    /**
     * Returns the leading whitespace characters of a String.
     *
     * @param x String
     * @return String
     */
    private static String leadingWhitespace(final String x) {
        for (int i = 0; i < x.length(); ++i) {
            if (x.charAt(i) != '\n' && x.charAt(i) != ' ') {
                return x.substring(0, i);
            }
        }
        return x;
    }


    /**
     * Returns the minimum of a and b, indicating which of a or b was selected, with a default preference for b.
     * Special case if both a and b are -1.
     * Expects a and b to be >= -1.
     *
     * @param a int
     * @param b int
     * @return int[2], representing [minimum of a and b, which of a and b was selected]
     */
    private static int[] specialMin(final int a, final int b) {
        if (a == -1 && b == -1) return new int[]{-1, 0};
        else if (a == -1) return new int[]{b, 2};
        else if (b == -1) return new int[]{a, 1};
        else if (a < b) return new int[]{a, 1};
        else if (b < a) return new int[]{b, 2};
        else return new int[]{b, 2};
    }
}
