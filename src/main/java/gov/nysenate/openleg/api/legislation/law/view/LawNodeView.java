package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawTreeNode;

import java.time.LocalDate;
import java.util.Map;

public class LawNodeView extends LawDocInfoView implements ViewObject
{
    protected int sequenceNo;
    protected boolean isRepealed;
    protected LocalDate repealedDate;
    protected String fromSection = null;
    protected String toSection = null;
    protected String text;  // Can be null when just displaying structure.

    protected ListView<LawNodeView> documents;

    public LawNodeView(LawTreeNode treeNode, Integer depth) {
        this(treeNode, depth, null);
    }

    public LawNodeView(LawTreeNode treeNode, Integer depth, Map<String, LawDocument> docMap) {
        super((treeNode != null) ? treeNode.getLawDocInfo() : null);
        if (treeNode != null) {
            initFromLawTreeNode(treeNode);
            this.text = (docMap != null && docMap.containsKey(treeNode.getDocumentId()))
                ? docMap.get(treeNode.getDocumentId()).getText() : null;
            if (depth == null || depth > 0) {
                final Integer childDepth = (depth != null) ? depth - 1 : null;
                this.documents = ListView.of(
                        treeNode.getChildNodeList().stream()
                                .map(n -> new LawNodeView(n, childDepth, docMap))
                                .toList());
            }
        }
    }

    private void initFromLawTreeNode(LawTreeNode treeNode) {
        this.sequenceNo = treeNode.getSequenceNo();
        this.repealedDate = treeNode.getRepealedDate();
        this.isRepealed = this.repealedDate != null;
        treeNode.getFromSection().ifPresent(f -> this.fromSection = f.getLawDocInfo().getDocTypeId());
        treeNode.getToSection().ifPresent(t -> this.toSection = t.getLawDocInfo().getDocTypeId());
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

    public String getFromSection() {
        return fromSection;
    }

    public String getToSection() {
        return toSection;
    }

    public ListView<LawNodeView> getDocuments() {
        return documents;
    }
}
