package gov.nysenate.openleg.api;

public class StringView implements ViewObject
{
    protected String text;

    public StringView(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "string-view";
    }
}
