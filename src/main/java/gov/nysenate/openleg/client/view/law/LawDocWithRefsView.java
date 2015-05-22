package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawTreeNode;

import java.util.List;
import java.util.stream.Collectors;

public class LawDocWithRefsView extends LawDocView
{
    private List<String> parentLocationIds;
    private List<LawDocInfoView> parents;

    public LawDocWithRefsView(LawDocument lawDocument, List<LawTreeNode> parents) {
        super(lawDocument);
        this.parents = parents.stream()
            .map(p -> new LawDocInfoView(p.getLawDocInfo()))
            .collect(Collectors.toList());
        this.parentLocationIds = parents.stream()
            .map(p -> p.getLocationId())
            .collect(Collectors.toList());
    }

    @Override
    public String getViewType() {
        return "law-doc-info-detail";
    }

    public List<String> getParentLocationIds() {
        return parentLocationIds;
    }

    public List<LawDocInfoView> getParents() {
        return parents;
    }
}
