package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawTree;

import java.util.List;

public interface LawBuilder
{
    /**
     * Adds a LawDocument to the law tree and document map.
     *
     * @param block LawBlock - The LawBlock to convert into a document and store within the tree
     * @param isNewDoc boolean - Set to true if this is a new document and should be persisted.
     */
    void addInitialBlock(LawBlock block, boolean isNewDoc);

    /**
     * Updates a LawDocument within an existing tree or creates a new one if a master doc is received.
     *
     * @param block LawBlock - The LawBlock to convert into a document and store within the tree
     */
    void addUpdateBlock(LawBlock block);

    /**
     * The master document has a line for each document id and indicates that the law tree needs to be restructured
     * accordingly. If an update was received for a particular doc id or a node existed in the previous law tree, we
     * will reuse those. Otherwise we have to create a blank law doc with the current published date.
     *
     * @param masterDoc String
     */
    void rebuildTree(String masterDoc);

    /**
     * Constructs a new LawTree with the documents processed for this law.
     *
     * @return LawTree
     */
    LawTree getProcessedLawTree();

    /**
     * Return all the processed law documents as a list in NO particular order.
     *
     * @return List<LawDocument>
     */
    List<LawDocument> getProcessedLawDocuments();
}