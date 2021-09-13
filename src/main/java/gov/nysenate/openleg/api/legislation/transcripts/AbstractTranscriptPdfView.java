package gov.nysenate.openleg.api.legislation.transcripts;

import gov.nysenate.openleg.api.BasePdfView;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractTranscriptPdfView extends BasePdfView {
    protected static final float TOP = 710f, BOTTOM = 90f, LEFT = 105f, RIGHT = 575f, FONT_WIDTH = 7f, SPACING = 2;
    private static final Pattern LINE_NUM_PATTERN = Pattern.compile("^(?<indent> {0,11}\\d+)( |$)");
    protected int indent;

    @Override
    protected void newPageSetup() throws IOException {
        contentStream.addRect(LEFT, BOTTOM, RIGHT - LEFT, TOP - BOTTOM);
        contentStream.setStrokingColor(Color.BLACK);
        contentStream.stroke();
    }

    @Override
    protected void writePage(List<String> page) throws IOException {
        boolean hasPageNum = isPageNumber(page.get(0));
        String pageNum = hasPageNum ? page.get(0) : "";
        drawPageNumber(pageNum.trim());
        for (int i = hasPageNum ? 1 : 0; i < page.size(); i++)
            drawLine(page.get(i));
        drawStenographer(page.size());
    }

    protected static int indentSize(List<String> secondPage) {
        for (var line : secondPage) {
            Matcher m = LINE_NUM_PATTERN.matcher(line);
            if (m.find())
                return m.end("indent") + 1;
        }
        return 0;
    }

    protected abstract boolean isPageNumber(String firstLine);

    protected abstract boolean isDoubleSpaced();

    protected abstract void drawStenographer(int lineCount) throws IOException;

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
        float offset = LEFT - indent * FONT_WIDTH;
        contentStream.newLineAtOffset(offset, -FONT_SIZE);
        contentStream.showText(line);
        contentStream.newLineAtOffset(-offset, isDoubleSpaced() ? -FONT_SIZE : 0);
    }

}
