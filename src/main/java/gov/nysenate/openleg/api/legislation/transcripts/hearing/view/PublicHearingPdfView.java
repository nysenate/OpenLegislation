package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.legislation.transcripts.AbstractTranscriptPdfView;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;

import java.io.IOException;
import java.util.List;

/**
 * PDF representation of a Public Hearing.
 */
public class PublicHearingPdfView extends AbstractTranscriptPdfView {
    private final boolean isWrongFormat;

    public PublicHearingPdfView(PublicHearing publicHearing) throws IOException {
        if (publicHearing == null)
            throw new IllegalArgumentException("Supplied Public Hearing cannot be null when converting to pdf.");
        List<List<String>> pages = PublicHearing.getPages(publicHearing.getText());
        this.isWrongFormat = PublicHearing.isWrongFormat(pages);
        // These hearings format their line numbers differently.
        if (isWrongFormat)
            indent = 3;
        else
            indent = indentSize(pages.get(1));
        writePages(TOP - FONT_WIDTH, 0, pages);
    }

    @Override
    protected boolean isPageNumber(String firstLine) {
        return firstLine.trim().matches("(?i)(Page )?\\d+");
    }

    @Override
    protected boolean isDoubleSpaced() {
        return false;
    }

    @Override
    protected void drawStenographer(int lineCount) throws IOException {
        if (!isWrongFormat)
            return;
        // TODO: draw Stenographer
    }
}
