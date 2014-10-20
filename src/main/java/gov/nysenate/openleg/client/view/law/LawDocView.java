package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.law.LawDocument;

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
