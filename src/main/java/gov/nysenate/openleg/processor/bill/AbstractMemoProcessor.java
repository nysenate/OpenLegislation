package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.processor.base.AbstractLegDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * This Class is responsible for taking the context of the Memo files for processing.
 * <p>
 * Created by Robert Bebber on 3/8/17.
 */
public abstract class AbstractMemoProcessor extends AbstractLegDataProcessor {

    /**
     * This method is responsible for pulling the content tag and if it isn't present will throw a Parsing Exception.
     *
     * @param memo The String of the content tag with CSS script
     * @return
     */
    protected String parsedMemoHTML(String memo) {
        Document document = Jsoup.parse(memo);
        Elements elements = document.body().select("pre");
        if (elements.size() == 0) {
            throw new ParseError("No Pre Tags Found");
        }
        return getText(elements.get(0));
    }

    /**
     * This method is responsible for building a string of the entire content tag, as standing version of JSoup had
     * not supported getWholeText.
     *
     * @param cell This the pre tag for the content to be fulled from
     * @return
     */
    private String getText(Element cell) {
        StringBuilder textBuilder = new StringBuilder();
        for (Node node : cell.childNodes()) {
            if (node instanceof TextNode) {
                textBuilder.append(((TextNode) node).getWholeText());
            } else {
                for (Node childNode : node.childNodes()) {
                    textBuilder.append(getText((Element) childNode));
                }
                textBuilder.append(node.outerHtml());
            }
        }
        if (cell.childNodes().isEmpty()) {
            textBuilder.append(cell.outerHtml());
        }
        return textBuilder.toString();
    }
}
