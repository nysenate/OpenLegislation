package gov.nysenate.openleg.api.legislation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.OpenMode;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionLaunch;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderEffectDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDLinkAppearanceHandler;
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
    protected static final float FONT_SIZE = 12f, DEFAULT_TOP = 740f, CHAR_WIDTH = FONT_SIZE * 0.6f;
    protected static final PDFont FONT = PDType1Font.COURIER;
    protected static final PDColor LINK_COLOR = new PDColor(new float[]{0, 0, 1}, PDDeviceRGB.INSTANCE);
    protected static final PDBorderStyleDictionary LINK_UNDERLINE = new PDBorderStyleDictionary();
    static {
        LINK_UNDERLINE.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
        LINK_UNDERLINE.setWidth(1);
    }
    protected final ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
    protected PDPageContentStream contentStream;
    private final PDDocument doc = new PDDocument();
    private float currX, currY;
    private PDPage currPage;
    private static final Pattern LINK_PATTERN = Pattern.compile("<a href=\"([^\"]+)\">([^<]+)</a>");

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
     * @param top of the new page.
     * @param margin of the new page.
     * @throws IOException if the page can't be written to.
     */
    private void createPage(float top, float margin) throws IOException {
        currPage = new PDPage();
        contentStream = new PDPageContentStream(doc, currPage);
        newPageSetup();
        contentStream.beginText();
        currX = margin;
        currY = top - FONT_SIZE * getSpacing(); // offset so first decrement lands at top
        contentStream.newLineAtOffset(margin, top);
        contentStream.setFont(FONT, FONT_SIZE);
        contentStream.setLeading(FONT_SIZE * getSpacing());
    }

    protected float getSpacing() {
        return 1;
    }

    protected void writePage(List<String> page) throws IOException {
        for (String line : page) {
            List<String> urls = new ArrayList<>();
            List<float[]> linkRectangles = new ArrayList<>();

            Matcher matcher = LINK_PATTERN.matcher(line);
            StringBuilder plainText = new StringBuilder();
            int lastEnd = 0;

            // extract link text from HTML
            while (matcher.find()) {
                plainText.append(line, lastEnd, matcher.start());
                int startPos = plainText.length();
                String linkText = matcher.group(2);
                plainText.append(linkText);
                int endPos = plainText.length();
                lastEnd = matcher.end();

                // calculate link position
                float x1 = currX + startPos * CHAR_WIDTH;
                float x2 = currX + endPos * CHAR_WIDTH;
                linkRectangles.add(new float[]{x1, currY - 2, x2, currY + FONT_SIZE});
                urls.add(matcher.group(1));
            }
            plainText.append(line, lastEnd, line.length());

            writeLine(plainText.toString());

            // insert each link into the page
            for (int i = 0; i < urls.size(); i++) {
                PDAnnotationLink link = new PDAnnotationLink();
                PDActionJavaScript actionJS = new PDActionJavaScript("app.launchURL('" + urls.get(i) + "', true);");
                link.setAction(actionJS);
                float[] r = linkRectangles.get(i);
                link.setRectangle(new PDRectangle(r[0], r[1], r[2] - r[0], r[3] - r[1]));
                link.setColor(LINK_COLOR);
                link.setBorderStyle(LINK_UNDERLINE);
                link.setHighlightMode(PDAnnotationLink.HIGHLIGHT_MODE_NONE);
                currPage.getAnnotations().add(link);
            }

            contentStream.newLine();
            currY -= FONT_SIZE * getSpacing();
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
