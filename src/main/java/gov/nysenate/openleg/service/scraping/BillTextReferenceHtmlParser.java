package gov.nysenate.openleg.service.scraping;

import gov.nysenate.openleg.processor.base.ParseError;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles parsing of LRS bill scraped html files.
 */
@Service
public class BillTextReferenceHtmlParser {

    private static final String lrsOutageText = "404 - Processing Error";
    private static final Pattern billIdPattern = Pattern.compile("^([A-z]\\d+)(?:-([A-z]))?$");

    /**
     * Parse the print number from an LRS bill scrape html file.
     * @param doc
     * @return
     */
    public String parsePrintNo(Document doc) {
        Element printNoElement = doc.select("span.nv_bot_info > strong").first();
        if (printNoElement == null) {
            throw new ParseError("could not get scraped bill print no:");
        }
        Matcher printNoMatcher = billIdPattern.matcher(printNoElement.text());
        if (printNoMatcher.matches()) {
            String basePrintNo = printNoMatcher.group(1);
            String version = printNoMatcher.group(2) == null ? "" : printNoMatcher.group(2);
            return basePrintNo + version;
        }
        else {
            throw new ParseError("could not parse scraped bill print no: " + printNoElement.text());
        }
    }

    /**
     * Parse the bill text from an LRS bill scrape html file.
     * @param doc
     * @return
     */
    public String parseText(Document doc) {
        Element contents = doc.getElementById("nv_bot_contents");
        if (contents == null) {
            throw new ParseError("Could not locate scraped bill contents");
        }
        Elements textEles = new Elements();

        // Bill text is found in all pre tags contained in <div id="nv_bot_contents"> before the first <hr class="noprint">
        for (Element element : contents.children()) {
            if ("pre".equalsIgnoreCase(element.tagName())) {
                textEles.add(element);
            } else if ("hr".equalsIgnoreCase(element.tagName()) && element.classNames().contains("noprint")) {
                break;
            }
        }

        StringBuilder textBuilder = new StringBuilder();
        textEles.forEach(ele -> processTextNode(ele, textBuilder));
        return textBuilder.toString();
    }

    /**
     * Extracts bill/memo text from an element recursively
     */
    private void processTextNode(Element ele, StringBuilder stringBuilder) {
        for (Node t : ele.childNodes()) {
            if (t instanceof Element) {
                Element e = (Element) t;
                // TEXT IN <U> TAGS IS REPRESENTED IN CAPS FOR SOBI BILL TEXT
                if ("u".equals(e.tag().getName())) {
                    stringBuilder.append(e.text().toUpperCase());
                } else {
                    processTextNode(e, stringBuilder);
                }
            } else if (t instanceof TextNode) {
                stringBuilder.append(((TextNode) t).getWholeText());
            }
        }
    }

    /**
     * Parses the sponsor memo.
     * @param doc
     * @return
     */
    public String parseMemo(Document doc) {
        Element memoElement = doc.select("pre:last-of-type").first(); // you are the first and last of your kind
        if (memoElement != null) {
            StringBuilder memoBuilder = new StringBuilder();
            processTextNode(memoElement, memoBuilder);
            // todo format text
            return memoBuilder.toString();
        }
        // TODO: add parse exception here if element is null like other methods.
        return "";
    }

    /**
     * Determines if a bill was missing from LRS.
     * @param doc A {@link Document} containing the text from a {@link BillTextReferenceFile}.
     * @return {@code true} if the Bill represented by this Document was missing from LRS,
     *         {@code false} if the bill exists on LRS.
     */
    public boolean isBillMissing(Document doc) {
        Element botContents = doc.getElementById("nv_bot_contents");
        if (botContents == null) {
            return false;
        }
        Elements redFonts = botContents.select("font[color=\"red\"]");
        Element notFoundText = redFonts.first();
        return notFoundText != null && "Bill Status Information Not Found".equals(notFoundText.text());
    }

    /**
     * Detects if the document indicates an lrs outage
     * returns true if so
     */
    public boolean isLrsOutage(Document doc) {
        Elements h2Eles = doc.getElementsByTag("h2");
        if (h2Eles.isEmpty()) {
            return false;
        }
        Element firstH2 = h2Eles.first();
        return firstH2.text().startsWith(lrsOutageText);
    }
}
