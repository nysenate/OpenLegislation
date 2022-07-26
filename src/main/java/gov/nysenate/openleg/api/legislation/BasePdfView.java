package gov.nysenate.openleg.api.legislation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(BasePdfView.class);
    protected static final float FONT_SIZE = 12f, DEFAULT_TOP = 740f;
    protected static final PDFont FONT = PDType1Font.COURIER;
    protected final ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
    protected PDPageContentStream contentStream;
    private final PDDocument doc = new PDDocument();
    private PDPage currPage;

    public ResponseEntity<byte[]> writeData() throws IOException {
        doc.close();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
        return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
    }

    /**
     * Writes the given pages to the PDF, then saves the document.
     * @param pages to write.
     * @param top where to start page.
     * @param margin on each page.
     */
    protected void writePages(float top, float margin, List<List<String>> pages) throws IOException {
        for (List<String> page : pages) {
            createPage(top, margin);
            writePage(page);
            contentStream.endText();
            contentStream.close();
            doc.addPage(currPage);
        }
        doc.save(pdfBytes);
    }

    /**
     * Overridden if more setup is needed.
     */
    protected void newPageSetup() throws IOException {}

    /**
     * Creates and initializes a new page.
     * @param top of the new page.
     * @param margin of the new page.
     * @throws IOException if the page can't be written to.
     */
    private void createPage(float top, float margin) throws IOException {
        currPage = new PDPage();
        contentStream = new PDPageContentStream(doc, currPage);
        newPageSetup();
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, top);
        contentStream.setFont(FONT, FONT_SIZE);
        contentStream.setLeading(FONT_SIZE * getSpacing());
    }

    protected float getSpacing() {
        return 1;
    }

    protected void writePage(List<String> page) throws IOException {
        for (String line : page) {
            writeLine(line);
            contentStream.newLine();
        }
    }

    protected void writeLine(String line) throws IOException {
        try {
            contentStream.showText(line);
        }
        catch (IllegalArgumentException ex) {
            logger.warn("Bad character in PDF. Line: " + line);
        }
    }
}
