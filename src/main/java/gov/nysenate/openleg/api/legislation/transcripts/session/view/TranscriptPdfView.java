package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.common.util.TranscriptTextUtils;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.processors.transcripts.session.TranscriptLine;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pdf representation of a transcript designed to match the formatting
 * of the official transcripts.
 */
public class TranscriptPdfView extends BasePdfView {
    // Kirkland started on May 16, 2011
    private static final LocalDateTime KIRKLAND_START_TIME = LocalDate.of(2011, 5, 16).atStartOfDay();

    // Candyco started Jan 1st, 2005
    private static final LocalDateTime CANDYCO_START_TIME = LocalDate.of(2005, 1, 1).atStartOfDay();

    // Candyco also did 1999-2003
    private static final LocalDateTime CANDYCO_1999_START = LocalDate.of(1999, 1, 1).atStartOfDay();
    private static final LocalDateTime CANDYCO_2003_END = LocalDate.of(2004, 1, 1).atStartOfDay();

    // Pauline Williman did 1993-1998
    private static final LocalDateTime WILLIMAN_START = LocalDate.of(1993, 1, 1).atStartOfDay();
    private static final LocalDateTime WILLIMAN_END = LocalDate.of(1999, 1, 1).atStartOfDay();

    private static final Float bot = 90f;
    private static final Float right = 575f;
    private static final Float top = 710f;
    private static final Float left = 105f;
    private static final Float fontWidth = 7f;

    public static final int NO_LINE_NUM_INDENT = 11;
    public static final int STENOGRAPHER_LINE_NUM = 26;

    public static void writeTranscriptPdf(Transcript transcript, OutputStream outputStream) throws IOException, COSVisitorException {
        if (transcript == null) {
            throw new IllegalArgumentException("Supplied transcript cannot be null when converting to pdf.");
        }

        try (PDDocument doc = new PDDocument()) {
            List<List<String>> pages = TranscriptTextUtils.getPdfFormattedPages(transcript.getText());
            for (List<String> page : pages) {
                PDPage pg = new PDPage(PDPage.PAGE_SIZE_LETTER);
                PDPageContentStream contentStream = new PDPageContentStream(doc, pg);
                drawBorder(contentStream);
                contentStream.beginText();
                contentStream.setFont(FONT, FONT_SIZE);
                moveStreamToTopOfPage(contentStream);

                int lineCount = drawPageText(page, contentStream);
                drawStenographer(transcript, contentStream, lineCount);

                contentStream.endText();
                contentStream.close();
                doc.addPage(pg);
            }
            doc.save(outputStream);
        }
    }

    /**
     * Draw page text, correctly aligning page numbers, line numbers, and lines without line numbers.
     * Also insert information about the Stenographer.
     * <p>
     *     Page numbers should be right aligned above the border.
     *     Line numbers should aligned left of the left vertical border.
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
                drawPageNumber(line.fullText().trim(), contentStream);
            }
            else {
                drawText(contentStream, line);
                lineCount++;
            }
        }

        return lineCount;
    }

    private static void drawText(PDPageContentStream contentStream, TranscriptLine line) throws IOException {
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
    }

    private static void drawBorder(PDPageContentStream contentStream) throws IOException {
        contentStream.drawLine(left, top, left, bot);
        contentStream.drawLine(left, top, right, top);
        contentStream.drawLine(left, bot, right, bot);
        contentStream.drawLine(right, top, right, bot);
    }

    private static void drawLine(String line, float offset, PDPageContentStream contentStream) throws IOException {
        contentStream.moveTextPositionByAmount(offset, -FONT_SIZE);
        contentStream.drawString(line);
        contentStream.moveTextPositionByAmount(-offset, -FONT_SIZE);
    }

    private static void drawPageNumber(String line, PDPageContentStream contentStream) throws IOException {
        float offset = right - (line.length() + 1) * fontWidth;
        contentStream.moveTextPositionByAmount(offset, fontWidth * 2);
        contentStream.drawString(line);
        contentStream.moveTextPositionByAmount(-offset, -FONT_SIZE * 2);
    }

    private static void moveStreamToTopOfPage(PDPageContentStream contentStream) throws IOException {
        contentStream.moveTextPositionByAmount(0, top - fontWidth);
    }

    private static int lineNumberLength(TranscriptLine line) {
        return line.fullText().trim().split("\\s")[0].length();
    }

    private static void drawStenographer(Transcript transcript, PDPageContentStream contentStream, int lineCount) throws IOException {
        String stenographer = "";
        if (transcript.getDateTime().isAfter(KIRKLAND_START_TIME)) {
            stenographer = "Kirkland Reporting Service";
        }
        else if ((transcript.getDateTime().isAfter(CANDYCO_START_TIME)) || transcript.getDateTime().isAfter(CANDYCO_1999_START)
                 && transcript.getDateTime().isBefore(CANDYCO_2003_END)) {
            stenographer = "Candyco Transcription Service, Inc.";
        }
        else if (transcript.getDateTime().isAfter(WILLIMAN_START)
                 && transcript.getDateTime().isBefore(WILLIMAN_END)) {
            stenographer = "Pauline Williman, Certified Shorthand Reporter";
        }

        float offset = (lineCount - STENOGRAPHER_LINE_NUM) * 2 * FONT_SIZE; // * 2 because of double spacing.
        contentStream.moveTextPositionByAmount(left + (right - left - stenographer.length() * fontWidth) / 2, offset);
        contentStream.drawString(stenographer);
    }
}
