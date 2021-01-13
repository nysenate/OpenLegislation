package gov.nysenate.openleg.api;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Contains some common constants and methods for writing PDFs.
 */
public abstract class BasePdfView {
    public static final float FONT_SIZE = 12f;
    public static final PDFont FONT = PDType1Font.COURIER;
    protected static final float TOP = 740f;
    protected final ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
    protected PDPageContentStream contentStream;
    private final PDDocument doc = new PDDocument();
    private PDPage currPage;

    public ResponseEntity<byte[]> writeData() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
    }

    /**
     * Overridden if more setup is needed.
     */
    protected void newPageSetup() throws IOException {}

    /**
     * Creates and initializes a new page.
     * @param top of the new page.
     * @param margin of the new page.
     * @param keepFont true if the font from the end of last page should be kept.
     * @throws IOException if the page can't be written to.
     */
    protected void newPage(float top, float margin, boolean keepFont) throws IOException {
        currPage = new PDPage();
        contentStream = new PDPageContentStream(doc, currPage);
        newPageSetup();
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(margin, top);
        if (!keepFont)
            contentStream.setFont(FONT, FONT_SIZE);
    }

    protected void writePages(List<List<String>> pages, float margin) throws IOException {
        for (List<String> page : pages) {
            newPage(TOP, margin, false);
            for (String line : page) {
                contentStream.drawString(line);
                contentStream.moveTextPositionByAmount(0, -FONT_SIZE);
            }
            endPage();
        }
    }

    /**
     * Closes the current page, and adds it to the document.
     * @throws IOException if the page can't be closed.
     */
    protected void endPage() throws IOException {
        contentStream.endText();
        contentStream.close();
        doc.addPage(currPage);
    }

    /**
     * Saves and closes the document.
     */
    protected void saveDoc() throws IOException {
        try {
            doc.save(pdfBytes);
        } catch (COSVisitorException e) {
            throw new IOException("Error saving PDF.");
        }
        doc.close();
    }
}
