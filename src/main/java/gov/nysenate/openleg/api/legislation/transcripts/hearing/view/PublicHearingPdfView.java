package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import org.apache.pdfbox.exceptions.COSVisitorException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * PDF representation of a Public Hearing.
 */
public class PublicHearingPdfView extends BasePdfView {
    private static final float MARGIN = 10f;

    public void writePublicHearingPdf(PublicHearing publicHearing, OutputStream outputStream)
            throws IOException, COSVisitorException {
        if (publicHearing == null) {
            throw new IllegalArgumentException("Supplied Public Hearing cannot be null when converting to pdf.");
        }

        List<List<String>> pages = PublicHearingTextUtils.getPages(publicHearing.getText());
        writePages(pages, MARGIN);
        saveDoc(outputStream);
    }
}
