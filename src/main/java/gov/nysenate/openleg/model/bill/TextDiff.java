package gov.nysenate.openleg.model.bill;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextDiff {

    /**
     * The type of text relative to the previous amendment's text.
     * <p>
     * if 0: This text is unchanged.
     * if 1: This text has been added.
     * if -1: This text was removed.
     */
    private int type;

    /**
     * The raw text data.
     */
    private String rawText;

    /**
     * List of css classes that describe how this text should be styled.
     */
    private List<String> cssClasses;

    public TextDiff(int type, String rawText) {
        this(type, rawText, new ArrayList<>());
    }

    public TextDiff(int type, String rawText, List<String> cssClasses) {
        this.type = type;
        this.rawText = rawText;
        this.cssClasses = cssClasses;
    }

    /**
     * Converts this text diff into the plain text format.
     *
     * @return
     */
    protected String getPlainText() {
        String text = "";
        switch (this.type) {
            case 0:
                text = getRawText();
                break;
            case 1:
                text = getRawText().toUpperCase();
                break;
            case -1:
                text = getRawText();
                break;
        }
        return text;
    }

    /**
     * Converts this text diff into the html format.
     *
     * @return
     */
    protected String getHtmlText() {
        if (cssClasses.isEmpty()) {
            return getRawText();
        }

        StringBuilder text = new StringBuilder();
        text.append("<span class=\"");
        for (int i = 0; i < cssClasses.size(); i++) {
            if (i != 0) {
                text.append(" ");
            }
            text.append(cssClasses.get(i));
        }

        text.append("\">")
                .append(getRawText())
                .append("</span>");
        return text.toString();
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public List<String> getCssClasses() {
        return cssClasses;
    }

    @Override
    public String toString() {
        return "TextDiff{" +
                "type=" + type +
                ", text='" + rawText + '\'' +
                ", cssClasses=" + cssClasses +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextDiff textDiff = (TextDiff) o;
        return type == textDiff.type &&
                Objects.equals(rawText, textDiff.rawText) &&
                Objects.equals(cssClasses, textDiff.cssClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, rawText, cssClasses);
    }
}
