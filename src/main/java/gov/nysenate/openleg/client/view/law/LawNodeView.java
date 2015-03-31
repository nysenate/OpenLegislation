package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawTreeNode;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LawNodeView extends LawDocInfoView implements ViewObject
{
    protected int sequenceNo;
    protected boolean isRepealed;
    protected LocalDate repealedDate;
    protected String fromSection;
    protected String toSection;
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
                ? docMap.get(treeNode.getDocumentId()).getText()
                : null;
            if (depth == null || depth > 0) {
                final Integer childDepth = (depth != null) ? depth - 1 : null;
                this.documents = ListView.of(
                        treeNode.getChildNodeList().stream()
                                .map(n -> new LawNodeView(n, childDepth, docMap))
                                .collect(Collectors.toList()));
            }
        }
    }

    private void initFromLawTreeNode(LawTreeNode treeNode) {
        this.sequenceNo = treeNode.getSequenceNo();
        this.repealedDate = treeNode.getRepealedDate();
        this.isRepealed = this.repealedDate != null;
        Optional<LawTreeNode> fromSection = treeNode.getFromSection();
        Optional<LawTreeNode> toSection = treeNode.getToSection();
        this.fromSection = (fromSection.isPresent()) ? fromSection.get().getLocationId() : null;
        this.toSection = (toSection.isPresent()) ? toSection.get().getLocationId() : null;
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
