package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.common.util.NumberUtils;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawDocumentType;
import gov.nysenate.openleg.legislation.law.LawType;
import org.elasticsearch.common.collect.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.*;

/**
 * Data class for information on a block of characters.
 */
public class LawCharBlock {
    private final Tuple<String, LawCharBlockType> info;

    public LawCharBlock(String match) {
        this.info = new Tuple<>(match, parseType(match));
    }

    public String text() {
        return info.v1();
    }

    public LawCharBlockType type() {
        return info.v2();
    }

    /**
     * Converts text into the correct LawCharBlocks.
     * @param lawDoc to process.
     * @return the blocks.
     */
    public static List<LawCharBlock> getBlocks(LawDocument lawDoc) {
        List<LawCharBlock> ret = new ArrayList<>();
        // Dummies have no text.
        if (lawDoc.isDummy())
            return ret;
        String text = markForBolding(lawDoc);
        Matcher m = getMatcher(text);
        while (m.find()) {
            LawCharBlock curr = new LawCharBlock(m.group());
            if (isParagraphStart(lawDoc.getDocType().isSection(), ret, curr))
                ret.add(new LawCharBlock("\n"));
            ret.add(curr);
        }
        // Some extra lines for spacing.
        ret.add(new LawCharBlock("\n"));
        ret.add(new LawCharBlock("\n"));
        return ret;
    }

    /**
     * New paragraphs are marked by 2 spaces after a newline ends the last paragraph.
     * @param blockList to pull prior blocks from.
     * @param curr block we're looking at.
     * @return if we're at the start of a new paragraph in a section.
     */
    private static boolean isParagraphStart(boolean isSection, List<LawCharBlock> blockList, LawCharBlock curr) {
        return curr.text().equals("  ") && isSection && blockList.size() > 1 &&
                blockList.get(blockList.size()-1).type() == NEWLINE &&
                blockList.get(blockList.size()-2).type() == ALPHANUM;
    }

    /**
     * Adds in markers for what to bold into the String.
     * @param doc to process.
     * @return a String of the text with the added markers.
     */
    private static String markForBolding(LawDocument doc) {
        String text = doc.getText().replaceAll("\\\\n", "\n");
        // In text, the title may be split by newlines.
        List<String> toMatch = new ArrayList<>();
        if (doc.getDocType() != LawDocumentType.CHAPTER)
            toMatch.add(".*?" + doc.getTitle() + "[.]?");
        else {
            toMatch.add(".*?\n");
            if (LawChapterCode.valueOf(doc.getLawId()).getType() == LawType.CONSOLIDATED)
                toMatch.add(getConsolidatedMatch(doc));
            // Bolds the law name as well.
            String lawName = LawChapterCode.valueOf(doc.getLawId()).getName() + "( Law)?";
            lawName = lawName.replaceAll("(?i)(and|&)", "(and|&)");
            toMatch.add(lawName);
        }

        for (String pattern : toMatch) {
            // Newline characters instead of spaces could split up the Strings we're looking for.
            pattern = pattern.replaceAll(" ", "[ \n]+");
            Matcher m = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text);
            if (!m.find())
                continue;
            text = addBoldMarkers(m.start(), m.end(), text);
        }
        return text;
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
        return "Chapter " + fixedDocTypeId + " of the consolidated laws[.]?";
    }
}
