package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.common.util.NumberUtils;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawType;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.*;
import static gov.nysenate.openleg.api.legislation.law.view.LawPdfView.*;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.CHAPTER;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.SECTION;

/**
 * A parser for a single LawDocument that keeps track of state.
 */
public class LawTextParser {
    private final static int CHARS_PER_LINE = 78;
    private final List<LawCharBlock> charBlocks;
    private final LawDocInfo info;
    private int index = 0;
    private boolean bold = false;

    /**
     * Constructs a parser from a LawDocument, with some pre-processing.
     * @param doc to parse.
     */
    public LawTextParser(LawDocument doc) {
        this.info = doc;
        String text = markForBolding(doc);
        this.charBlocks = LawCharBlock.getBlocksFromText(text);

        // In the original text of sections, newlines are sometimes used as spaces.
        if (info.getDocType() == SECTION) {
            LawCharBlock prev = LawCharBlock.EMPTY;
            for (int i = 0; i < charBlocks.size(); i++) {
                LawCharBlock curr = charBlocks.get(i);
                LawCharBlock next = (i == charBlocks.size()-1 ?
                        LawCharBlock.EMPTY : charBlocks.get(i+1));
                if (prev.type() == ALPHANUM && curr.type() == NEWLINE &&
                        next.type() == ALPHANUM)
                    charBlocks.set(i, new LawCharBlock(" ", SPACE));
                prev = curr;
            }
        }
    }

    /**
     * Writes a single line from the document.
     * @param contentStream to write to.
     * @throws IOException if the writing was interrupted.
     */
    public void writeLine(PDPageContentStream contentStream)
            throws IOException {
        for (int charCount = 0; !finished() && charCount +
                charBlocks.get(index).text().length() <= CHARS_PER_LINE;) {
            LawCharBlock block = charBlocks.get(index++);
            if (block.type() == NEWLINE)
                break;
            else if (block.type() == BOLDMARKER) {
                bold = !bold;
                continue;
            }
            else if (block.type() == SPACE && info.getDocType() == SECTION
                    && charCount == 0)
                continue;

            contentStream.setFont(bold ? PDType1Font.COURIER_BOLD :
                    FONT, FONT_SIZE);
            contentStream.drawString(block.text());
            charCount += block.text().length();
        }
        contentStream.moveTextPositionByAmount(0, -FONT_SIZE*SPACING);
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

    /**
     * A simple method allowing other classes to access parser state.
     * @return if the document has finished processing.
     */
    public boolean finished() {
        return charBlocks.size() <= index;
    }
}
