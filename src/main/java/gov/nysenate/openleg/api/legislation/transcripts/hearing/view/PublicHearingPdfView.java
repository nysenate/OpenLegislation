package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * PDF representation of a Public Hearing.
 */
public abstract class PublicHearingPdfView extends BasePdfView {
    private static final float MARGIN = 10f;

    public static void writePublicHearingPdf(PublicHearing publicHearing, OutputStream outputStream)
            throws IOException, COSVisitorException {
        if (publicHearing == null) {
            throw new IllegalArgumentException("Supplied Public Hearing cannot be null when converting to pdf.");
        }

        PDDocument doc = new PDDocument();
        List<List<String>> pages = PublicHearingTextUtils.getPages(publicHearing.getText());
        for (List<String> page : pages) {
            PDPage pg = new PDPage();
            PDPageContentStream contentStream = new PDPageContentStream(doc, pg);
            contentStream.beginText();
            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.moveTextPositionByAmount(MARGIN, TOP);
            drawPage(contentStream, page);
            contentStream.endText();
            contentStream.close();
            doc.addPage(pg);
        }
        doc.save(outputStream);
        doc.close();
    }

    private static void drawPage(PDPageContentStream contentStream, List<String> page) throws IOException {
        for (String line : page) {
            contentStream.drawString(line);
            contentStream.moveTextPositionByAmount(0, -FONT_SIZE);
        }
    }
}
