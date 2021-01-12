package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawTreeNode;
import org.apache.pdfbox.exceptions.COSVisitorException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public class LawPdfView extends BasePdfView {
    public static final float SPACING = 1.5f;
    private static final float BOTTOM = 60f, MARGIN = 20f;
    private static final int LINES_PER_PAGE = (int) ((TOP - BOTTOM)/(FONT_SIZE*SPACING));

    /**
     * Writes a law document to a PDF.
     * @param node of the appropriate law document.
     * @param outputStream to write to,
     * @param lawDocs to lookup law document text.
     * @throws IOException if there was a problem making the pdf.
     * @throws COSVisitorException if there was a problem saving the PDF.
     */
    public void writeLawDocumentPdf(LawTreeNode node, OutputStream outputStream,
                                           Map<String, LawDocument> lawDocs)
            throws IOException, COSVisitorException {
        Queue<LawDocument> lawDocQueue = node.getAllNodes().stream()
                .map(curr -> lawDocs.get(curr.getDocumentId())).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
        LawTextParser parser = new LawTextParser(lawDocQueue.remove());
        while(!lawDocQueue.isEmpty() || !parser.finished()) {
            newPage(TOP, MARGIN, true);
            for (int currLine = 0; currLine < LINES_PER_PAGE; currLine++) {
                parser.writeLine(contentStream);
                if (parser.finished()) {
                    if (lawDocQueue.isEmpty())
                        break;
                    parser = new LawTextParser(lawDocQueue.poll());
                }
            }
            endPage();
        }
        saveDoc(outputStream);
    }
}
