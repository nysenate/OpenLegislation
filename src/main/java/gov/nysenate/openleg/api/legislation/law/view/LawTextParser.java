package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.common.util.RomanNumerals;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawType;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.*;
import static gov.nysenate.openleg.api.legislation.law.view.LawPdfView.FONT_SIZE;
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
                    PDType1Font.COURIER, FONT_SIZE);
            contentStream.drawString(block.text());
            charCount += block.text().length();
        }
        contentStream.moveTextPositionByAmount(0, -FONT_SIZE);
    }

    /**
     * Adds in markers for what to bold into the String.
     * @param doc to process.
     * @return a String of the text with the added markers.
     */
    private static String markForBolding(LawDocument doc) {
        String text = doc.getText().replaceAll("\\\\n", "\n");
        // In text, the title may be split by newlines.
        String toMatch = doc.getTitle() + "[.]?";
        if (doc.getDocType() == CHAPTER) {
            if (LawChapterCode.valueOf(doc.getLawId()).getType() != LawType.CONSOLIDATED ||
            doc.isDummy())
                toMatch = ".*?\n";
            else {
                String[] dashSplit = doc.getDocTypeId().split("-");
                String fixedDocTypeId = doc.getDocTypeId().replaceFirst("\\d+",
                        RomanNumerals.allOptions(dashSplit[0]));
                toMatch = "Chapter " + fixedDocTypeId + " of the consolidated laws";
                toMatch = toMatch.toUpperCase();
            }
        }
        // Newline characters could split up the Strings we're looking for.
        Matcher m = Pattern.compile(toMatch.replaceAll(" ", "[ \n]+")).matcher(text);
        if (!m.find()) {
            // Tries a case-insensitive match.
            m = Pattern.compile(m.pattern().pattern(), Pattern.CASE_INSENSITIVE).matcher(text);
            if (!m.find())
                return text;
        }
        return LawCharBlockType.addBoldMarkers(m.start(), m.end(), text);
    }

    /**
     * A simple method allowing other classes to access parser state.
     * @return if the document has finished processing.
     */
    public boolean finished() {
        return charBlocks.size() <= index;
    }
}
