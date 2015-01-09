package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawTreeNode;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LawNodeView extends LawDocInfoView implements ViewObject
{
    protected int sequenceNo;
    protected boolean isRepealed;
    protected LocalDate repealedDate;
    protected String text;  // Including the text here, can be null when just displaying structure.

    protected ListView<LawNodeView> documents;

    public LawNodeView(LawTreeNode treeNode) {
        super((treeNode != null) ? treeNode.getLawDocInfo() : null);
        if (treeNode != null) {
            this.sequenceNo = treeNode.getSequenceNo();
            this.repealedDate = treeNode.getRepealedDate();
            this.isRepealed = this.repealedDate != null;
            this.documents = ListView.of(
                treeNode.getChildNodeList().stream().map(LawNodeView::new).collect(Collectors.toList()));
        }
    }

    public LawNodeView(LawTreeNode treeNode, Map<String, LawDocument> docMap) {
        super((treeNode != null) ? treeNode.getLawDocInfo() : null);
        if (treeNode != null) {
            this.sequenceNo = treeNode.getSequenceNo();
            this.repealedDate = treeNode.getRepealedDate();
            this.isRepealed = this.repealedDate != null;
            this.text = docMap.get(treeNode.getDocumentId()).getText();
            this.documents = ListView.of(
                treeNode.getChildNodeList().stream().map(n -> new LawNodeView(n, docMap))
                        .collect(Collectors.toList()));
        }
    }

    @Override
    public String getViewType() {
        return "law-node";
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public boolean isRepealed() {
        return isRepealed;
    }

    public LocalDate getRepealedDate() {
        return repealedDate;
    }

    public String getText() {
        return text;
    }

    public ListView<LawNodeView> getDocuments() {
        return documents;
    }
}
