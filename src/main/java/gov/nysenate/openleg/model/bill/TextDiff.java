package gov.nysenate.openleg.model.bill;

import java.util.List;
import java.util.Objects;

public class TextDiff {

    private TextDiffType type;

    /**
     * The raw text data.
     */
    private String rawText;


    public TextDiff(TextDiffType type, String rawText) {
        this.type = type;
        this.rawText = rawText;
    }

    /**
     * Converts this text diff into the plain text format.
     *
     * @return
     */
    protected String getPlainFormatText() {
        String text = "";
        switch (this.type.getType()) {
            case 0:
                text = getText();
                break;
            case 1:
                text = getText().toUpperCase();
                break;
            case -1:
                text = getText();
                break;
        }
        return text;
    }

    /**
     * Converts this text diff into html format.
     * @return
     */
    protected String getHtmlFormatText() {
        return type.getHtmlOpeningTags() + getText() + type.getHtmlClosingTags();
    }

    /**
     * Converts this text diff into the template format.
     *
     * @return
     */
    protected String getTemplateFormatText() {
        if (type.getTemplateCssClass().isEmpty()) {
            return getText();
        }

        StringBuilder text = new StringBuilder();
        text.append("<span class=\"");
        for (int i = 0; i < type.getTemplateCssClass().size(); i++) {
            if (i != 0) {
                text.append(" ");
            }
            text.append(type.getTemplateCssClass().get(i));
        }

        text.append("\">")
                .append(getText())
                .append("</span>");
        return text.toString();
    }

    public String getText() {
        return rawText;
    }

    public void setText(String text) {
        this.rawText = text;
    }

    public List<String> getCssClasses() {
        return type.getTemplateCssClass();
    }

    public TextDiffType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "TextDiff{" +
                "type=" + type +
                ", rawText='" + rawText + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextDiff textDiff = (TextDiff) o;
        return type == textDiff.type &&
                Objects.equals(rawText, textDiff.rawText);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, rawText);
    }
}
