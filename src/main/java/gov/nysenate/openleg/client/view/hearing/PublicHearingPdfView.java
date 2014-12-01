package gov.nysenate.openleg.client.view.hearing;

import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.util.PublicHearingTextUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * PDF representation of a Public Hearing.
 */
public class PublicHearingPdfView
{
    private static Float fontSize = 12f;
    private static Float top = 740f;
    private static Float margin = 10f;

    public PublicHearingPdfView(PublicHearing publicHearing, OutputStream outputStream) throws IOException, COSVisitorException {
        if (publicHearing == null) {
            throw new IllegalArgumentException("Supplied Public Hearing cannot be null when converting to pdf.");
        }

        PDDocument doc = new PDDocument();
        PDFont font = PDType1Font.COURIER;

        List<List<String>> pages = PublicHearingTextUtils.getPages(publicHearing.getText());
        for (List<String> page : pages) {
            PDPage pg = new PDPage();
            PDPageContentStream contentStream = new PDPageContentStream(doc, pg);
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.moveTextPositionByAmount(margin, top);
            drawPage(contentStream, page);
            contentStream.endText();
            contentStream.close();
            doc.addPage(pg);
        }
        doc.save(outputStream);
        doc.close();
    }

    private void drawPage(PDPageContentStream contentStream, List<String> page) throws IOException {
        for (String line : page) {
            contentStream.drawString(line);
            contentStream.moveTextPositionByAmount(0, -fontSize);
        }
    }
}
