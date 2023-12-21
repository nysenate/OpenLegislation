package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.legislation.transcripts.AbstractTranscriptPdfView;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingTextUtils;

import java.io.IOException;
import java.util.List;

/**
 * PDF representation of a Hearing.
 */
public class HearingPdfView extends AbstractTranscriptPdfView {
    private final boolean isWrongFormat;

    public HearingPdfView(Hearing hearing) throws IOException {
        if (hearing == null) {
            throw new IllegalArgumentException("Supplied Hearing cannot be null when converting to pdf.");
        }
        var pages = HearingTextUtils.getPages(hearing.getText());
        // TODO: some of these lines are too long.
        this.isWrongFormat = HearingTextUtils.isWrongFormat(pages);
        writeTranscriptPages(pages);
    }

    @Override
    protected void writePage(List<String> page) throws IOException {
        // Indents may change every page in these hearings.
        if (isWrongFormat) {
            indent = getIndent(page) + 1;
        }
        super.writePage(page);
    }
}
