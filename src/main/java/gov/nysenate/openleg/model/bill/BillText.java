package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.util.BillTextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BillText implements Cloneable {

    private static final String HTML_STYLE = "<STYLE><!--u  {color: Green}s  {color: RED} i  {color: DARKBLUE; background-color:yellow}\n"+
            "p.brk {page-break-before:always}--></STYLE>\n";

    private String sobiPlainText;
    private List<TextDiff> diffs;

    public BillText() {
        this("", new ArrayList<>());
    }

    public BillText(String sobiPlainText) {
        this(sobiPlainText, new ArrayList<>());
    }

    public BillText(List<TextDiff> diffs) {
        this("", diffs);
    }

    public BillText(String sobiPlainText, List<TextDiff> diffs) {
        this.sobiPlainText = sobiPlainText == null ? "" : sobiPlainText;
        this.diffs = diffs == null ? new ArrayList<>() : diffs;
    }

    /**
     * Get the full bill text in the specified format.
     * <p>
     * PLAIN format slightly alters the bill headers adding space between characters and centering. (See {@link BillTextUtils#formatHtmlExtractedBillText(String)});
     * <p>
     * TextDiffs are required to create the HTML and TEMPLATE formats. TextDiffs are also used
     * to create the PLAIN format when they are available. If TextDiffs are not available (i.e. SOBI data),
     * the PLAIN format is taken straight from the SOBI text processor.
     *
     * @param format
     * @return
     */
    public String getFullText(BillTextFormat format) {
        String text = "";
        switch (format) {
            case PLAIN:
                text = createPlainText();
                text = BillTextUtils.formatHtmlExtractedBillText(text);
                break;
            case HTML:
                text = createHtmlText();
                break;
            case TEMPLATE:
                text = createTemplateText();
                break;
        }

        return text;
    }

    public List<TextDiff> getTextDiffs() {
        return this.diffs;
    }

    private String createPlainText() {
        StringBuilder plainText = new StringBuilder();
        if (diffs.isEmpty() && !sobiPlainText.isEmpty()) {
            plainText.append(sobiPlainText);
        }
        else {
            for (TextDiff diff : this.diffs) {
                plainText.append(diff.getPlainFormatText());
            }
        }
        return plainText.toString();
    }

    private String createHtmlText() {
        StringBuilder htmlText = new StringBuilder();
        if (hasDiffs()) {
            htmlText.append(HTML_STYLE);
            htmlText.append("<pre>");
            for (TextDiff diff : this.diffs) {
                htmlText.append(diff.getHtmlFormatText());
            }
            htmlText.append("</pre>");
        }
        return htmlText.toString();
    }

    private String createTemplateText() {
        StringBuilder templateText = new StringBuilder();
        if (hasDiffs()) {
            templateText.append("<pre class=\"ol-bill-text\">");
            for (TextDiff diff : this.diffs) {
                templateText.append(diff.getTemplateFormatText());
            }
            templateText.append("</pre>");
        }
        return templateText.toString();
    }

    private boolean hasDiffs() {
        return !diffs.isEmpty();
    }

    public BillText shallowClone() {
        try {
            return (BillText) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone bill text!");
        }
    }

    @Override
    public String toString() {
        return "BillText{" +
                "diffs=" + diffs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillText billText = (BillText) o;
        return Objects.equals(diffs, billText.diffs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diffs);
    }
}
