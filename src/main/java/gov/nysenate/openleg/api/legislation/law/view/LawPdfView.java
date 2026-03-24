package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.legislation.BasePdfView;
import gov.nysenate.openleg.legislation.law.LawDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.*;

/**
 * Converts a LawDocument, and potentially its children in order, into a PDF.
 */
public class LawPdfView extends BasePdfView {
    private static final float BOTTOM = 60f;
    private final int LINES_PER_PAGE = (int) ((top - BOTTOM)/(FONT_SIZE * spacing));
    private boolean bold = false;

    public LawPdfView(Queue<LawDocument> lawDocQueue) throws IOException {
        super(null, 50f, 1.5f);
        List<String> lines = new ArrayList<>();
        for (LawDocument doc : lawDocQueue)
            lines.addAll(LawPdfUtil.getLines(doc));
        writePages(getPages(lines));
    }

    /**
     * Writes a single line from the document, after splitting it into sections marked by bold markers.
     * @throws IOException if the writing was interrupted.
     */
    @Override
    protected void writeLine(String line) throws IOException {
        // Used to avoid split() erasing a marker at the end.
        if (line.endsWith(LawPdfUtil.BOLD_MARKER))
            line += " ";
        List<String> sections = Arrays.asList(line.split(LawPdfUtil.BOLD_MARKER));
        for (int i = 0; i < sections.size(); i++) {
            contentStream.setFont(bold ? PDType1Font.COURIER_BOLD : FONT, FONT_SIZE);
            contentStream.showText(sections.get(i));
            if (i != sections.size() - 1)
                bold = !bold;
        }
    }

    /**
     * Converts lines into pages using a set page length.
     * Necessary so that multiple documents can be on the same page.
     * @param lines of the full text.
     * @return the pages, formatted as in other PDFs.
     */
    private List<List<String>> getPages(List<String> lines) {
        // Even with no text, a page should still be generated.
        if (lines.isEmpty())
            return List.of(new ArrayList<>());
        int numPages = (int) Math.ceil((double)lines.size()/LINES_PER_PAGE);
        List<List<String>> pages = new ArrayList<>(numPages);
        for (int page = 1; page <= numPages; page++) {
            // All pages, except potentially the last, will have the same number of lines.
            int endIndex = Math.min(page * LINES_PER_PAGE, lines.size());
            List<String> currPage = lines.subList((page - 1) * LINES_PER_PAGE, endIndex);
            pages.add(currPage);
        }
        return pages;
    }
}
