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

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public abstract class LawPdfView extends BasePdfView {
    private final static int CHARS_PER_LINE = 80;
    private final static int LINES_PER_PAGE = (int) ((TOP - BOTTOM)/FONT_SIZE);
    // Marks where a title starts and ends for bolding.
    private final static String BOLD_MARKER = "%~~%";

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
        String docString = getNodeText(node, lawDocs);
        Queue<String> charBlocks = getWordsAndSpaces(docString);
        boolean isBold = false;
        while(!charBlocks.isEmpty()) {
            PDPage pg = new PDPage();
            PDPageContentStream contentStream = new PDPageContentStream(doc, pg);
            contentStream.beginText();
            contentStream.moveTextPositionByAmount(MARGIN, TOP);
            for (int currLine = 0; currLine < LINES_PER_PAGE; currLine++) {
                for (int charCount = 0; !charBlocks.isEmpty() && charCount + charBlocks.peek().length() < CHARS_PER_LINE;) {
                    String curr = charBlocks.poll();
                    if (curr == null)
                        continue;
                    if (curr.equals("\n"))
                        break;
                    boolean switchBold = curr.contains(BOLD_MARKER);
                    curr = curr.replaceAll(BOLD_MARKER, "");
                    contentStream.setFont(isBold ? PDType1Font.COURIER_BOLD :
                            PDType1Font.COURIER, FONT_SIZE);
                    contentStream.drawString(curr);
                    isBold = (switchBold != isBold);
                    charCount += curr.length();
                }
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
        if (topDoc != null) {
            String docText = topDoc.getText();
            if (topDoc.getDocType() == LawDocumentType.SECTION) {
                ret.append(BOLD_MARKER);
                docText = docText.replaceFirst(topDoc.getTitle() + "\\.?",
                        topDoc.getTitle() + BOLD_MARKER + ".");
            }
            ret.append(docText);
            for (LawTreeNode currNode : topNode.getChildNodeList())
                ret.append(getNodeText(currNode, lawDocs)).append("\n");
        }
        return ret.toString().replaceAll("\\\\n", "\n").trim();
    }

    /**
     * Parses document String into words and spaces for simpler parsing.
     * @param nodeText to be parsed.
     * @return word blocks.
     */
    private static Queue<String> getWordsAndSpaces(String nodeText) {
        List<String> words = Arrays.stream(nodeText.split("\\s+"))
                .filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<String> spaces = Arrays.stream(nodeText.split("[^\\s]+"))
                .filter(s -> !s.isEmpty()).collect(Collectors.toList());

        Queue<String> ret = new LinkedList<>();
        for (int i = 0; i < words.size() || i < spaces.size(); i++) {
            if (i < words.size())
                ret.add(words.get(i));
            if (i < spaces.size()) {
                String currSpace = spaces.get(i);
                // Sometimes, newlines alone are used to separate words in the original text.
                if (currSpace.equals("\n"))
                    ret.add(" ");
                else if (currSpace.contains("\n")) {
                    String[] spaceSplit = currSpace.split("\n");
                    for (String s : spaceSplit) {
                        // To avoid double-counting of repeated newlines.
                        if (!s.isEmpty())
                            ret.add(s);
                        // Will generate a newline during PDF writing process.
                        ret.add("\n");
                    }

                    if (currSpace.endsWith("\n"))
                        ret.add("\n");
                }
                else
                    ret.add(spaces.get(i));
            }
        }
        return ret;
    }
}
