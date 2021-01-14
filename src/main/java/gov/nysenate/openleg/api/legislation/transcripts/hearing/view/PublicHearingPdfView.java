package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;

import java.io.IOException;
import java.util.List;

/**
 * PDF representation of a Public Hearing.
 */
public class PublicHearingPdfView extends BasePdfView {
    private static final float MARGIN = 10f;

    public PublicHearingPdfView(PublicHearing publicHearing) throws IOException {
        if (publicHearing == null)
            throw new IllegalArgumentException("Supplied Public Hearing cannot be null when converting to pdf.");
        List<List<String>> pages = PublicHearingTextUtils.getPages(publicHearing.getText());
        writePages(pages, MARGIN);
    }
}
