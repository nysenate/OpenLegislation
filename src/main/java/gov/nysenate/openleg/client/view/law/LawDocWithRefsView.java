package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawTreeNode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LawDocWithRefsView extends LawDocView
{
    private List<String> parentLocationIds;
    private List<LawDocInfoView> parents;
    private LawDocInfoView prevSibling;
    private LawDocInfoView nextSibling;

    public LawDocWithRefsView(LawDocument lawDocument, Optional<LawTreeNode> lawTreeNode) {
        super(lawDocument);
        if (lawTreeNode.isPresent()) {
            this.parents = lawTreeNode.get()
                    .getAllParents().stream()
                    .map(n -> new LawDocInfoView(n.getLawDocInfo())).collect(Collectors.toList());
            this.parentLocationIds = this.parents.stream().map(p -> p.getLocationId()).collect(Collectors.toList());
            this.prevSibling = (lawTreeNode.get().getPrevSibling().isPresent())
                    ? new LawDocInfoView(lawTreeNode.get().getPrevSibling().get().getLawDocInfo()) : null;
            this.nextSibling = (lawTreeNode.get().getNextSibling().isPresent())
                    ? new LawDocInfoView(lawTreeNode.get().getNextSibling().get().getLawDocInfo()) : null;
        }
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

    public LawDocInfoView getPrevSibling() {
        return prevSibling;
    }

    public LawDocInfoView getNextSibling() {
        return nextSibling;
    }
}
