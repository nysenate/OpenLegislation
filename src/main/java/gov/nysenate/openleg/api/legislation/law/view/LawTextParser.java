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
import static gov.nysenate.openleg.api.legislation.law.view.LawPdfView.FONT_SIZE;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.SECTION;

public class LawTextParser {
    // Marks where a title starts and ends for bolding.
    public static final String BOLD_MARKER = "~~~~";
    private static final int CHARS_PER_LINE = 84;
    private final List<CharBlockInfo> charBlocks = new ArrayList<>();
    private final LawDocInfo info;
    private int index = 0;
    private boolean bold = false;

    public LawTextParser(LawDocument doc) {
        this.info = doc;
        String text = markForBolding(doc.getText(), doc.getTitle());
        Matcher m = LAW_CHAR_BLOCK_PATTERN.matcher(text);
        while (m.find()) {
            Optional<CharBlockType> type = Arrays.stream(CharBlockType.values())
                    .filter(t -> m.group(t.name()) != null).findFirst();
            if (!type.isPresent())
                continue;
            charBlocks.add(new CharBlockInfo(m.group(), type.get()));
        }
        // Some extra lines for spacing.
        charBlocks.add(new CharBlockInfo("\n", CharBlockType.NEWLINE));
        charBlocks.add(new CharBlockInfo("\n", CharBlockType.NEWLINE));
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
            if (block.isBoldMarker()) {
                bold = !bold;
                continue;
            }
            contentStream.setFont(bold ? PDType1Font.COURIER_BOLD :
                    PDType1Font.COURIER, FONT_SIZE);

            // Spaces at the beginning of a section line should be ignored.
//            boolean spacesAtStart =
//            // Sections sometimes use newlines to put space between words.
//            boolean newlineAsSpace = index != 0 && prev().isAlphanum() && next().isAlphanum();
            if (info.getDocType() == SECTION && charCount == 0 && block.isSpace())
                continue;
            contentStream.drawString(block.text());
            charCount += block.text().length();
        }
        contentStream.moveTextPositionByAmount(0, -FONT_SIZE);
    }

    private String markForBolding(String text, String title) {
        text = text.replaceAll("\\\\n", "\n");
        Matcher m = Pattern.compile("(?i)" + title + "[.]?").matcher(text);
        if (!m.find())
            return text;
        return BOLD_MARKER + text.substring(0, m.end()) + BOLD_MARKER + text.substring(m.end());
    }

    private CharBlockInfo next() {
        return charBlocks.get(index+1);
    }

    private CharBlockInfo prev() {
        return charBlocks.get(index-1);
    }

    public boolean reachedEnd() {
        return charBlocks.size() <= index;
    }
}
