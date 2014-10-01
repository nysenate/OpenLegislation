package gov.nysenate.openleg.model.law;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class LawTreeNode implements Comparable<LawTreeNode>
{
    /** This number indicates the order in which this node appears in the tree, starting at 1. */
    protected int sequenceNo;

    /** Reference to the law info which contains details about this node. */
    protected LawInfo lawInfo;

    /** Reference to the parent node, null if this is the chapter node. */
    protected LawTreeNode parent;

    /** Contains references to all the immediate children of this node. The key is the document id
     *  of the child node. */
    protected Map<String, LawTreeNode> children = new HashMap<>();

    /** --- Constructors --- */

    public LawTreeNode(LawInfo lawInfo, int sequenceNo) {
        if (lawInfo == null) {
            throw new IllegalArgumentException("Cannot instantiate LawTreeNode with a null LawInfo");
        }
        this.lawInfo = lawInfo;
        this.sequenceNo = sequenceNo;
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

    /**
     * Get a list of the children nodes ordered by sequence number.
     *
     * @return List<LawTreeNode>
     */
    public List<LawTreeNode> getOrderedNodeList() {
        return this.children.values().stream().sorted().collect(toList());
    }

    /**
     * Prints out this tree with formatting to show the hierarchy.
     *
     * @return String
     */
    public String printTree() {
        return printTree(1);
    }

    /**
     * Recursively print out this tree with formatting to show the hierarchy.
     *
     * @param level int - Number to indicate the nesting level.
     * @return String
     */
    public String printTree(int level) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.lawInfo.toString());
        getOrderedNodeList().forEach(n -> {
            sb.append("\n").append(StringUtils.repeat("  |  ", level)).append(n.printTree(level + 1));
        });
        return sb.toString();
    }

    /** --- Overrides --- */

    /** Compare the nodes simply by using the sequence number. */
    @Override
    public int compareTo(LawTreeNode o) {
        return Integer.compare(this.getSequenceNo(), o.getSequenceNo());
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

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public LawInfo getLawInfo() {
        return lawInfo;
    }

    public LawTreeNode getParent() {
        return parent;
    }

    public void setParent(LawTreeNode parent) {
        this.parent = parent;
    }

    public Map<String, LawTreeNode> getChildren() {
        return children;
    }

    public void setChildren(TreeMap<String, LawTreeNode> children) {
        this.children = children;
    }
}