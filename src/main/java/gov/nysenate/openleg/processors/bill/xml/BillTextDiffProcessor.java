package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.legislation.bill.BillText;
import gov.nysenate.openleg.legislation.bill.TextDiff;
import gov.nysenate.openleg.legislation.bill.TextDiffType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.safety.Safelist;
import org.jsoup.select.NodeVisitor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BillTextDiffProcessor {

    /**
     * Accepts XML bill text and converts it into a BillText object.
     *
     * @param xmlText String
     * @return ArrayList&lt;TextDiff&gt;
     */
    public BillText processBillText(String xmlText) {
        if (xmlText == null) {
            return new BillText(new ArrayList<>());
        }

        String cleanHtml = cleanXmlText(xmlText);
        Document doc = Jsoup.parse(cleanHtml);
        BillTextNodeVisitor nodeVisitor = new BillTextNodeVisitor();
        doc.traverse(nodeVisitor);
        return new BillText(nodeVisitor.getTextDiffs());
    }

    /**
     * Clean the xmlText.
     * - Removes closing tags with no associated opening tags.
     * - Removes tags not important for parsing the bill text.
     * - Converts section symbol alt code into the section symbol character.
     *
     * This method has to do some manual replacing of characters which are html escaped by jsoup while cleaning.
     * I have been unable to find a way to prevent html escaping while still preserving whitespace.
     * It seems you can only do one or the other.
     * @param xmlText
     * @return
     */
    private String cleanXmlText(String xmlText) {
        Document.OutputSettings outputSettings = new Document.OutputSettings()
                .escapeMode(Entities.EscapeMode.xhtml) // Limit the characters to be html escaped as much as possible. We manually fix these later.
                .prettyPrint(false); // Preserve whitespace while cleaning.

        // Whitelist of allowed tags and attributes.
        var whitelist = new Safelist();
        whitelist.addTags("pre", "b", "s", "u", "font", "p");
        whitelist.addAttributes("font", "size");
        whitelist.addAttributes("p", "class");

        // Clean the text while preserving whitespace.
        String cleanHtml = Jsoup.clean(xmlText, "", whitelist, outputSettings);

        // Remove the html escaping added by jsoup.
        cleanHtml = cleanHtml.replaceAll("&amp;", "&");
        cleanHtml = cleanHtml.replaceAll("&lt;", "<");
        cleanHtml = cleanHtml.replaceAll("&gt;", ">");
        return cleanHtml;
    }

    /**
     * Visitor class for Jsoup's depth first traversal.
     *
     * head method is called whenever entering into a new node.
     * tail is called when leaving a node.
     */
    private static class BillTextNodeVisitor implements NodeVisitor {

        private final List<TextDiff> diffs = new ArrayList<>();

        public List<TextDiff> getTextDiffs() {
            return diffs;
        }

        @Override
        public void head(Node node, int depth) {
            if (node instanceof TextNode) {
                Node parent = node.parent();
                String parentTagName = getParentTagName(node);
                String grandparentTag = getParentTagName(parent);

                if (parentTagName.equals("u") && grandparentTag.equals("b")) {
                    // Text node inside <B><U> tags
                    diffs.add(new TextDiff(TextDiffType.ADDED, ((TextNode) node).getWholeText()));
                }
                else if (parentTagName.equals("s") && grandparentTag.equals("b")) {
                    // Text node inside <B><S> tags
                    diffs.add(new TextDiff(TextDiffType.REMOVED, ((TextNode) node).getWholeText()));
                }
                else if (parentTagName.equals("b") && grandparentTag.equals("font")) {
                    // Text node inside <FONT><B> tags
                    diffs.add(new TextDiff(TextDiffType.HEADER, ((TextNode) node).getWholeText()));
                }
                else if (parentTagName.equals("b")) {
                    // Text node inside <B> tag
                    diffs.add(new TextDiff(TextDiffType.BOLD, ((TextNode) node).getWholeText()));
                }
                else {
                    diffs.add(new TextDiff(TextDiffType.UNCHANGED, ((TextNode) node).getWholeText()));
                }
            }
            else if (node instanceof Element el) {
                if (el.tagName().equals("p") && el.className().equals("brk")) {
                    // <p class="brk"> tags indicate the end of a page.
                    diffs.add(new TextDiff(TextDiffType.PAGE_BREAK, ""));
                }
            }
        }

        @Override
        public void tail(Node node, int depth) {}

        private String getParentTagName(Node node) {
            Node parent = node.parent();
            if (parent != null) {
                if (parent instanceof Element parentEl) {
                    return parentEl.tagName();
                }
            }
            return "";
        }
    }
}
