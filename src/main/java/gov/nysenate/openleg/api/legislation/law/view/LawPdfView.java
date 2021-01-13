package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.BasePdfView;
import gov.nysenate.openleg.legislation.law.LawDocument;

import java.io.IOException;
import java.util.Queue;

public class LawPdfView extends BasePdfView {
    public static final float SPACING = 1.5f;
    private static final float BOTTOM = 60f, MARGIN = 20f;
    private static final int LINES_PER_PAGE = (int) ((TOP - BOTTOM)/(FONT_SIZE*SPACING));

    public LawPdfView(Queue<LawDocument> lawDocQueue) throws IOException {
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
        saveDoc();
    }
}
