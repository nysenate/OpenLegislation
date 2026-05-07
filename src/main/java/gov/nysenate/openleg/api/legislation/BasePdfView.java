package gov.nysenate.openleg.api.legislation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains some common constants and methods for writing PDFs.
 */
public abstract class BasePdfView {
    private static final Logger logger = LoggerFactory.getLogger(BasePdfView.class);
    protected static final float FONT_SIZE = 12f, CHAR_WIDTH = FONT_SIZE * 0.6f;
    protected static final PDFont FONT = PDType1Font.COURIER;
    protected static final PDColor TEXT_COLOR = new PDColor(new float[]{0, 0, 0}, PDDeviceRGB.INSTANCE);
    protected static final PDColor LINK_COLOR = new PDColor(new float[]{0, 0, 1}, PDDeviceRGB.INSTANCE);
    protected static final PDBorderStyleDictionary LINK_UNDERLINE = new PDBorderStyleDictionary();
    static {
        LINK_UNDERLINE.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
        LINK_UNDERLINE.setWidth(1);
    }
    protected final ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
    protected PDPageContentStream contentStream;
    private final PDDocument doc = new PDDocument();
    protected final float top, margin, spacing;
    private float currX, currY;
    private PDPage currPage;
    private static final Pattern LINK_PATTERN = Pattern.compile("(.*?)<a href=\"([^\"]+)\"[^>]*>([^<]+)</a>");

    protected BasePdfView(Float top, Float margin, Float spacing) {
        this.top = top == null ? 740f : top;
        this.margin = margin == null ? 0f : margin;
        this.spacing = spacing == null ? 1f : spacing;
    }

    public ResponseEntity<byte[]> getData() throws IOException {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
        return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
    }

    /**
     * Writes the given pages to the PDF, then saves the document.
     * @param pages to write.
     */
    protected void writePages(List<List<String>> pages) throws IOException {
        for (List<String> page : pages) {
            createPage();
            writePage(page);
            contentStream.endText();
            contentStream.close();
            doc.addPage(currPage);
        }
        doc.save(pdfBytes);
        doc.close();
    }

    /**
     * Overridden if more setup is needed.
     */
    protected void newPageSetup() throws IOException {}

    /**
     * Moves to the start of the next line with position tracking
     * @param tx The x translation
     * @param ty The y translation
     * @throws IOException if there is an error writing to the stream.
     */
    protected void newLineAtOffsetTracked(float tx, float ty) throws IOException {
        contentStream.newLineAtOffset(tx, ty);
        currX += tx;
        currY += ty;
    }

    /**
     * Creates and initializes a new page.
     * @throws IOException if the page can't be written to.
     */
    private void createPage() throws IOException {
        currPage = new PDPage();
        contentStream = new PDPageContentStream(doc, currPage);
        newPageSetup();
        contentStream.beginText();
        currX = margin;
        currY = top - FONT_SIZE * spacing; // offset so first decrement lands at top
        contentStream.newLineAtOffset(margin, top);
        contentStream.setFont(FONT, FONT_SIZE);
        contentStream.setLeading(FONT_SIZE * spacing);
    }

    protected void writePage(List<String> page) throws IOException {
        for (String line : page) {
            writeLine(line);
            contentStream.newLine();
            currY -= FONT_SIZE * spacing;
        }
    }

    protected void writeLine(String line) throws IOException {
        List<PdfText> textSegments = new ArrayList<>();
        Matcher linkMatcher = LINK_PATTERN.matcher(line);
        int charsPrinted = 0;
        int lastEnd = 0;

        while (linkMatcher.find()) {
            textSegments.add(new PdfText(linkMatcher.group(1)));
            charsPrinted += linkMatcher.group(1).length();

            String linkedText = linkMatcher.group(3);
            // 2 is arbitrary for padding
            var linkRectangle = new PDRectangle(currX + charsPrinted * CHAR_WIDTH, currY - 2,
                    linkedText.length() * CHAR_WIDTH, FONT_SIZE + 2);
            textSegments.add(new LinkedPdfText(linkedText, linkMatcher.group(2), linkRectangle));
            charsPrinted += linkedText.length();
            lastEnd = linkMatcher.end();
        }
        // TODO: can use linkMatcher.hasMatch() in Java 20, and remove lastEnd
        textSegments.add(new PdfText(line.substring(lastEnd)));

        for (PdfText textSegment : textSegments) {
            textSegment.showText();
        }
    }

    private class PdfText {
        private final String text;

        public PdfText(String text) {
            this.text = text;
        }

        public void showText() throws IOException {
            try {
                contentStream.showText(text);
            } catch (IllegalArgumentException ex) {
                logger.warn("Bad character in PDF. Text: {}", text);
            }
        }
    }

    private class LinkedPdfText extends PdfText {
        private final PDAnnotationLink link;

        public LinkedPdfText(String text, String url, PDRectangle rectangle) {
            super(text);
            var uriAction = new PDActionURI();
            uriAction.setURI(url);
            this.link = new PDAnnotationLink();
            link.setAction(uriAction);
            link.setRectangle(rectangle);
            link.setColor(LINK_COLOR);
            link.setBorderStyle(LINK_UNDERLINE);
        }

        @Override
        public void showText() throws IOException {
            contentStream.setNonStrokingColor(LINK_COLOR);
            super.showText();
            contentStream.setNonStrokingColor(TEXT_COLOR);
            currPage.getAnnotations().add(link);
        }
    }
}
