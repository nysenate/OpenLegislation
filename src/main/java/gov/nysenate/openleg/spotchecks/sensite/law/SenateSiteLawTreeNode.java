package gov.nysenate.openleg.spotchecks.sensite.law;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.legislation.law.LawTree;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.TreeSet;

/**
 * A simplified {@link LawTree} used to store NYSenate.gov law tree data from a node dump.
 */
public class SenateSiteLawTreeNode implements Comparable<SenateSiteLawTreeNode> {

    private final int sequenceNo;
    private final String lawId;
    private final String docId;

    private SenateSiteLawTreeNode parent = null;
    private TreeSet<SenateSiteLawTreeNode> children = new TreeSet<>();

    public SenateSiteLawTreeNode(int sequenceNo, String lawId, String docId) {
        this.sequenceNo = sequenceNo;
        this.lawId = lawId;
        this.docId = docId;
    }

    @Override
    public int compareTo(SenateSiteLawTreeNode o) {
        return Integer.compare(this.sequenceNo, o.sequenceNo);
    }

    void addChild(@Nonnull SenateSiteLawTreeNode child) {
        child.parent = this;
        children.add(child);
    }

    public ImmutableList<SenateSiteLawTreeNode> getChildren() {
        return ImmutableList.copyOf(children);
    }

    public SenateSiteLawTreeNode getPrevSibling() {
        return Optional.ofNullable(parent)
                .map(p -> p.children.lower(this))
                .orElse(null);
    }

    public SenateSiteLawTreeNode getNextSibling() {
        return Optional.ofNullable(parent)
                .map(p -> p.children.higher(this))
                .orElse(null);
    }

    /* --- Getters --- */

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getLawId() {
        return lawId;
    }

    public String getDocId() {
        return docId;
    }

    public SenateSiteLawTreeNode getParent() {
        return parent;
    }
}
