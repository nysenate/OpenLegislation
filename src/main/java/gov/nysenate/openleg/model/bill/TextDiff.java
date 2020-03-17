package gov.nysenate.openleg.model.bill;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TextDiff.text is the plain text
 * TextDiff.html is the escaped html (contains HTML escape sequences)
 */
public class TextDiff {

    /**
     * The type of text relative to the previous amendment's text.
     *
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
     * // TODO Use enum for this instead of string?
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

    protected String getPlainText() {
        String text = "";
        switch(this.type) {
            case 0:
                text = rawText();
                break;
            case 1:
                text = rawText().toUpperCase();
                break;
            case -1:
                text = "[" + rawText() + "]";
                break;
        }
        return text;
    }

    public String rawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
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
