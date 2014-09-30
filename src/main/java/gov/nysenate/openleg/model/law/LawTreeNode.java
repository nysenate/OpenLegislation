package gov.nysenate.openleg.model.law;

import java.time.LocalDate;
import java.util.TreeMap;

public class LawTreeNode
{
    /** Reference to the law info which contains details about this node. */
    protected LawInfo lawInfo;

    /** Reference to the parent node, null if this is the chapter node. */
    protected LawTreeNode parent;

    /** Contains references to all the immediate children of this node. The key is the document id
     *  of the child node. */
    protected TreeMap<String, LawTreeNode> children = new TreeMap<>();

    /** --- Constructors --- */

    public LawTreeNode(LawInfo lawInfo) {
        if (lawInfo == null) {
            throw new IllegalArgumentException("Cannot instantiate LawTreeNode with a null LawInfo");
        }
        this.lawInfo = lawInfo;
    }

    /** --- Methods --- */

    public boolean isRootNode() {
        return this.lawInfo.getDocType().equals(LawDocumentType.CHAPTER);
    }

    public void addChild(LawTreeNode node) {
        if (node == null) throw new IllegalArgumentException("Cannot add a null child node ");
        node.setParent(this);
        children.put(node.lawInfo.documentId, node);
    }

    /** --- Delegates --- */

    public String getLawId() {
        return lawInfo.getLawId();
    }

    public LawDocumentType getDocType() {
        return lawInfo.getDocType();
    }

    public String getDocTypeId() {
        return lawInfo.getDocTypeId();
    }

    public LocalDate getPublishDate() {
        return lawInfo.getPublishDate();
    }

    public String getDocumentId() {
        return lawInfo.getDocumentId();
    }

    public String getLocationId() {
        return lawInfo.getLocationId();
    }

    /** --- Basic Getters/Setters --- */

    public LawInfo getLawInfo() {
        return lawInfo;
    }

    public LawTreeNode getParent() {
        return parent;
    }

    public void setParent(LawTreeNode parent) {
        this.parent = parent;
    }

    public TreeMap<String, LawTreeNode> getChildren() {
        return children;
    }

    public void setChildren(TreeMap<String, LawTreeNode> children) {
        this.children = children;
    }
}