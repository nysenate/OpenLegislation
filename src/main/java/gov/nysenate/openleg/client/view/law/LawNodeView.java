package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.law.LawTreeNode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LawNodeView extends LawDocInfoView implements ViewObject
{
    protected int sequenceNo;
    protected ListView<LawNodeView> documents;

    public LawNodeView(LawTreeNode treeNode) {
        super((treeNode != null) ? treeNode.getLawDocInfo() : null);
        if (treeNode != null) {
            this.sequenceNo = treeNode.getSequenceNo();
            Map<String, LawNodeView> docMap = new LinkedHashMap<>();
            this.documents = ListView.of(
                    treeNode.getChildNodeList().stream().map(LawNodeView::new).collect(Collectors.toList()));
        }
    }

    @Override
    public String getViewType() {
        return "law-node";
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public ListView<LawNodeView> getDocuments() {
        return documents;
    }
}
