package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawDocumentType;
import gov.nysenate.openleg.legislation.law.LawTreeNode;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.legislation.law.LawDocumentType.CHAPTER;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.SECTION;

public abstract class LawPdfView extends BasePdfView {
    private final static int CHARS_PER_LINE = 84;
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
        Queue<LawDocument> lawDocQueue = node.getAllNodes().stream()
                .map(curr -> lawDocs.get(curr.getDocumentId())).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
        LawDocument currDoc = lawDocQueue.remove();
        Queue<String> charBlocks = getNodeText(currDoc);
        PDDocument doc = new PDDocument();
        boolean isBold = false;
        // The top of the Queue is the document we are currently writing.
        while(!lawDocQueue.isEmpty() || !charBlocks.isEmpty()) {
            PDPage pg = new PDPage();
            PDPageContentStream contentStream = new PDPageContentStream(doc, pg);
            contentStream.beginText();
            contentStream.moveTextPositionByAmount(MARGIN, TOP);
            for (int currLine = 0; currLine < LINES_PER_PAGE; currLine++) {
                isBold = writeLine(charBlocks, contentStream, isBold, currDoc.getDocType());
                if (charBlocks.isEmpty()) {
                    if (lawDocQueue.isEmpty())
                        break;
                    currDoc = lawDocQueue.poll();
                    charBlocks = getNodeText(currDoc);
                }
            }
            contentStream.endText();
            contentStream.close();
            doc.addPage(pg);
        }
        doc.save(outputStream);
        doc.close();
    }

    private static boolean writeLine(Queue<String> charBlocks, PDPageContentStream contentStream,
                                     boolean isBold, LawDocumentType type) throws IOException {
        for (int charCount = 0; !charBlocks.isEmpty() &&
                charCount + charBlocks.peek().length() < CHARS_PER_LINE;) {
            String curr = charBlocks.poll();
            if (curr == null)
                continue;
            if (curr.equals("\n"))
                break;
            if (curr.equals(BOLD_MARKER)) {
                isBold = !isBold;
                continue;
            }
            contentStream.setFont(isBold ? PDType1Font.COURIER_BOLD :
                    PDType1Font.COURIER, FONT_SIZE);
            // Spaces at the beginning of a section line should be ignored.
            if (charCount == 0 && curr.matches(" +") && type == SECTION)
                curr = curr.trim();
            contentStream.drawString(curr);
            charCount += curr.length();
        }
        contentStream.moveTextPositionByAmount(0, -FONT_SIZE);
        return isBold;
    }


    /**
     * Returns a block of words with proper bolding.
     * @param doc to draw data from.
     * @return character blocks.
     */
    private static Queue<String> getNodeText(LawDocument doc) {
        if (doc == null)
            return new LinkedList<>();
        String docText = doc.getText().replaceAll("\\\\n", "\n");
        // Marks a title for bolding.
        if (doc.getDocType() != CHAPTER)
            docText = markForBolding(docText, doc.getTitle(), doc.getDocType());
        return getWordsAndSpaces(docText);
    }

    private static String markForBolding(String text, String title, LawDocumentType type) {
        // Spaces are added to ensure the bold markers will be parsed out.
        String boldTitle = (type == SECTION ? title : title.toUpperCase()) +
                ". " + BOLD_MARKER + (type == SECTION ? "" : "\n");
        return BOLD_MARKER + " " + text.replaceFirst("(?i)" + title + "[. \n]+?", boldTitle);
        // Unnecessary spaces are removed.
//        return text.replaceAll(BOLD_MARKER + " +", BOLD_MARKER + " ");
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

        LinkedList<String> ret = new LinkedList<>();
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
            }
        }
        ret.add("\n");
        ret.add("\n");
        return ret;
    }
}
