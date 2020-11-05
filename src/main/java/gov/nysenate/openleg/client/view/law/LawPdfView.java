package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.BasePdfView;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawDocumentType;
import gov.nysenate.openleg.model.law.LawTreeNode;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class LawPdfView extends BasePdfView {
    // Marks where a title starts and ends for bolding.
    private final static String BOLD_MARKER = "%~~%";
    private final static Logger logger = LoggerFactory.getLogger(LawPdfView.class);

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
        Queue<String> lines = new LinkedList<>(Arrays.asList(getNodeText(node, lawDocs).split("\\\\n")));
        boolean isBold = false;
        while(!lines.isEmpty()) {
            PDPage pg = new PDPage();
            PDPageContentStream contentStream = new PDPageContentStream(doc, pg);
            contentStream.beginText();
            contentStream.moveTextPositionByAmount(MARGIN, TOP);
            for (float currY = TOP; currY > pg.getMediaBox().getLowerLeftY() && !lines.isEmpty(); currY -= FONT_SIZE) {
                String line = lines.poll();
                String[] titleSplit = line.split(BOLD_MARKER);
                for (String text : titleSplit) {
                    if (text.length() < 2)
                        continue;
                    if (titleSplit.length != 1) {
                        isBold = !isBold;
                        if (isBold) {
                            contentStream.moveTextPositionByAmount(0, -FONT_SIZE);
                            currY -= FONT_SIZE;
                        }
                        logger.info("isBold = " + isBold + ", switch at end of String " + text);
                    }
                    contentStream.setFont(isBold ? PDType1Font.COURIER_BOLD : PDType1Font.COURIER, FONT_SIZE);
                    contentStream.drawString(text);
                }
                if (line.endsWith(BOLD_MARKER)) {
                    isBold = !isBold;
                    logger.info("isBold = " + isBold + ", switch at end of line " + line);
                }
                contentStream.drawString("\n");
                contentStream.moveTextPositionByAmount(0, -FONT_SIZE);
            }
            contentStream.drawString("\n");
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
        if (topDoc != null) {
            String docText = topDoc.getText();
            if (topDoc.getDocType() == LawDocumentType.SECTION) {
                ret.append(BOLD_MARKER);
                docText = docText.replaceFirst(topDoc.getTitle() + "\\.?",
                        topDoc.getTitle() + BOLD_MARKER);
            }
            ret.append(docText);
            for (LawTreeNode currNode : topNode.getChildNodeList())
                ret.append(getNodeText(currNode, lawDocs)).append("\n");
        }
        return ret.toString();
    }
}
