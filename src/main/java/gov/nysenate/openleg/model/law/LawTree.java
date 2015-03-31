package gov.nysenate.openleg.model.law;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Container for the root node that comprises the hierarchy of components within a law. Tree traversal methods
 * should be implemented on the LawTreeNode, so this class serves more as a container to represent a tree for a given
 * law at a given time.
 */
public class LawTree
{
    /** The identifier for this tree. */
    protected LawVersionId lawVersionId;

    /** Information about the law. */
    protected LawInfo lawInfo;

    /** List of dates during which this tree was modified. */
    protected List<LocalDate> publishedDates;

    /** Reference to the root tree node (should be the chapter node) */
    protected LawTreeNode rootNode;

    /** Map of doc id to all nodes within this law tree. Necessary for quick lookup. */
    private Map<String, LawTreeNode> nodeLookupMap;

    /** --- Constructors --- */

    public LawTree(LawVersionId lawVersionId, LawTreeNode rootNode, LawInfo lawInfo) {
        if (lawVersionId == null) throw new IllegalArgumentException("Cannot construct a LawTree with a null lawVersionId");
        if (rootNode == null) throw new IllegalArgumentException("Cannot construct a LawTree with a null rootNode");
        if (lawInfo == null) throw new IllegalArgumentException("Cannot construct a LawTree with a null lawInfo");
        this.lawVersionId = lawVersionId;
        this.rootNode = rootNode;
        this.lawInfo = lawInfo;
        this.publishedDates = Arrays.asList(lawVersionId.getPublishedDate());
    }

    /** --- Method --- */

    public void rebuildLookupMap() {
        this.nodeLookupMap = this.rootNode.getAllNodes().stream()
            .collect(Collectors.toMap(LawTreeNode::getDocumentId, Function.identity()));
    }

    public Optional<LawTreeNode> find(String documentId) {
        if (this.nodeLookupMap == null) rebuildLookupMap();
        return Optional.ofNullable(this.nodeLookupMap.get(documentId));
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

    public LawInfo getLawInfo() {
        return lawInfo;
    }

    public List<LocalDate> getPublishedDates() {
        return publishedDates;
    }

    public void setPublishedDates(List<LocalDate> publishedDates) {
        this.publishedDates = publishedDates;
    }
}