package gov.nysenate.openleg.model.law;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class LawTreeNode implements Comparable<LawTreeNode>
{
    /** This number indicates the order in which this node appears in the tree, starting at 1. */
    protected int sequenceNo;

    /** Reference to the law info which contains details about this node. */
    protected LawDocInfo lawDocInfo;

    /** Reference to the parent node, null if this is the chapter node. */
    protected LawTreeNode parent;

    /** Contains references to all the immediate children of this node. The key is the document id
     *  of the child node. */
    protected Map<String, LawTreeNode> children = new HashMap<>();

    /** --- Constructors --- */

    public LawTreeNode(LawDocInfo lawDocInfo, int sequenceNo) {
        if (lawDocInfo == null) {
            throw new IllegalArgumentException("Cannot instantiate LawTreeNode with a null LawDocInfo");
        }
        this.lawDocInfo = lawDocInfo;
        this.sequenceNo = sequenceNo;
    }

    /** --- Methods --- */

    public boolean isRootNode() {
        return this.lawDocInfo.getDocType().equals(LawDocumentType.CHAPTER);
    }

    public void addChild(LawTreeNode node) {
        if (node == null) throw new IllegalArgumentException("Cannot add a null child node ");
        node.setParent(this);
        children.put(node.lawDocInfo.documentId, node);
    }

    /**
     * Get a list of the children nodes ordered by sequence number.
     *
     * @return List<LawTreeNode>
     */
    public List<LawTreeNode> getChildNodeList() {
        return this.children.values().stream().sorted().collect(toList());
    }

    /**
     * Returns this node as well as all the descendants of this node in an ordered list.
     * This is a convenience method that creates the initial accumulator list before running
     * the recursive method.
     *
     * @return List<LawTreeNode>
     */
    public List<LawTreeNode> getAllNodes() {
        return getAllNodes(new ArrayList<>());
    }

    /**
     * Returns this node as well as all the descendants of this node in an ordered list.
     *
     * @param descNodes List<LawTreeNode> - Used to append nodes recursively.
     * @return List<LawTreeNode>
     */
    public List<LawTreeNode> getAllNodes(List<LawTreeNode> descNodes) {
        if (descNodes == null) throw new IllegalStateException("Node list is null");
        descNodes.add(this);
        getChildNodeList().forEach(n -> {
            n.getAllNodes(descNodes);
        });
        return descNodes;
    }

    /**
     * Recursively searches for a node that matches the given documentId.
     *
     * @param documentId
     * @return Optional<LawDocInfo> - Matched node or empty if it could not be found.
     */
    public Optional<LawDocInfo> find(String documentId) {
        Optional<LawDocInfo> docInfo = Optional.empty();
        if (this.getDocumentId().equals(documentId)) {
            docInfo = Optional.of(this.getLawDocInfo());
        }
        else if (children.containsKey(documentId)) {
            docInfo = Optional.of(children.get(documentId).getLawDocInfo());
        }
        else {
            for (LawTreeNode node : children.values()) {
                docInfo = node.find(documentId);
                if (docInfo.isPresent()) break;
            }
        }
        return docInfo;
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
        sb.append(this.lawDocInfo.toString());
        getChildNodeList().forEach(n -> {
            sb.append("\n").append(StringUtils.repeat("  |  ", level)).append(n.printTree(level + 1));
        });
        return sb.toString();
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "Law Tree Node [" + this.sequenceNo + "] " + this.lawDocInfo;
    }

    /** Compare the nodes simply by using the sequence number. */
    @Override
    public int compareTo(LawTreeNode o) {
        return Integer.compare(this.getSequenceNo(), o.getSequenceNo());
    }

    /** --- Delegates --- */

    public String getLawId() {
        return lawDocInfo.getLawId();
    }

    public LawDocumentType getDocType() {
        return lawDocInfo.getDocType();
    }

    public String getDocTypeId() {
        return lawDocInfo.getDocTypeId();
    }

    public LocalDate getPublishDate() {
        return lawDocInfo.getPublishedDate();
    }

    public String getDocumentId() {
        return lawDocInfo.getDocumentId();
    }

    public String getLocationId() {
        return lawDocInfo.getLocationId();
    }

    /** --- Basic Getters/Setters --- */

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public LawDocInfo getLawDocInfo() {
        return lawDocInfo;
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