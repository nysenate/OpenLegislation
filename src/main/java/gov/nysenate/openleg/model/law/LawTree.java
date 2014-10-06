package gov.nysenate.openleg.model.law;

import java.time.LocalDate;

/**
 * Container for the root node that comprises the hierarchy of components within a law. Tree traversal methods
 * should be implemented on the LawTreeNode, so this class serves more as a container to represent a tree for a given
 * law at a given time.
 */
public class LawTree
{
    /** The identifier for this tree. */
    LawVersionId lawVersionId;

    /** Reference to the root tree node (should be the chapter node) */
    protected LawTreeNode rootNode;

    /** --- Constructors --- */

    public LawTree(LawVersionId lawVersionId, LawTreeNode rootNode) {
        if (lawVersionId == null) throw new IllegalArgumentException("Cannot construct a LawTree with a null lawVersionId");
        if (rootNode == null) throw new IllegalArgumentException("Cannot construct a LawTree with a null rootNode");
        this.lawVersionId = lawVersionId;
        this.rootNode = rootNode;
    }

    /** --- Delegates --- */

    public String getLawId() {
        return lawVersionId.getLawId();
    }

    public LocalDate getPublishedDate() {
        return lawVersionId.getPublishedDate();
    }

    /** --- Basic Getters/Setters --- */

    public LawVersionId getLawVersionId() {
        return lawVersionId;
    }

    public LawTreeNode getRootNode() {
        return rootNode;
    }
}