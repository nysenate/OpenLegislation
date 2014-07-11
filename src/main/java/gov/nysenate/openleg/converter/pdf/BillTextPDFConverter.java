package gov.nysenate.openleg.converter.pdf;

import gov.nysenate.openleg.api.AbstractApiRequest;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.IBaseObject;
import gov.nysenate.openleg.util.TextFormatter;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class BillTextPDFConverter
{
    private static Float fontSize = 12f;
    private static Float top = 740f;
    private static Float billMargin = 10f;
    private static Float resolutionMargin = 46f;

    public static void write(IBaseObject object, OutputStream out) throws IOException, COSVisitorException, AbstractApiRequest.ApiRequestException {
        if (!(object instanceof Bill)) {
            throw new AbstractApiRequest.ApiRequestException("Unable to convert " + object.getOtype() + "s to pdf.");
        }

        Bill bill = (Bill) object;
        PDDocument doc = new PDDocument();
        PDFont font = PDType1Font.COURIER;

        Float margin = billMargin;
        if (bill.isResolution()) {
            margin = resolutionMargin;
        }

        List<List<String>> pages = TextFormatter.pdfPrintablePages(bill);
        for (List<String> page : pages) {
            PDPage pg = new PDPage(PDPage.PAGE_SIZE_LETTER);
            PDPageContentStream contentStream = new PDPageContentStream(doc, pg);

            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.moveTextPositionByAmount(margin, top);

            for (String line : page) {
                contentStream.drawString(line);
                contentStream.moveTextPositionByAmount(0, -fontSize);
            }

            contentStream.endText();
            contentStream.close();
            doc.addPage(pg);
        }

        doc.save(out);
        doc.close();
    }

}
