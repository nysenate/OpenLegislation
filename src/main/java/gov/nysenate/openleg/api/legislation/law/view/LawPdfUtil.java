package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.common.util.NumberUtils;
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
        if (doc.getDocType() != LawDocumentType.CHAPTER)
            toMatch.add(".*?" + doc.getTitle() + "[.]?");
        else {
            LawChapterCode code = LawChapterCode.valueOf(doc.getLawId());
//            if (code.getType() == LawType.CONSOLIDATED)
//                toMatch.add(getConsolidatedMatch(doc));
            // Bolds the law name as well.
            String lawName = code.getChapterName() + "( Law)?";
            lawName = lawName.replaceAll("(?i)(and|&)", "(and|&)");
            toMatch.add(lawName.toUpperCase());
            toMatch.add("\\s{2,}" + LawDocumentType.CHAPTER.name() + " +\\d+.*\n");
        }

        String text = doc.getText().replaceAll("\\\\n", "\n");
        for (String pattern : toMatch) {
            // Newline characters instead of spaces could split up the Strings we're looking for.
            pattern = pattern.replaceAll(" ", "[ \n]+");
//            pattern = pattern.toUpperCase() + "|" + pattern;
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

    /**
     * Gets a pattern to bold that's specific to consolidated laws.
     * @param doc to pull info from.
     * @return the pattern to match.
     */
    private static String getConsolidatedMatch(LawDocument doc) {
        // Labels may be split by a dash, e.g. 4-D.
        String[] dashSplit = doc.getDocTypeId().split("-");
        String fixedDocTypeId = doc.getDocTypeId().replaceFirst("\\d+",
                NumberUtils.allOptions(dashSplit[0]));
        if (dashSplit.length != 1)
            fixedDocTypeId = fixedDocTypeId + "-" + dashSplit[1];
        return ("Chapter " + fixedDocTypeId + " of the consolidated laws[.]?").toUpperCase();
    }
}
