package gov.nysenate.openleg.api.legislation.bill.view;

import gov.nysenate.openleg.api.legislation.BasePdfView;
import gov.nysenate.openleg.legislation.bill.*;
import gov.nysenate.openleg.legislation.bill.exception.BillAmendNotFoundEx;
import gov.nysenate.openleg.legislation.bill.utils.BillTextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.legislation.bill.BillTextFormat.HTML;
import static gov.nysenate.openleg.legislation.bill.BillTextFormat.PLAIN;

/**
 * PDF representation of a bill.
 */
public class BillPdfView extends BasePdfView {
    private static final Logger logger = LoggerFactory.getLogger(BillPdfView.class);
    private static final float BILL_MARGIN = 10f, RESOLUTION_MARGIN = 46f;
    private static final Pattern BAD_CHAR_PATTERN = Pattern.compile("(?i)U\\+(?<unicode>[A-F0-9]{4})");
    private final String displayName;
    private static final String STYLES = """

            u {color: green;}
            s {color: red;}
            p.brk {page-break-before: always;}
            body {font-size: 14px;}
            .header {font-size: 1.8em; text-align: center; font-weight: bold;}
            """,
            BILL_STYLES = STYLES +
            "@page {margin-left: 31.5px; margin-top: 27.5px;}\n" +
            "body {font-size: 14px;}\n",
            RESOLUTION_STYLES = STYLES +
                    "@page {margin-left: 50px; margin-top: 30px;}\n" +
                    "body {font-size: 16px;}\n";

    public BillPdfView(Bill bill, Version version) throws IOException {
        if (bill == null)
            throw new IllegalArgumentException("Supplied bill cannot be null when converting to pdf!");
        if (!bill.hasAmendment(version))
            throw new BillAmendNotFoundEx(bill.getBaseBillId().withVersion(version));
        BillAmendment ba = bill.getAmendment(version);
        this.displayName = ba.toString();

        BillTextFormat format = ba.getFullText(HTML).isEmpty() ? PLAIN : HTML;
        String fullText = ba.getFullText(format);
        switch (format) {
            case HTML -> writeHtmlPdf(bill.isResolution(), fullText);
            case PLAIN -> writePlainTextPdf(ba.getBillId(), fullText);
            default -> throw new IllegalStateException("Unable to write pdf for text format: " + format);
        }
    }

    @Override
    protected void writeLine(String line) throws IOException {
        try {
            super.writeLine(line);
        }
        catch (IllegalArgumentException ex) {
            logger.warn("In " + displayName + ", there's a bad character in line: " + line);
            Matcher m = BAD_CHAR_PATTERN.matcher(ex.getMessage());
            if (!m.find())
                throw ex;
            String unicodeStr = m.group("unicode");
            char[] badCharArray = Character.toChars(Integer.parseInt(unicodeStr, 16));
            line = line.replaceAll(new String(badCharArray), "");
            super.writeLine(line);
        }
    }

    private void writeHtmlPdf(boolean resolution, String fullTextHtml) {
        ITextRenderer renderer = new ITextRenderer();
        Document doc = Jsoup.parse(fullTextHtml);
        // Remove backwards html
        doc.getElementsByTag("basefont").remove();
        doc.getElementsByTag("style").remove();
        doc.select("font[size=\"5\"]").forEach(fontEle -> {
            String text = fontEle.getAllElements().stream()
                    .map(Element::textNodes)
                    .flatMap(Collection::stream)
                    .map(TextNode::getWholeText)
                    .collect(Collectors.joining());
            Element replacement = new Element(Tag.valueOf("span"), "/");
            replacement.addClass("header");
            replacement.text(text);
            fontEle.replaceWith(replacement);
        });
        // Add our own styles
        Element styleTag = doc.head().appendElement("style");
        styleTag.appendText(resolution ? RESOLUTION_STYLES : BILL_STYLES);
        // Get reformatted html
        StringBuilder htmlBuilder = new StringBuilder();
        doc.children().forEach(child -> writeHtml(child, htmlBuilder, false));
        String formattedHtml = htmlBuilder.toString();
        // Render and output pdf
        renderer.setDocumentFromString(formattedHtml);
        renderer.layout();
        renderer.createPDF(pdfBytes);
    }

    /**
     *  This method is needed because JSoup does not preserve whitespace when parsing font tags
     *  (These are replaced with <pre class="header"></pre> after parsing.)
     */
    private static void writeHtml(Node node, StringBuilder sBuilder, boolean headerParent) {
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            if (headerParent) {
                sBuilder.append(textNode.getWholeText());
            } else {
                sBuilder.append(textNode.outerHtml());
            }
        } else if (node instanceof Element){
            Element ele = (Element) node;
            sBuilder.append("<")
                    .append(ele.tagName())
                    .append(ele.attributes().html());

            boolean header = ele.hasClass("header");

            if (ele.tag().isSelfClosing() && ele.childNodes().isEmpty()) {
                sBuilder.append(" />");
            } else {
                sBuilder.append(">");
                ele.childNodes().forEach(child -> writeHtml(child, sBuilder, headerParent || header));
                sBuilder.append("</")
                        .append(ele.tagName())
                        .append(">");
            }
        }
    }

    private void writePlainTextPdf(BillId billId, String fullText) throws IOException {
        List<List<String>> pages = billId.getBillType().isResolution() ?
                BillTextUtils.getResolutionPages(fullText) : BillTextUtils.getBillPages(fullText);
        if (pages.isEmpty())
            pages = Collections.singletonList(Collections.singletonList(
                    "No full text available for " + billId));
        float margin = billId.getBillType().isResolution() ? RESOLUTION_MARGIN : BILL_MARGIN;
        writePages(DEFAULT_TOP, margin, pages);
    }
}
