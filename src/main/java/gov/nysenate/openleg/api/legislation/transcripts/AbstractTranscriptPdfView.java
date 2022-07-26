package gov.nysenate.openleg.api.legislation.transcripts;

import gov.nysenate.openleg.api.legislation.BasePdfView;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractTranscriptPdfView extends BasePdfView {
    protected static final float TOP = 710f, BOTTOM = 90f, LEFT = 105f, RIGHT = 575f, FONT_WIDTH = 7f;

    private static final Pattern LINE_NUM_PATTERN = Pattern.compile("^ {0,11}\\d{0,2}");

    protected int indent;

    protected void writeTranscriptPages(List<List<String>> pages) throws IOException {
        this.indent = getIndent(pages.get(1));
        writePages(TOP, 0, pages);
    }

    @Override
    protected void newPageSetup() throws IOException {
        contentStream.addRect(LEFT, BOTTOM, RIGHT - LEFT, TOP - BOTTOM);
        contentStream.setStrokingColor(Color.BLACK);
        contentStream.stroke();
    }

    @Override
    protected void writePage(List<String> page) throws IOException {
        String pageNum = page.get(0).trim();
        float xOffsetPageNum = RIGHT - (pageNum.length() + 1) * FONT_WIDTH;
        contentStream.newLineAtOffset(xOffsetPageNum, FONT_SIZE/2);
        contentStream.showText(pageNum);
        contentStream.newLine();
        float xOffsetLine = LEFT - indent * FONT_WIDTH;
        contentStream.newLineAtOffset(-xOffsetPageNum + xOffsetLine, -FONT_SIZE/2);
        super.writePage(page.subList(1, page.size()));
        contentStream.newLineAtOffset(-xOffsetLine, 0);
    }

    protected static int getIndent(List<String> page) {
        int indent = 1 << 10;
        for (int i = 1; i < page.size(); i++) {
            if (page.get(i).isBlank())
                continue;
            Matcher m = LINE_NUM_PATTERN.matcher(page.get(i));
            if (m.find())
                indent = Math.min(m.end() + 1, indent);
        }
        return indent == 1 ? -1: indent;
    }
}
