package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawTreeNode;
import gov.nysenate.openleg.processor.law.LawBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LawBuilder
{
    protected LawTreeNode rootNode = null;

    protected Map<String, LawDocument> lawDocMap = new HashMap<>();

    protected Stack<LawTreeNode> parentNodeStack = new Stack<>();

    protected int sequenceNo = 0;

    /** --- Constructors --- */

    public void addRootDocument(LawDocument rootDoc) {
        if (rootDoc == null) throw new IllegalArgumentException("Root document cannot be null!");
        rootNode = new LawTreeNode(rootDoc, ++sequenceNo);
        lawDocMap.put(rootDoc.getDocumentId(), rootDoc);
        parentNodeStack.push(this.rootNode);
    }

    public void addNode(LawDocument lawDoc) {
        if (parentNodeStack.empty()) {
            throw new IllegalStateException("Failed to add node because it's parent node was not added!");
        }
        lawDocMap.put(lawDoc.getDocumentId(), lawDoc);
        parentNodeStack.peek().addChild(new LawTreeNode(lawDoc, ++sequenceNo));
    }

    /**
     * Section documents typically just have a location id with the number of the section (except in the constitution).
     * All other document types start with a character or symbol.
     *
     * @param lawDoc LawDocument
     * @return boolean - true if this block is most likely a section
     */
    protected boolean isLikelySectionDoc(LawDocument lawDoc) {
        return Character.isDigit(lawDoc.getLocationId().charAt(0));
    }



}
