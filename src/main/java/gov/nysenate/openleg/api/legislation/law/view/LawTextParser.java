package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.legislation.law.LawDocInfo;
import gov.nysenate.openleg.legislation.law.LawDocument;
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
    // Marks where a title starts and ends for bolding.
    public static final String BOLD_MARKER = "~~~~";
    private static final int CHARS_PER_LINE = 82;
    private final List<CharBlockInfo> charBlocks = new ArrayList<>();
    private final LawDocInfo info;
    private int index = 0;
    private boolean bold = false;

    public LawTextParser(LawDocument doc) {
        this.info = doc;
        String text = markForBolding(doc.getText(), doc.getTitle(), doc.getDocType() == CHAPTER);
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
            for (int i = 0; i < charBlocks.size(); i++) {
                if (charBlocks.get(i).isNewline() && prev().isAlphanum() && next().isAlphanum())
                    charBlocks.set(i, new CharBlockInfo(" ", SPACE));
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
            contentStream.setFont(bold ? PDType1Font.COURIER_BOLD :
                    PDType1Font.COURIER, FONT_SIZE);

            if (info.getDocType() == SECTION && charCount == 0 && block.isSpace())
                continue;
            contentStream.drawString(block.text());
            charCount += block.text().length();
        }
        contentStream.moveTextPositionByAmount(0, -FONT_SIZE);
    }

    private String markForBolding(String text, String title, boolean isChapter) {
        text = text.replaceAll("\\\\n", "\n");
        if (isChapter)
            return text;
        Matcher m = Pattern.compile("(?i)" + title + "[.]?").matcher(text);
        if (!m.find())
            return text;
        return BOLD_MARKER + text.substring(0, m.end()) + BOLD_MARKER + text.substring(m.end());
    }

    private CharBlockInfo next() {
        try {
            return charBlocks.get(index+1);
        }
        catch (IndexOutOfBoundsException e) {
            return CharBlockInfo.EMPTY;
        }
    }

    private CharBlockInfo prev() {
        try {
            return charBlocks.get(index-1);
        }
        catch (IndexOutOfBoundsException e) {
            return CharBlockInfo.EMPTY;
        }
    }

    public boolean reachedEnd() {
        return charBlocks.size() <= index;
    }
}
