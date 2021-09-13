package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.api.legislation.transcripts.AbstractTranscriptPdfView;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.processors.transcripts.session.Stenographer;

import java.io.IOException;

/**
 * Pdf representation of a transcript designed to match the formatting
 * of the official transcripts.
 */
public class TranscriptPdfView extends AbstractTranscriptPdfView {
    private static final int STENOGRAPHER_LINE_NUM = 27;
    private final String stenographer;
    private final float stenographerCenter;

    public TranscriptPdfView(Transcript transcript) throws IOException {
        if (transcript == null)
            throw new IllegalArgumentException("Supplied transcript cannot be null when converting to pdf.");

        this.stenographer = Stenographer.getStenographer(transcript.getDateTime());
        this.stenographerCenter = (RIGHT + LEFT - stenographer.length() * FONT_WIDTH) / 2;
        var parser = new TranscriptPdfParser(transcript.getText());
        var pages = parser.getPages();
        this.indent = parser.hasLineNumbers() ? AbstractTranscriptPdfView.indentSize(pages.get(1)) : 0;
        writePages(TOP - FONT_WIDTH, 0, pages);
    }

    // Session transcripts are guaranteed to start with page numbers.
    @Override
    protected boolean isPageNumber(String firstLine) {
        return true;
    }

    @Override
    protected boolean isDoubleSpaced() {
        return true;
    }

    /**
     * The stenographer should be centered at the bottom of the page
     * @param lineCount of the session.
     */
    @Override
    protected void drawStenographer(int lineCount) throws IOException {
        float offset = (lineCount - STENOGRAPHER_LINE_NUM) * FONT_SIZE * SPACING;
        contentStream.newLineAtOffset(stenographerCenter, offset);
        contentStream.showText(stenographer);
    }
}
