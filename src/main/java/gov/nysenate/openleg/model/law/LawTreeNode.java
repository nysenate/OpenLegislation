package gov.nysenate.openleg.model.law;

import com.google.common.collect.Range;
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
    protected LinkedHashMap<String, LawTreeNode> children = new LinkedHashMap<>();

    /** Date when this law node was repealed, null if not repealed. */
    protected LocalDate repealedDate;

    /** Instance variable used to cache the section range once computed. */
    private Optional<Range<LawTreeNode>> sectionRange;

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
     * Returns a range of the sections that span the range of this node. For example if this is an article node,
     * this method will return the start and end sections contained under this article.
     *
     * Note: This method will recompute the range regardless of whether the 'sectionRange' instance variable is already set.
     * @return Optional<Range<LawTreeNode>>
     */
    public Optional<Range<LawTreeNode>> getSectionRange() {
        LawTreeNode start = findFirstSection(this);
        LawTreeNode end = findLastSection(this);
        if (start != null && end != null) {
            sectionRange = Optional.of(Range.closed(start, end));
        }
        else {
            sectionRange = Optional.empty();
        }
        return sectionRange;
    }

    /**
     * Gets the first immediate section, using the cached range if available.
     * @return Optional<LawTreeNode>
     */
    public Optional<LawTreeNode> getFromSection() {
        if (sectionRange == null) {
            sectionRange = getSectionRange();
        }
        if (sectionRange.isPresent()) {
            return Optional.of(sectionRange.get().lowerEndpoint());
        }
        return Optional.empty();
    }

    /**
     * Gets the last immediate section, using the cached range if available.
     * @return Optional<LawTreeNode>
     */
    public Optional<LawTreeNode> getToSection() {
        if (sectionRange == null) {
            sectionRange = getSectionRange();
        }
        if (sectionRange.isPresent()) {
            return Optional.of(sectionRange.get().upperEndpoint());
        }
        return Optional.empty();
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
        getChildNodeList().forEach(n -> n.getAllNodes(descNodes));
        return descNodes;
    }

    /**
     * Returns an optional containing the previous law tree node within the same level as this node.
     * @return Optional<LawTreeNode>
     */
    public Optional<LawTreeNode> getPrevSibling() {
        if (getParent() != null) {
            List<LawTreeNode> childNodeList = getParent().getChildNodeList();
            int index = childNodeList.indexOf(this);
            if (index > 0) {
                return Optional.of(childNodeList.get(index - 1));
            }
        }
        return Optional.empty();
    }

    /**
     * Returns an optional containing the next law tree node within the same level as this node.
     * @return Optional<LawTreeNode>
     */
    public Optional<LawTreeNode> getNextSibling() {
        if (getParent() != null) {
            List<LawTreeNode> childNodeList = getParent().getChildNodeList();
            int index = childNodeList.indexOf(this);
            if (index < childNodeList.size() - 1) {
                return Optional.of(childNodeList.get(index + 1));
            }
        }
        return Optional.empty();
    }

    /**
     * Return a list of all the parent nodes for this particular node.
     * @return LinkedList<LawTreeNode>
     */
    public LinkedList<LawTreeNode> getAllParents() {
        LinkedList<LawTreeNode> parents = new LinkedList<>();
        LawTreeNode lawTreeNode = this;
        while (lawTreeNode.getParent() != null) {
            lawTreeNode = lawTreeNode.getParent();
            parents.addFirst(lawTreeNode);
        }
        return parents;
    }

    /**
     * Recursively searches for a node that matches the given documentId and returns the law doc info.
     *
     * @param documentId String - Document id of the law document.
     * @return Optional<LawDocInfo> - Matched node or empty if it could not be found.
     */
    public Optional<LawDocInfo> find(String documentId) {
        Optional<LawTreeNode> lawTreeNode = findNode(documentId, false);
        return (lawTreeNode.isPresent()) ? Optional.of(lawTreeNode.get().getLawDocInfo()) : Optional.empty();
    }

    /**
     * Recursively searches for a child node that matches the given documentId or returns the current node
     * if it happens to match the docId. The delete param can be set to true to delete this node from the tree
     * by removing the reference from it's parent node.
     *
     * @param documentId String - Document id of the law document.
     * @param delete boolean - Set to true to delete the node and it's descendants from the tree.
     * @return Optional<LawDocInfo> - Matched node or empty if it could not be found.
     */
    public Optional<LawTreeNode> findNode(String documentId, boolean delete) {
        Optional<LawTreeNode> lawTreeNode = Optional.empty();
        if (this.getDocumentId().equals(documentId)) {
            lawTreeNode = Optional.of(this);
        }
        else if (children.containsKey(documentId)) {
            lawTreeNode = Optional.of(children.get(documentId));
        }
        else {
            for (LawTreeNode node : children.values()) {
                lawTreeNode = node.findNode(documentId, delete);
                if (lawTreeNode.isPresent()) break;
            }
        }
        if (delete && lawTreeNode.isPresent()) {
            LawTreeNode parentNode = lawTreeNode.get().getParent();
            if (parentNode != null) {
                parentNode.getChildren().remove(documentId);
            }
        }
        return lawTreeNode;
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

    /**
     * Finds the first valid section under this law node.
     * @param node LawTreeNode
     * @return LawTreeNode of the first immediate section (reference to self if this is a section node), or null if none.
     */
    private LawTreeNode findFirstSection(LawTreeNode node) {
        if (node.getDocType().equals(LawDocumentType.SECTION)) {
            return node;
        }
        LinkedList<LawTreeNode> children = new LinkedList<>(node.getChildren().values());
        LawTreeNode firstNode = null;
        while (!children.isEmpty() && firstNode == null) {
            firstNode = findFirstSection(children.getFirst());
            if (firstNode == null) {
                children.removeFirst();
            }
        }
        return firstNode;
    }

    /**
     * Finds the last valid section under this node.
     * @param node LawTreeNode
     * @return LawTreeNode of the last immediate section (reference to self if this is a section node), or null if none.
     */
    private LawTreeNode findLastSection(LawTreeNode node) {
        if (node.getDocType().equals(LawDocumentType.SECTION)) {
            return node;
        }
        LinkedList<LawTreeNode> children = new LinkedList<>(node.getChildren().values());
        LawTreeNode lastNode = null;
        while (!children.isEmpty() && lastNode == null) {
            lastNode = findLastSection(children.getLast());
            if (lastNode == null) {
                children.removeLast();
            }
        }
        return lastNode;
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

    public LinkedHashMap<String, LawTreeNode> getChildren() {
        return children;
    }

    public LocalDate getRepealedDate() {
        return repealedDate;
    }

    public void setRepealedDate(LocalDate repealedDate) {
        this.repealedDate = repealedDate;
    }
}