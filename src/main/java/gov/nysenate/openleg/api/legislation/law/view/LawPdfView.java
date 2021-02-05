package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.legislation.law.LawDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.BOLD_MARKER;
import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.NEWLINE;

/**
 * Converts a LawDocument, and potentially its children in order, into a PDF.
 */
public class LawPdfView extends BasePdfView {
    private static final float SPACING = 1.5f, BOTTOM = 60f, MARGIN = 40f;
    private static final int LINES_PER_PAGE = (int) ((DEFAULT_TOP - BOTTOM)/(FONT_SIZE*SPACING));
    private final Queue<LawCharBlock> charBlocks = new LinkedList<>();
    private boolean bold = false;

    public LawPdfView(Queue<LawDocument> lawDocQueue) throws IOException {
        for (LawDocument doc : lawDocQueue)
            charBlocks.addAll(LawCharBlock.getBlocks(doc));
        while(!charBlocks.isEmpty()) {
            newPage(DEFAULT_TOP, MARGIN);
            for (int currLine = 0; currLine < LINES_PER_PAGE; currLine++)
                writeLine();
            endPage();
        }
        saveDoc();
    }

    /**
     * Writes a single line from the document.
     * @throws IOException if the writing was interrupted.
     */
    private void writeLine() throws IOException {
        while (!charBlocks.isEmpty()) {
            LawCharBlock block = charBlocks.poll();
            if (block.type() == NEWLINE)
                break;
            else if (block.type() == BOLD_MARKER) {
                bold = !bold;
                continue;
            }
            // TODO: speed bolding up by moving inside if?
            //  Would need to change new page logic.
            contentStream.setFont(bold ? PDType1Font.COURIER_BOLD :
                    FONT, FONT_SIZE);
            contentStream.drawString(block.text());
        }
        contentStream.moveTextPositionByAmount(0, -FONT_SIZE*SPACING);
    }
}
