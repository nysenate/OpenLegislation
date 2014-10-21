package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.util.BillTextUtils;
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
 * PDF representation of a bill.
 */
public class BillPdfView
{
    private static Float fontSize = 12f;
    private static Float top = 740f;
    private static Float billMargin = 10f;
    private static Float resolutionMargin = 46f;

    public BillPdfView(Bill bill, Version version, OutputStream outputStream) throws IOException, COSVisitorException {
        if (bill == null) {
            throw new IllegalArgumentException("Supplied bill cannot be null when converting to pdf!");
        }
        if (!bill.hasAmendment(version)) {
            throw new IllegalArgumentException("Supplied bill version " + version.name() + " is not set in the bill");
        }

        BillAmendment ba = bill.getAmendment(version);
        PDDocument doc = new PDDocument();
        PDFont font = PDType1Font.COURIER;

        Float margin = billMargin;
        if (bill.isResolution()) {
            margin = resolutionMargin;
        }

        List<List<String>> pages = BillTextUtils.getPages(ba.getFullText());
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

        doc.save(outputStream);
        doc.close();
    }
}
