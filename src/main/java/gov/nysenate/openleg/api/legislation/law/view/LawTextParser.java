package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.common.util.RomanNumerals;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawType;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.api.legislation.law.view.CharBlockType.LAW_CHAR_BLOCK_PATTERN;
import static gov.nysenate.openleg.api.legislation.law.view.CharBlockType.SPACE;
import static gov.nysenate.openleg.api.legislation.law.view.LawPdfView.FONT_SIZE;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.CHAPTER;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.SECTION;

public class LawTextParser {
    private static final int CHARS_PER_LINE = 82;
    private final List<CharBlockInfo> charBlocks = new ArrayList<>();
    private final LawDocInfo info;
    private int index = 0;
    private boolean bold = false;

    public LawTextParser(LawDocument doc) {
        this.info = doc;
        String text = markForBolding(doc);
        Matcher m = LAW_CHAR_BLOCK_PATTERN.matcher(text);
        while (m.find()) {
            // Finds the type associated with the matched block of text.
            Optional<CharBlockType> type = Arrays.stream(CharBlockType.values())
                    .filter(t -> m.group(t.name()) != null).findFirst();
            if (!type.isPresent())
                continue;
            charBlocks.add(new CharBlockInfo(m.group(), type.get()));
        }
        // Some extra lines for spacing.
        charBlocks.add(new CharBlockInfo("\n", CharBlockType.NEWLINE));
        charBlocks.add(new CharBlockInfo("\n", CharBlockType.NEWLINE));

        // In sections, newlines are sometimes used as spaces.
        if (info.getDocType() == SECTION) {
            CharBlockInfo prev = CharBlockInfo.EMPTY;
            for (int i = 0; i < charBlocks.size(); i++) {
                CharBlockInfo curr = charBlocks.get(i);
                CharBlockInfo next = (i == charBlocks.size()-1 ?
                        CharBlockInfo.EMPTY : charBlocks.get(i+1));
                if (curr.isNewline() && prev.isAlphanum() && next.isAlphanum())
                    charBlocks.set(i, new CharBlockInfo(" ", SPACE));
                prev = curr;
            }
        }
    }

    /**
     * Writes a single line from the document.
     * @param contentStream to write to.
     * @throws IOException if the writing was interrupted.
     */
    public void writeLine(PDPageContentStream contentStream) throws IOException {
        for (int charCount = 0; !reachedEnd() && charCount +
                charBlocks.get(index).text().length() <= CHARS_PER_LINE;) {
            CharBlockInfo block = charBlocks.get(index++);
            if (block.isNewline())
                break;
            else if (block.isBoldMarker()) {
                bold = !bold;
                continue;
            }
            else if (block.isSpace() && info.getDocType() == SECTION && charCount == 0)
                continue;

            contentStream.setFont(bold ? PDType1Font.COURIER_BOLD :
                    PDType1Font.COURIER, FONT_SIZE);
            contentStream.drawString(block.text());
            charCount += block.text().length();
        }
        contentStream.moveTextPositionByAmount(0, -FONT_SIZE);
    }

    private String markForBolding(LawDocument doc) {
        String text = doc.getText().replaceAll("\\\\n", "\n");
        // In text, the title may be split by newlines.
        String toMatch = (info.getDocType() == CHAPTER ? ".*?\n" : "(?i)" + doc.getTitle() + "[.]?");
        if (doc.getDocType() == CHAPTER) {
            if (LawChapterCode.valueOf(doc.getLawId()).getType() != LawType.CONSOLIDATED)
                toMatch = ".*?\n";
            else {
                String[] dashSplit = doc.getDocTypeId().split("-");
                String fixedDocTypeId = doc.getDocTypeId().replaceFirst("\\d+",
                        RomanNumerals.allOptions(dashSplit[0]));
                toMatch = "(?i)Chapter " + fixedDocTypeId + " of the consolidated laws";
            }
        }
        Matcher m = Pattern.compile(toMatch).matcher(text);
        if (!m.find())
            return text;
        return CharBlockType.addBoldMarkers(text.substring(0, m.end())) + text.substring(m.end());
    }

    public boolean reachedEnd() {
        return charBlocks.size() <= index;
    }
}
