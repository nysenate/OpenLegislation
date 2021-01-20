package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.common.util.NumberUtils;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawDocumentType;
import gov.nysenate.openleg.legislation.law.LawType;
import org.elasticsearch.common.collect.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.*;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.CHAPTER;

/**
 * Data class for information on a block of characters.
 */
public class LawCharBlock {
    public final static LawCharBlock EMPTY = new LawCharBlock("", null);
    private final Tuple<String, LawCharBlockType> info;

    public LawCharBlock(String match, LawCharBlockType type) {
        this.info = new Tuple<>(match, type);
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
    public static List<LawCharBlock> getBlocksFromText(LawDocument lawDoc) {
        List<LawCharBlock> ret = new ArrayList<>();
        String text = markForBolding(lawDoc);
        Matcher m = LAW_CHAR_BLOCK_PATTERN.matcher(text);
        while (m.find()) {
            // Finds the type associated with the matched block of text.
            Optional<LawCharBlockType> type = Arrays.stream(LawCharBlockType.values())
                    .filter(t -> m.group(t.name()) != null).findFirst();
            if (!type.isPresent())
                continue;
            LawCharBlock curr = new LawCharBlock(m.group(), type.get());
            // In sections, newlines for paragraphs are marked by two spaces after
            // a newline at the end of the prior paragraph.
            if (lawDoc.getDocType() == LawDocumentType.SECTION && ret.size() > 1 &&
                    ret.get(ret.size()-2).type() == ALPHANUM &&
                    ret.get(ret.size()-1).type() == NEWLINE && curr.text().equals("  "))
                ret.add(new LawCharBlock("\n", NEWLINE));
            ret.add(curr);
        }

        // Some extra lines for spacing.
        ret.add(new LawCharBlock("\n", LawCharBlockType.NEWLINE));
        ret.add(new LawCharBlock("\n", LawCharBlockType.NEWLINE));
        return ret;
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
