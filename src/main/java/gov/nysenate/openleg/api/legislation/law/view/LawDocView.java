package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.law.LawDocument;

public class LawDocView extends LawDocInfoView implements ViewObject
{
    protected String text;

    public LawDocView(LawDocument lawDocument) {
        super(lawDocument);
        if (lawDocument != null) {
            this.text = lawDocument.getText();
        }
    }

    @Override
    public String getViewType() {
        return "law-document";
    }

    public String getText() {
        return text;
    }
}
