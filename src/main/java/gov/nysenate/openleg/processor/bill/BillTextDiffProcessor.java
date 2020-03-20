package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.bill.BillText;
import gov.nysenate.openleg.model.bill.TextDiff;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class BillTextDiffProcessor {

    /**
     * Accepts raw HTML string and converts it into a BillText object.
     *
     * @param rawHTML String
     * @return ArrayList&lt;TextDiff&gt;
     */
    public BillText processBillText(String rawHTML) {
        if (rawHTML == null) {
            return new BillText(new ArrayList<>());
        }

        // Remove any empty lines at the end of the text.
        rawHTML = rawHTML.trim();

        ArrayList<TextDiff> textDiffs = new ArrayList<>();
        String billText = rawHTML;

        // Remove STYLE and BASEFONT element before starting.
        if (billText.contains("<PRE")) {
            billText = billText.substring(billText.indexOf("<PRE"));
        }

        // Create TextDiffSearch obj for each type of diff we want to search for.
        TextDiffSearch addedSearch = new TextDiffSearch(
                Pattern.compile("<B><U>([\\S|\\s]*?)</U></B>"),
                1,
                Arrays.asList("ol-changed", "ol-added"),
                billText);
        TextDiffSearch removedSearch = new TextDiffSearch(
                Pattern.compile("<B><S>([\\S|\\s]*?)</S></B>"),
                -1,
                Arrays.asList("ol-changed", "ol-removed"),
                billText);
        TextDiffSearch boldSearch = new TextDiffSearch(
                Pattern.compile("(?<!<FONT SIZE=5>)<B>(?!<U>)(?!<S>)([\\S|\\s]*?)</B>"),
                0,
                Arrays.asList("ol-bold"),
                billText);
        TextDiffSearch headerSearch = new TextDiffSearch(
                Pattern.compile("<FONT SIZE=5><B>([\\S|\\s]*?)</B></FONT>"),
                0,
                Arrays.asList("ol-header"),
                billText);
        TextDiffSearch pageBreakSearch = new TextDiffSearch(
                Pattern.compile("<P CLASS=\"brk\">"),
                0,
                Arrays.asList("ol-page-break"),
                billText);

        // Find the first match for all diff types.
        addedSearch.findNext();
        removedSearch.findNext();
        boldSearch.findNext();
        headerSearch.findNext();
        pageBreakSearch.findNext();

        // Loop through the bill text, creating diffs for all matches and text in between.
        List<TextDiffSearch> results = Arrays.asList(addedSearch, removedSearch, boldSearch, headerSearch, pageBreakSearch);
        int currentIndex = 0;

        while (currentIndex < billText.length()) {
            // Find which search result is next in the bill text.
            Optional<TextDiffSearch> result = results.stream()
                    .filter(r -> r.getStartingIndex() >= 0)
                    .min(Comparator.comparingInt(TextDiffSearch::getStartingIndex));

            if (!result.isPresent()) {
                // No more diffs found, add the final text and break out of loop.
                String text = billText.substring(currentIndex, billText.length());
                if (text.length() > 0) {
                    textDiffs.add(new TextDiff(0, text));
                }
                break;
            }
            if (result.get().getStartingIndex() > currentIndex) {
                // Add a diff containing the unchanged text from before the text of this result.
                String text = billText.substring(currentIndex, result.get().getStartingIndex());
                if (text.length() > 0) {
                    textDiffs.add(new TextDiff(0, text));
                }
            }

            textDiffs.add(result.get().createDiff());
            currentIndex = result.get().getEndingIndex();
            // Find the next match for this result
            result.get().findNext();
        }

        // Remove erroneous xml elements and empty text diffs.
        for (int i = 0; i < textDiffs.size(); ++i) {
            textDiffs.get(i).setRawText(jsoupParsePreserveNewline(textDiffs.get(i).getRawText()));
            if (textDiffs.get(i).getRawText().equals("") && textDiffs.get(i).getCssClasses().size() == 0) {
                textDiffs.remove(i--);
            }
        }

        return new BillText(textDiffs);
    }

    /**
     * Converts html to plaintext while attempting to preserve all line breaks.
     *
     * @param html String
     * @return String
     */
    private static String jsoupParsePreserveNewline(String html) {
        // Remove lines containing only a single space.
        html = html.replaceAll("(?<=\n|^) ?(?=\n)", "");
        final String leadingNewlines = leadingWhitespace(html);
        html = html.substring(leadingNewlines.length());
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
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
}
