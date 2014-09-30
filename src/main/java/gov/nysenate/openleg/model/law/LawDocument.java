package gov.nysenate.openleg.model.law;

public class LawDocument extends LawInfo
{
    protected String text;

    /** --- Constructors --- */

    public LawDocument() {}

    /** --- Basic Getters/Setters --- */

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}