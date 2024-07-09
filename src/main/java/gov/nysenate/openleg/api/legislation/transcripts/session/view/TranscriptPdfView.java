package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.api.legislation.transcripts.AbstractTranscriptPdfView;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.processors.transcripts.session.Stenographer;

import java.io.IOException;
import java.util.List;

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
        var pages = new TranscriptPdfParser(transcript.getText()).getPages();
        this.stenographer = Stenographer.getStenographer(transcript.getDateTime().toLocalDate());
        this.stenographerCenter = (RIGHT + LEFT - stenographer.length() * FONT_WIDTH) / 2;
        writeTranscriptPages(pages);
    }

    @Override
    protected void writePage(List<String> page) throws IOException {
        super.writePage(page);
        // The stenographer should be centered at the bottom of the page.
        float yOffset = (page.size() - STENOGRAPHER_LINE_NUM) * FONT_SIZE * getSpacing();
        contentStream.newLineAtOffset(stenographerCenter, yOffset);
        contentStream.showText(stenographer);
    }

    @Override
    protected float getSpacing() {
        return 2;
    }
}
