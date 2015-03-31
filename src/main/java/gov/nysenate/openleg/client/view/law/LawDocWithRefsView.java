package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.model.law.LawDocument;

import java.util.List;

public class LawDocWithRefsView extends LawDocView
{
    private List<String> parentLocationIds;

    public LawDocWithRefsView(LawDocument lawDocument, List<String> parentLocationIds) {
        super(lawDocument);
        this.parentLocationIds = parentLocationIds;
    }

    @Override
    public String getViewType() {
        return "law-document-with-parent-refs";
    }

    public List<String> getParentLocationIds() {
        return parentLocationIds;
    }
}
