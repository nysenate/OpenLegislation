package gov.nysenate.openleg.api.legislation.transcripts;

import gov.nysenate.openleg.api.BasePdfView;

import java.awt.*;
import java.io.IOException;

public abstract class AbstractTranscriptPdfView extends BasePdfView {
    protected static final float TOP = 710f, BOTTOM = 90f, LEFT = 105f, RIGHT = 575f, FONT_WIDTH = 7f, SPACING = 2;
    @Override
    protected void newPageSetup() throws IOException {
        contentStream.addRect(LEFT, BOTTOM, RIGHT - LEFT, TOP - BOTTOM);
        contentStream.setStrokingColor(Color.BLACK);
        contentStream.stroke();
    }
}
