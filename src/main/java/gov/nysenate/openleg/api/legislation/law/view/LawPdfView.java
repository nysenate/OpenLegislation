package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.legislation.law.LawDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.BOLDMARKER;
import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.NEWLINE;

/**
 * Converts a LawDocument, and potentially its children in order, into a PDF.
 */
public class LawPdfView extends BasePdfView {
    private static final float SPACING = 1.5f, BOTTOM = 60f, MARGIN = 30f;
    private static final int LINES_PER_PAGE = (int) ((TOP - BOTTOM)/(FONT_SIZE*SPACING));
    private List<LawCharBlock> charBlocks;
    private int index = 0;
    private boolean bold = false;

    public LawPdfView(Queue<LawDocument> lawDocQueue) throws IOException {
        charBlocks = LawCharBlock.getBlocksFromText(lawDocQueue.remove());
        while(!lawDocQueue.isEmpty() || index < charBlocks.size()) {
            newPage(TOP, MARGIN, true);
            for (int currLine = 0; currLine < LINES_PER_PAGE; currLine++) {
                writeLine();
                if (index >= charBlocks.size()) {
                    if (lawDocQueue.isEmpty())
                        break;
                    index = 0;
                    charBlocks = LawCharBlock.getBlocksFromText(lawDocQueue.remove());
                }
            }
            endPage();
        }
        saveDoc();
    }

    /**
     * Writes a single line from the document.
     * @throws IOException if the writing was interrupted.
     */
    private void writeLine() throws IOException {
        while (index < charBlocks.size()) {
            LawCharBlock block = charBlocks.get(index++);
            if (block.type() == NEWLINE)
                break;
            else if (block.type() == BOLDMARKER) {
                bold = !bold;
                continue;
            }
            contentStream.setFont(bold ? PDType1Font.COURIER_BOLD :
                    FONT, FONT_SIZE);
            contentStream.drawString(block.text());
        }
        contentStream.moveTextPositionByAmount(0, -FONT_SIZE*SPACING);
    }
}
