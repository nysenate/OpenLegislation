package gov.nysenate.openleg.util;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

/** Utilities for scraping html */
public abstract class ScrapeUtils {

    /**
     * Given a jsoup element, gets all contained text preserving formatting by tags such as <br>
     * @param element An html element
     * @return String
     */
    public static String getFormattedText(Element element) {
        StringBuilder stringBuilder = new StringBuilder();
        element.childNodes().forEach(node -> {
            if (node instanceof TextNode) {
                stringBuilder.append(((TextNode) node).text());
            } else if (node instanceof Element) {
                if ("br".equalsIgnoreCase(((Element) node).tag().getName())) {
                    stringBuilder.append("\n");
                } else {
                    stringBuilder.append(getFormattedText((Element) node));
                }
            }
        });
        return stringBuilder.toString();
    }
}
