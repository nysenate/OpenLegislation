package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.law.LawTreeNode;

import java.util.LinkedHashMap;
import java.util.Map;

public class LawNodeView extends LawDocInfoView implements ViewObject
{
    protected int sequenceNo;
    protected MapView<String, LawNodeView> documents;

    public LawNodeView(LawTreeNode treeNode) {
        super((treeNode != null) ? treeNode.getLawDocInfo() : null);
        if (treeNode != null) {
            this.sequenceNo = treeNode.getSequenceNo();
            Map<String, LawNodeView> docMap = new LinkedHashMap<>();
            for (LawTreeNode node : treeNode.getChildNodeList()) {
                docMap.put(node.getLocationId(), new LawNodeView(node));
            }
            this.documents = MapView.of(docMap);
        }
    }

    @Override
    public String getViewType() {
        return "law-node";
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public MapView<String, LawNodeView> getDocuments() {
        return documents;
    }
}
