package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.api.legislation.transcripts.AbstractTranscriptPdfView;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pdf representation of a transcript designed to match the formatting
 * of the official transcripts.
 */
public class TranscriptPdfView extends AbstractTranscriptPdfView {
    private static final int STENOGRAPHER_LINE_NUM = 27;
    private final String stenographer;
    private final float stenographerCenter;
    private final boolean hasLineNumbers;

    public TranscriptPdfView(Transcript transcript) throws IOException {
        if (transcript == null)
            throw new IllegalArgumentException("Supplied transcript cannot be null when converting to pdf.");

        this.stenographer = Stenographer.getStenographer(transcript.getDateTime());
        this.stenographerCenter = (RIGHT + LEFT - stenographer.length() * FONT_WIDTH) / 2;
        var parser = new TranscriptPdfParser(transcript.getDateTime(), transcript.getText());
        this.hasLineNumbers = parser.hasLineNumbers();
        writePages(TOP - FONT_WIDTH, 0, parser.getPages());
    }



    /**
     * Draw correctly aligned text along with Stenographer information.
     * @param page to draw.
     */
    @Override
    protected void writePage(List<String> page) throws IOException {
        drawPageNumber(page.get(0));
        for (int i = 1; i < page.size(); i++)
            drawLine(page.get(i));
        drawStenographer(page.size());
    }

    /**
     * Page numbers should be right aligned above the border.
     * @param line to write.
     */
    private void drawPageNumber(String line) throws IOException {
        float offset = RIGHT - (line.length() + 1) * FONT_WIDTH;
        contentStream.newLineAtOffset(offset, FONT_WIDTH * SPACING);
        contentStream.showText(line);
        contentStream.newLineAtOffset(-offset, -FONT_SIZE * SPACING);
    }

    private void drawLine(String line) throws IOException {
        Matcher m = Pattern.compile(" {0,11}((\\d*).*)").matcher(line);
        // Line numbers should align left of the left vertical border.
        int indent = m.find() && hasLineNumbers ? m.group(2).length() + 1 : 0;
        float offset = LEFT - indent * FONT_WIDTH;
        contentStream.newLineAtOffset(offset, -FONT_SIZE);
        contentStream.showText(m.group(1));
        contentStream.newLineAtOffset(-offset, -FONT_SIZE);
    }

    /**
     * The stenographer should be centered at the bottom of the page
     * @param lineCount of the session.
     */
    private void drawStenographer(int lineCount) throws IOException {
        float offset = (lineCount - STENOGRAPHER_LINE_NUM) * FONT_SIZE * SPACING;
        contentStream.newLineAtOffset(stenographerCenter, offset);
        contentStream.showText(stenographer);
    }
}
