package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.common.util.TranscriptTextUtils;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.processors.transcripts.session.TranscriptLine;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pdf representation of a transcript designed to match the formatting
 * of the official transcripts.
 */
public class TranscriptPdfView extends BasePdfView {
    private static final float top = 710f, bot = 90f, left = 105f, right = 575f, FONT_WIDTH = 7f;
    private static final float[] X_VALS = {left, left, right, right}, Y_VALS = {top, bot, bot, top};
    private static final int NO_LINE_NUM_INDENT = 11, STENOGRAPHER_LINE_NUM = 26;

    public TranscriptPdfView(Transcript transcript) throws IOException {
        if (transcript == null)
            throw new IllegalArgumentException("Supplied transcript cannot be null when converting to pdf.");

        List<List<String>> pages = TranscriptTextUtils.getPdfFormattedPages(transcript.getText());
        for (List<String> page : pages) {
            newPage(top - FONT_WIDTH, 0);
            drawPageText(page);
            // TODO: are there different page lengths?
            drawStenographer(transcript.getDateTime(), page.size()-1);
            endPage();
        }
        saveDoc();
    }

    @Override
    protected void newPageSetup() throws IOException {
        contentStream.drawPolygon(X_VALS, Y_VALS);
    }

    /**
     * Draw correctly aligned text along with Stenographer information.
     * @param page to draw.
     */
    private void drawPageText(List<String> page) throws IOException {
        for (String ln : page) {
            TranscriptLine line = new TranscriptLine(ln);
            if (line.isPageNumber())
                drawPageNumber(line.fullText());
            else
                drawText(line);
        }
    }

    /**
     * Page numbers should be right aligned above the border.
     * @param line to write.
     */
    private void drawPageNumber(String line) throws IOException {
        float offset = right - (line.length() + 1) * FONT_WIDTH;
        contentStream.moveTextPositionByAmount(offset, FONT_WIDTH*2);
        contentStream.drawString(line);
        contentStream.moveTextPositionByAmount(-offset, -FONT_SIZE*2);
    }

    private void drawText(TranscriptLine line) throws IOException {
        int indent = NO_LINE_NUM_INDENT;
        String text = line.fullText();
        // Line numbers should aligned left of the left vertical border.
        if (line.hasLineNumber())
            indent = text.split("\\s")[0].length() + 1;
        // Lines without line numbers should have their spacing between each
        // vertical line similar to other transcripts.
        float offset = left - indent * FONT_WIDTH;
        contentStream.moveTextPositionByAmount(offset, -FONT_SIZE);
        contentStream.drawString(text);
        contentStream.moveTextPositionByAmount(-offset, -FONT_SIZE);
    }

    /**
     * The stenographer should be centered at the bottom of the page
     * @param ldt when the session occurred.
     * @param lineCount of the session.
     */
    private void drawStenographer(LocalDateTime ldt, int lineCount) throws IOException {
        String stenographer = Stenographer.getStenographer(ldt);
        float offset = (lineCount - STENOGRAPHER_LINE_NUM) * 2 * FONT_SIZE; // * 2 because of double spacing.
        contentStream.moveTextPositionByAmount((right + left - stenographer.length() * FONT_WIDTH) / 2, offset);
        contentStream.drawString(stenographer);
    }
}
