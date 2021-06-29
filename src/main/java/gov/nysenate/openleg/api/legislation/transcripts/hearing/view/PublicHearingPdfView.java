package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.legislation.transcripts.AbstractTranscriptPdfView;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;

import java.io.IOException;
import java.util.List;

/**
 * PDF representation of a Public Hearing.
 */
public class PublicHearingPdfView extends AbstractTranscriptPdfView {
    private static final float MARGIN = 10f;
    // TODO: Must not be done by filename so fixes will work.
    public PublicHearingPdfView(PublicHearing publicHearing) throws IOException {
        if (publicHearing == null)
            throw new IllegalArgumentException("Supplied Public Hearing cannot be null when converting to pdf.");
        List<List<String>> pages = PublicHearing.getPages(publicHearing.getText());
        writePages(DEFAULT_TOP, MARGIN, pages);
    }
}
