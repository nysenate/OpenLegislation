package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawTreeNode;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public abstract class LawPdfView extends BasePdfView {
    /**
     * Writes a law document to a PDF.
     * @param node of the appropriate law document.
     * @param outputStream to write to,
     * @param lawDocs to lookup law document text.
     * @throws IOException if there was a problem making the pdf.
     * @throws COSVisitorException if there was a problem saving the PDF.
     */
    public static void writeLawDocumentPdf(LawTreeNode node, OutputStream outputStream, Map<String, LawDocument> lawDocs)
            throws IOException, COSVisitorException {
        PDDocument doc = new PDDocument();
        PDFont font = PDType1Font.COURIER;
        LinkedList<String> lines = new LinkedList<>(Arrays.asList(getNodeText(node, lawDocs).split("\\\\n")));
        while(!lines.isEmpty()) {
            PDPage pg = new PDPage();
            PDPageContentStream contentStream = new PDPageContentStream(doc, pg);
            contentStream.beginText();
            contentStream.setFont(font, FONT_SIZE);
            contentStream.moveTextPositionByAmount(MARGIN, TOP);
            for (float currY = TOP; currY > pg.getMediaBox().getLowerLeftY() && !lines.isEmpty(); currY -= FONT_SIZE) {
                contentStream.drawString(lines.poll() + "\n");
                contentStream.moveTextPositionByAmount(0, -FONT_SIZE);
            }
            contentStream.endText();
            contentStream.close();
            doc.addPage(pg);
        }
        doc.save(outputStream);
        doc.close();
    }

    /**
     * Gets the text for a law document and all of its children in the proper order.
     * @param topNode to build String of.
     * @param lawDocs to get text from.
     * @return a full String of a law document.
     */
    private static String getNodeText(LawTreeNode topNode, Map<String, LawDocument> lawDocs) {
        StringBuilder ret = new StringBuilder();
        LawDocument topDoc = lawDocs.remove(topNode.getDocumentId());
        // Ensures something is being removed every time, so you can't go into an infinite loop.
        if (topDoc != null) {
            ret.append(topDoc.getText());
            for (LawTreeNode currNode : topNode.getChildNodeList())
                ret.append(getNodeText(currNode, lawDocs));
        }
        return ret.toString();
    }
}
