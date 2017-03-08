package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.base.ParseError;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * Created by senateuser on 3/8/17.
 */
public abstract class AbstractMemoProcessor extends AbstractDataProcessor {
    protected String parsedMemoHTML(String memo) {
        Document document = Jsoup.parse(memo);
        Elements elements = document.body().select("pre");
        if (elements.size() == 0) {
            throw new ParseError("No Pre Tags Found");
        }
        return getText(elements.get(0));
    }

    private String getText(Element cell) {
        StringBuilder textBuilder = new StringBuilder();
        for (Node node : cell.childNodes()) {
            if (node instanceof TextNode) {
                textBuilder.append(((TextNode)node).getWholeText());
            }
            else {
                for (Node childNode : node.childNodes()) {
                    textBuilder.append(getText((Element)childNode));
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
