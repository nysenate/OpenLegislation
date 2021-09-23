package gov.nysenate.openleg.spotchecks.sensite.law;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.api.legislation.law.view.LawNodeView;
import gov.nysenate.openleg.api.legislation.law.view.LawTreeView;
import gov.nysenate.openleg.legislation.law.LawTree;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReferenceId;

import java.util.*;

/**
 * Represents the structure of a law chapter as given by a nysenate.gov node dump.
 *
 * It is also possible to convert a {@link LawTree} into this form for easier comparison.
 */
public class SenateSiteLawTree {

    private SpotCheckReferenceId referenceId;
    private String chapterId;
    private SenateSiteLawTreeNode rootNode = null;
    private List<SenateSiteLawTreeNode> orphanNodes = new ArrayList<>();
    private Map<String, SenateSiteLawTreeNode> lookupMap = new HashMap<>();

    public SenateSiteLawTree(String chapterId,
                             Collection<SenateSiteLawDoc> lawDocs,
                             SpotCheckReferenceId referenceId) {
        this.chapterId = chapterId;
        this.referenceId = referenceId;
        for (SenateSiteLawDoc doc : lawDocs) {
            SenateSiteLawTreeNode node = new SenateSiteLawTreeNode(
                    doc.getSequenceNo(), doc.getLawId(), doc.getStatuteId());
            addNode(doc.getParentStatuteId(), node);
        }
    }

    public SenateSiteLawTree(LawTreeView lawTree) {
        this.chapterId = lawTree.getInfo().getLawId();
        addLawNodeView(lawTree.getDocuments(), null);
    }

    /**
     * Adds the given {@link LawNodeView} and all children to the law tree in depth-first order.
     */
    private void addLawNodeView(LawNodeView nodeView, LawNodeView parent) {
        if (nodeView == null) {
            return;
        }
        SenateSiteLawTreeNode ssLawNode = new SenateSiteLawTreeNode(
                nodeView.getSequenceNo(), nodeView.getLawId(), getDocId(nodeView));
        String parentDocId = getDocId(parent);
        addNode(parentDocId, ssLawNode);
        if (nodeView.getDocuments() != null) {
            nodeView.getDocuments().getItems().forEach(child -> addLawNodeView(child, nodeView));
        }
    }

    private String getDocId(LawNodeView lawNodeView) {
        if (lawNodeView == null) {
            return null;
        }
        return lawNodeView.getLawId() + lawNodeView.getLocationId();
    }

    private void addNode(String parentDocId, SenateSiteLawTreeNode node) {
        // Treat nodes with null or dup. id as orphans without adding them to lookup map.
        if (node.getDocId() == null || lookupMap.containsKey(node.getDocId())) {
            orphanNodes.add(node);
            return;
        }
        if (parentDocId == null || !lookupMap.containsKey(parentDocId)) {
            // If the parent is null/not found, it could be the root, or an orphan.
            if (rootNode == null) {
                rootNode = node;
            } else {
                orphanNodes.add(node);
            }
        } else {
            // If the parent is found, add the node as a child.
            SenateSiteLawTreeNode parent = lookupMap.get(parentDocId);
            parent.addChild(node);
        }
        lookupMap.put(node.getDocId(), node);
    }

    /* --- Getters --- */

    public String getChapterId() {
        return chapterId;
    }

    public SenateSiteLawTreeNode getRootNode() {
        return rootNode;
    }

    public ImmutableList<SenateSiteLawTreeNode> getOrphanNodes() {
        return ImmutableList.copyOf(orphanNodes);
    }

    public SpotCheckReferenceId getReferenceId() {
        return referenceId;
    }
}
