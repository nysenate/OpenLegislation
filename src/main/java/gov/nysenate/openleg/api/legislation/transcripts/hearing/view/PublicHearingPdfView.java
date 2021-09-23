package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.legislation.transcripts.AbstractTranscriptPdfView;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingTextUtils;

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
        var pages = PublicHearingTextUtils.getPages(publicHearing.getText());
        // TODO: some of these lines are too long.
        this.isWrongFormat = PublicHearingTextUtils.isWrongFormat(pages);
        writeTranscriptPages(pages);
    }

    @Override
    protected void writePage(List<String> page) throws IOException {
        // Indents may change every page in these hearings.
        if (isWrongFormat)
            indent = getIndent(page) + 1;
        super.writePage(page);
    }
}
