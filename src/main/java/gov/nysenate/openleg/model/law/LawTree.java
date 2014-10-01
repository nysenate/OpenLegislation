package gov.nysenate.openleg.model.law;

import java.time.LocalDate;

public class LawTree
{
    /** The 3 letter law id, e.g ABC, EDN, etc. */
    protected String lawId;

    /** The date on which this tree is valid. */
    protected LocalDate publishedDate;

    /** Reference to the root tree node (should be the chapter node) */
    protected LawTreeNode rootNode;

    /** --- Constructors --- */

    public LawTree(String lawId, LocalDate publishedDate, LawTreeNode rootNode) {
        this.lawId = lawId;
        this.publishedDate = publishedDate;
        this.rootNode = rootNode;
    }

    /** --- Basic Getters/Setters --- */

    public String getLawId() {
        return lawId;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public LawTreeNode getRootNode() {
        return rootNode;
    }
}