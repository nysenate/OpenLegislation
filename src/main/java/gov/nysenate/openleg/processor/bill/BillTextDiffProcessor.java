package gov.nysenate.openleg.processor.bill;

import com.google.common.collect.Range;
import gov.nysenate.openleg.model.bill.BillText;
import gov.nysenate.openleg.model.bill.TextDiff;
import gov.nysenate.openleg.model.bill.TextDiffType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
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

        String rawHtml = cleanupRawHtml(rawHTML);

        // Create TextDiffSearch obj for each type of diff we want to search for.
        TextDiffSearch addedSearch = new TextDiffSearch(
                Pattern.compile("<B><U>([\\S|\\s]*?)</U></B>"),
                TextDiffType.ADDED,
                rawHtml);
        TextDiffSearch removedSearch = new TextDiffSearch(
                Pattern.compile("<B><S>([\\S|\\s]*?)</S></B>"),
                TextDiffType.REMOVED,
                rawHtml);
        TextDiffSearch boldSearch = new TextDiffSearch(
                Pattern.compile("(?<!<FONT SIZE=5>)<B>(?!<U>)(?!<S>)([\\S|\\s]*?)</B>"),
                TextDiffType.BOLD,
                rawHtml);
        TextDiffSearch headerSearch = new TextDiffSearch(
                Pattern.compile("<FONT SIZE=5><B>([\\S|\\s]*?)</B></FONT>"),
                TextDiffType.HEADER,
                rawHtml);
        TextDiffSearch pageBreakSearch = new TextDiffSearch(
                Pattern.compile("<P CLASS=\"brk\">"),
                TextDiffType.PAGE_BREAK,
                rawHtml);

        // Find the first match for all diff types.
        addedSearch.findNext();
        removedSearch.findNext();
        boldSearch.findNext();
        headerSearch.findNext();
        pageBreakSearch.findNext();

        List<TextDiff> textDiffs = createTextDiffs(rawHtml, addedSearch, removedSearch, boldSearch, headerSearch, pageBreakSearch);
        postProcessingCleanup(textDiffs);
        return new BillText(textDiffs);
    }

    private String cleanupRawHtml(String rawHTML) {
        // Remove any empty lines at the end of the text.
        rawHTML = rawHTML.trim();

        // Remove STYLE and BASEFONT element before starting.
        if (rawHTML.contains("<PRE")) {
            rawHTML = rawHTML.substring(rawHTML.indexOf("<PRE"));
        }
        return rawHTML;
    }

    private List<TextDiff> createTextDiffs(String billText, TextDiffSearch addedSearch, TextDiffSearch removedSearch,
                                           TextDiffSearch boldSearch, TextDiffSearch headerSearch, TextDiffSearch pageBreakSearch) {
        ArrayList<TextDiff> textDiffs = new ArrayList<>();
        List<TextDiffSearch> results = Arrays.asList(addedSearch, removedSearch, boldSearch, headerSearch, pageBreakSearch);
        int currentIndex = 0;
        while (currentIndex < billText.length()) {
            // Find which search result is next in the bill text.
            Optional<TextDiffSearch> result = results.stream()
                    .filter(r -> r.getStartingIndex() >= 0)
                    .min(Comparator.comparingInt(TextDiffSearch::getStartingIndex));

            if (!result.isPresent()) {
                // No more diffs found, we are at the end of the text, add the final text and break out of loop.
                String text = billText.substring(currentIndex, billText.length());
                if (text.length() > 0) {
                    textDiffs.add(new TextDiff(TextDiffType.UNCHANGED, text));
                }
                break;
            }

            // Check if added or removed search results are nested in each other.
            // See BillTextDiffProcessorTest for more details and examples.
            TextDiffType resultDiffType = result.get().getDiffType();
            Range<Integer> resultIndexRange = Range.open(result.get().getStartingIndex(), result.get().getEndingIndex());
            if (resultDiffType == TextDiffType.REMOVED) {
                if (resultIndexRange.contains(addedSearch.getStartingIndex())) {
                    // addedSearch result found inside the current removedSearch result.

                    // This nested removedResult is a bug so skip over it. See tests for more info.
                    result.get().findNext();
                    continue;
                }
            }
            else if (resultDiffType == TextDiffType.ADDED) {
                while (resultIndexRange.contains(removedSearch.getStartingIndex())) {
                    // The removedSearch found a match inside this addedSearch.

                    // Ignore this removedSearch result. Its due to a bracket in bill text which is incorrectly
                    // represented as a removal in the XML text. See tests for more info.
                    removedSearch.findNext();
                }
            }

            if (result.get().getStartingIndex() > currentIndex) {
                // Add a diff containing the unchanged text from before the text of this result.
                String text = billText.substring(currentIndex, result.get().getStartingIndex());
                if (text.length() > 0) {
                    textDiffs.add(new TextDiff(TextDiffType.UNCHANGED, text));
                }
            }

            textDiffs.add(result.get().createDiff());
            currentIndex = result.get().getEndingIndex();
            // Find the next match for this result
            result.get().findNext();
        }
        return textDiffs;
    }

    /**
     * Remove remaining erroneous xml elements and empty text diffs.
     */
    private void postProcessingCleanup(List<TextDiff> textDiffs) {
        for (int i = 0; i < textDiffs.size(); ++i) {
            textDiffs.get(i).setText(jsoupParsePreserveNewline(textDiffs.get(i).getText()));
            if (textDiffs.get(i).getText().equals("") && textDiffs.get(i).getCssClasses().size() == 0) {
                textDiffs.remove(i--);
            }
        }
    }

    /**
     * Converts html to plaintext while attempting to preserve all line breaks.
     * This also converts section symbol alt codes into the section symbol.
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
        String text = document.html().replaceAll("\\\\n", "\n");
        // EscapeMode.xhtml only escapes less than, greater than, ampersand, and quote. We minimize this so we have less escaping to remove.
        text = leadingNewlines + Jsoup.clean(text, "", Whitelist.none(),
                new Document.OutputSettings().escapeMode(Entities.EscapeMode.xhtml).prettyPrint(false));
        // Remove the html escaping added by jsoup.
        text = text.replaceAll("&amp;", "&");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        return text;
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
