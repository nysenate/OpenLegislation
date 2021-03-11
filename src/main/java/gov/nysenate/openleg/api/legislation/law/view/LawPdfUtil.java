package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawDocumentType;
import gov.nysenate.openleg.legislation.law.LawType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Methods that get lines from, and mark bolding for, a document.
 */
public class LawPdfUtil {
    protected static final String BOLD_MARKER = "~~~~";
    private static final String CONSOLIDATED_CHAP_LABEL = "CHAPTER [\\w-]+( OF THE CONSOLIDATED LAWS)?",
    UNCONSOLIDATED_CHAP_LABEL = "Chapter \\d+ of the laws of \\d{4}",
    RULES_CHAP_NAME = "RULES OF THE (SENATE|ASSEMBLY)[\n ]+OF THE STATE OF NEW YORK",
    CONSTITUTION_CHAP_NAME = "THE CONSTITUTION OF THE STATE OF NEW YORK",
    CHAP_NAME = "(The )?%s( Law)?[.]?",
    NON_CHAP_TITLE = ".*?%s[.]?";

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
        String toMatch;
        if (doc.getDocType() == LawDocumentType.CHAPTER)
            toMatch = getChapterPattern(doc.getLawId());
        else {
            String titleMatch = NON_CHAP_TITLE.formatted(doc.getTitle());
            // Newline characters instead of spaces could split up the Strings we're looking for.
            toMatch = titleMatch.replaceAll(" ", "[ \n]+");
            if (doc.getDocType() != LawDocumentType.SECTION)
                toMatch = "(?i)" + toMatch;
        }

        String text = doc.getText().replaceAll("\\\\n", "\n");
        Matcher m = Pattern.compile(toMatch).matcher(text);
        List<Integer> indices = new ArrayList<>();
        while (m.find()) {
            indices.add(m.start());
            indices.add(m.end());
            // Non-chapters should only highlight titles.
            if (doc.getDocType() != LawDocumentType.CHAPTER)
                break;
        }
        return addBoldMarkers(indices, text);
    }

    /**
     * The Chapter patterns are more complicated.
     * @param lawId of the Chapter document.
     * @return the pattern of what should be bolded.
     */
    private static String getChapterPattern(String lawId) {
        LawChapterCode code = LawChapterCode.valueOf(lawId);
        String chapLabel = code.getType() == LawType.UNCONSOLIDATED ? UNCONSOLIDATED_CHAP_LABEL : CONSOLIDATED_CHAP_LABEL;
        var chapterName = "";
        switch (code.getType()) {
            case CONSOLIDATED, UNCONSOLIDATED, COURT_ACTS -> {
                var temp = code.getChapterName().replaceAll("(?i)(and|&)", "(and|&)");
                var formattedName = CHAP_NAME.formatted(temp);
                if (code.getType() == LawType.UNCONSOLIDATED)
                    chapterName = "(?i)" + formattedName.replaceFirst(" \\d.+", "");
                else
                    chapterName = formattedName.toUpperCase();
            }
            case RULES -> chapterName = RULES_CHAP_NAME;
            case MISC -> chapterName = CONSTITUTION_CHAP_NAME;
        }
        return chapLabel + "|" + chapterName;
    }

    /**
     * Adds markers to text to indicate where bolding should start and end.
     * @param indices where markers should be added.
     * @param text to pull from.
     * @return the text, with the markers added.
     */
    private static String addBoldMarkers(List<Integer> indices, String text) {
        StringBuilder ret = new StringBuilder();
        int start = 0;
        for (Integer end : indices) {
            ret.append(text, start, end).append(BOLD_MARKER);
            start = end;
        }
        ret.append(text.substring(start));
        return ret.toString();
    }
}
