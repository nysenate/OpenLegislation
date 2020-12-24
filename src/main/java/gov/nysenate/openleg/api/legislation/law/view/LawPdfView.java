package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawTreeNode;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public abstract class LawPdfView extends BasePdfView {
    public final static int LINES_PER_PAGE = (int) ((TOP - BOTTOM)/FONT_SIZE);

    /**
     * Writes a law document to a PDF.
     * @param node of the appropriate law document.
     * @param outputStream to write to,
     * @param lawDocs to lookup law document text.
     * @throws IOException if there was a problem making the pdf.
     * @throws COSVisitorException if there was a problem saving the PDF.
     */
    public static void writeLawDocumentPdf(LawTreeNode node, OutputStream outputStream,
                                           Map<String, LawDocument> lawDocs)
            throws IOException, COSVisitorException {
        Queue<LawDocument> lawDocQueue = node.getAllNodes().stream()
                .map(curr -> lawDocs.get(curr.getDocumentId())).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
        LawTextParser parser = new LawTextParser(lawDocQueue.remove());
        PDDocument doc = new PDDocument();
        while(!lawDocQueue.isEmpty() || !parser.finished()) {
            PDPage pg = new PDPage();
            PDPageContentStream contentStream = new PDPageContentStream(doc, pg);
            contentStream.beginText();
            contentStream.moveTextPositionByAmount(MARGIN, TOP);
            for (int currLine = 0; currLine < LINES_PER_PAGE; currLine++) {
                parser.writeLine(contentStream);
                if (parser.finished()) {
                    if (lawDocQueue.isEmpty())
                        break;
                    parser = new LawTextParser(lawDocQueue.poll());
                }
            }
            contentStream.endText();
            contentStream.close();
            doc.addPage(pg);
        }
        doc.save(outputStream);
        doc.close();
    }
}
