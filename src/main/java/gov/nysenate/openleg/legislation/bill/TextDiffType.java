package gov.nysenate.openleg.legislation.bill;

import java.util.Arrays;
import java.util.List;

public enum TextDiffType {

    UNCHANGED(0, Arrays.asList(), "", ""),
    ADDED(1, Arrays.asList("ol-changed", "ol-added"), "<b><u>", "</u></b>"),
    REMOVED(-1, Arrays.asList("ol-changed", "ol-removed"), "<b><s>", "</s></b>"),
    HEADER(0, Arrays.asList("ol-header"), "<font size=5><b>", "</b></font>"),
    BOLD(0, Arrays.asList("ol-bold"), "<b>", "</b>"),
    PAGE_BREAK(0, Arrays.asList("ol-page-break"), "<p class=\"brk\">", "");

    /**
     * The type of text relative to the previous amendment's text.
     * <p>
     * if 0: This text is unchanged.
     * if 1: This text has been added.
     * if -1: This text was removed.
     */
    private int type;

    /**
     * List of css classes that describe how this text should be styled.
     */
    private List<String> templateCssClass;

    /**
     * Html tag used as a prefix for the html format.
     */
    private String htmlOpeningTags;

    /**
     * Html tag used as an ending for the html format.
     */
    private String htmlClosingTags;

    TextDiffType(int type, List<String> templateCssClass, String htmlOpeningTags, String htmlClosingTags) {
        this.type = type;
        this.templateCssClass = templateCssClass;
        this.htmlOpeningTags = htmlOpeningTags;
        this.htmlClosingTags = htmlClosingTags;
    }

    public int getType() {
        return type;
    }

    public List<String> getTemplateCssClass() {
        return templateCssClass;
    }

    public String getHtmlOpeningTags() {
        return htmlOpeningTags;
    }

    public String getHtmlClosingTags() {
        return htmlClosingTags;
    }
}
