package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.util.BillTextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BillText implements Cloneable {

    private static final String HTML_STYLE = "<STYLE><!--U  {color: Green}S  {color: RED} I  {color: DARKBLUE; background-color:yellow}\n"+
            "P.brk {page-break-before:always}--></STYLE>\n";

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
        this.sobiPlainText = sobiPlainText;
        this.diffs = diffs;
    }

    /**
     * Checks if the text has been loaded into this BillText.
     * @return
     */
    public boolean isLoaded() {
        return !(diffs.isEmpty() && sobiPlainText.isEmpty());
    }

    public String getFullText(BillTextFormat format) {
        String text = "";
        switch (format) {
            case PLAIN:
                text = getPlainText();
                text = BillTextUtils.formatHtmlExtractedBillText(text);
                break;
            case HTML:
                text = getHtmlText();
                break;
            case TEMPLATE:
                // TODO
                break;
            case DIFF:
                // TODO
                break;
        }

        return text;
    }

    public List<TextDiff> getTextDiffs() {
        return this.diffs;
    }

    private String getPlainText() {
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

    private String getHtmlText() {
        StringBuilder htmlText = new StringBuilder();
        htmlText.append(HTML_STYLE);
        htmlText.append("<PRE>");
        for (TextDiff diff : this.diffs) {
            htmlText.append(diff.getHtmlFormatText());
        }
        htmlText.append("</PRE>");
        return htmlText.toString();
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
