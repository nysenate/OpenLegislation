package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawDocumentType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Methods that get lines from, and mark bolding for, a document.
 */
public class LawPdfUtil {
    protected static final String BOLD_MARKER = "~~~~";
    private static final String CHAP_NUM = "\\s{2,}CHAPTER [\\w-]+( OF THE CONSOLIDATED LAWS)?";

    private LawPdfUtil() {}

    /**
     * Gets a list of Strings from a LawDocument, assumed to be separated by newlines.
     * Necessary because just passing a newline character to the content stream doesn't work.
     * @param lawDoc to convert.
     * @return the lines, with bold markers added.
     */
    public static List<String> getLines(LawDocument lawDoc) {
        List<String> ret = new ArrayList<>();
        // Dummies have no text.
        if (lawDoc.isDummy())
            return ret;
        String text = markForBolding(lawDoc);
        String[] lines = text.split("\n");
        for (String curr : lines){
            // Adds an empty line before new paragraphs.
            if (lawDoc.getDocType().isSection() && curr.matches("^ {2}\\w.*"))
                ret.add("");
            ret.add(curr);
        }

        // Some extra lines for spacing.
        ret.add("");
        ret.add("");
        return ret;
    }

    /**
     * Adds in special characters to note where bolding should start or stop.
     * @param doc to process.
     * @return a String of the text with the added markers.
     */
    private static String markForBolding(LawDocument doc) {
        // In text, the title may be split by newlines.
        List<String> toMatch = new ArrayList<>();
        if (doc.getDocType() != LawDocumentType.CHAPTER) {
            String titleMatch = ".*?" + doc.getTitle() + "[.]?";
            // Newline characters instead of spaces could split up the Strings we're looking for.
            titleMatch = titleMatch.replaceAll(" ", "[ \n]+");
            toMatch.add(titleMatch);
        }
        else {
            LawChapterCode code = LawChapterCode.valueOf(doc.getLawId());
            String lawName = code.getChapterName() + "( Law)?";
            lawName = lawName.replaceAll("(?i)(and|&)", "(and|&)");
            toMatch.add(lawName.toUpperCase());
            toMatch.add(CHAP_NUM);
        }

        String text = doc.getText().replaceAll("\\\\n", "\n");
        text = text.replaceFirst(".*?\n", "");
        for (String pattern : toMatch) {
            Matcher m = Pattern.compile(pattern).matcher(text);
            while (m.find())
                text = addBoldMarkers(m.start(), m.end(), text);
        }
        return text;
    }

    /**
     * Adds markers around a section of text to indicate it should be bold.
     * @param start of bolding.
     * @param end of bolding.
     * @param input to marked.
     * @return the marked String.
     */
    private static String addBoldMarkers(int start, int end, String input) {
        return input.substring(0, start) + BOLD_MARKER +
                input.substring(start, end) + BOLD_MARKER +
                input.substring(end);
    }

}
