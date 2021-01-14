package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.common.util.NumberUtils;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.legislation.law.LawDocumentType.CHAPTER;

/**
 * A parser for a single LawDocument that keeps track of state.
 */
public class LawTextParser {
    /**
     * Adds in markers for what to bold into the String.
     * @param doc to process.
     * @return a String of the text with the added markers.
     */
    public static String markForBolding(LawDocument doc) {
        String text = doc.getText().replaceAll("\\\\n", "\n");
        // In text, the title may be split by newlines.
        List<String> toMatch = new ArrayList<>();
        if (doc.getDocType() != CHAPTER)
            toMatch.add(".*?" + doc.getTitle() + "[.]?");
        else {
            toMatch.add(".*?\n");
            if (LawChapterCode.valueOf(doc.getLawId()).getType() == LawType.CONSOLIDATED
                    && !doc.isDummy()) {
                String[] dashSplit = doc.getDocTypeId().split("-");
                String fixedDocTypeId = doc.getDocTypeId().replaceFirst("\\d+",
                        NumberUtils.allOptions(dashSplit[0]));
                String temp = "Chapter " + fixedDocTypeId + " of the consolidated laws";
                toMatch.add(temp.toUpperCase());
            }
            // Bolds the law name as well.
            String lawName = LawChapterCode.valueOf(doc.getLawId()).getName() + "( Law)?";
            lawName = lawName.replaceAll("(?i)(and|&)", "(and|&)");
            toMatch.add(lawName.toUpperCase());
        }

        for (String pattern : toMatch) {
            pattern = pattern.replaceAll(" ", "[ \n]+");
            // Newline characters could split up the Strings we're looking for.
            Matcher m = Pattern.compile(pattern).matcher(text);
            if (!m.find()) {
                // Tries a case-insensitive match.
                m = Pattern.compile(m.pattern().pattern(), Pattern.CASE_INSENSITIVE).matcher(text);
                if (!m.find())
                    return text;
            }
            text = LawCharBlockType.addBoldMarkers(m.start(), m.end(), text);
        }
        return text;
    }
}
