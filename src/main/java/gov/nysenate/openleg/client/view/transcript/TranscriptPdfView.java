package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.processor.transcript.TranscriptLine;
import gov.nysenate.openleg.util.TranscriptTextUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pdf representation of a transcript designed to match the formatting
 * of the official transcripts.
 */
public class TranscriptPdfView
{
    // Kirkland started on May 16, 2011
    private static LocalDateTime KIRKLAND_START_TIME = LocalDateTime.of(2011, 5, 16, 0, 0, 0);

    // Candyco started Jan 1st, 2005
    private static LocalDateTime CANDYCO_START_TIME = LocalDateTime.of(2005, 1, 1, 0, 0, 0);

    // Candyco also did 1999-2003
    private static LocalDateTime CANDYCO_1999_START = LocalDateTime.of(1999, 1, 1, 0, 0, 0);
    private static LocalDateTime CANDYCO_2003_END = LocalDateTime.of(2004, 1, 1, 0, 0, 0);

    // Pauline Williman did 1993-1998
    private static LocalDateTime WILLIMAN_START = LocalDateTime.of(1993, 1, 1, 0, 0, 0);
    private static LocalDateTime WILLIMAN_END = LocalDateTime.of(1999, 1, 1, 0, 0, 0);

    private static Float bot = 90f;
    private static Float right = 575f;
    private static Float top = 710f;
    private static Float left = 105f;
    private static Float fontSize = 12f;
    private static Float fontWidth = 7f;

    public static final int NO_LINE_NUM_INDENT = 11;
    public static final int STENOGRAPHER_LINE_NUM = 26;

    public TranscriptPdfView(Transcript transcript, OutputStream outputStream) throws IOException, COSVisitorException {
        if (transcript == null) {
            throw new IllegalArgumentException("Supplied transcript cannot be null when converting to pdf.");
        }

        PDDocument doc = new PDDocument();
        PDFont font = PDType1Font.COURIER;

        List<List<String>> pages = TranscriptTextUtils.getPdfFormattedPages(transcript.getTranscriptText());
        for (List<String> page : pages) {
            PDPage pg = new PDPage(PDPage.PAGE_SIZE_LETTER);
            PDPageContentStream contentStream = new PDPageContentStream(doc, pg);

            drawBorder(contentStream);

            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            moveStreamToTopOfPage(contentStream);

            int lineCount = drawPageText(page, contentStream);
            drawStenographer(transcript, page, contentStream, lineCount);

            contentStream.endText();
            contentStream.close();
            doc.addPage(pg);
        }
        doc.save(outputStream);
        doc.close();
    }

    /**
     * Draw page text, correctly aligning page numbers, line numbers, and lines without line numbers.
     * Also insert information the the Stenographer.
     * <p>
     *     Page numbers should be right aligned at the top of the page.
     *     Line numbers should aligned left of the left vertical line.
     *     Lines without line numbers should have their spacing between each vertical line similar
     *          to other transcripts.
     *     The stenographer should be centered at the bottom of the page
     * </p>
     */
    private static int drawPageText(List<String> page, PDPageContentStream contentStream) throws IOException {
        int lineCount = 0;
        for (String ln : page) {
            TranscriptLine line = new TranscriptLine(ln);

            if (line.isPageNumber()) {
                float offset = right - (line.fullText().trim().length() + 1) * fontWidth;
                drawPageNumber(line.fullText().trim(), offset, contentStream);
            }
            else {
                int indent;
                String text;
                if (line.hasLineNumber()) {
                    indent = lineNumberLength(line) + 1;
                    text = line.fullText().trim();
                } else {
                    indent = NO_LINE_NUM_INDENT;
                    text = line.fullText();
                }

                float offset = left - indent * fontWidth;
                drawLine(text, offset, contentStream);

                lineCount++;
            }
        }

        return lineCount;
    }

    private static void drawBorder(PDPageContentStream contentStream) throws IOException {
        contentStream.drawLine(left, top, left, bot);
        contentStream.drawLine(left, top, right, top);
        contentStream.drawLine(left, bot, right, bot);
        contentStream.drawLine(right, top, right, bot);
    }

    private static void drawLine(String line, float offset, PDPageContentStream contentStream) throws IOException {
        contentStream.moveTextPositionByAmount(offset, -fontSize);
        contentStream.drawString(line);
        contentStream.moveTextPositionByAmount(-offset, -fontSize);
    }

    private static void drawPageNumber(String line, float offset, PDPageContentStream contentStream) throws IOException {
        contentStream.moveTextPositionByAmount(offset, fontWidth * 2);
        contentStream.drawString(line);
        contentStream.moveTextPositionByAmount(-offset, -fontSize * 2);
    }

    private void moveStreamToTopOfPage(PDPageContentStream contentStream) throws IOException {
        contentStream.moveTextPositionByAmount(0, top - fontWidth);
    }

    private static int lineNumberLength(TranscriptLine line) {
        return line.fullText().trim().split("\\s")[0].length();
    }

    private static void drawStenographer(Transcript transcript, List<String> page, PDPageContentStream contentStream, int lineCount) throws IOException {
        String stenographer = "";
        if (transcript.getDateTime().isAfter(KIRKLAND_START_TIME)) {
            stenographer = "Kirkland Reporting Service";
        }
        else if (transcript.getDateTime().isAfter(CANDYCO_START_TIME)) {
            stenographer = "Candyco Transcription Service, Inc.";
        }
        else if (transcript.getDateTime().isAfter(CANDYCO_1999_START)
                 && transcript.getDateTime().isBefore(CANDYCO_2003_END)) {
            stenographer = "Candyco Transcription Service, Inc.";
        }
        else if (transcript.getDateTime().isAfter(WILLIMAN_START)
                 && transcript.getDateTime().isBefore(WILLIMAN_END)) {
            stenographer = "Pauline Williman, Certified Shorthand Reporter";
        }

        float offset = (lineCount - STENOGRAPHER_LINE_NUM) * 2 * fontSize; // * 2 because of double spacing.
        contentStream.moveTextPositionByAmount(left + (right - left - stenographer.length() * fontWidth) / 2, offset);
        contentStream.drawString(stenographer);
    }
}
