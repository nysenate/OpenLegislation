package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawTreeNode;
import gov.nysenate.openleg.processor.law.LawBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LawBuilder
{
    private static final Logger logger = LoggerFactory.getLogger(LawBuilder.class);

    protected String lawId;

    protected LocalDate publishedDate;

    protected LawTreeNode rootNode = null;

    protected Map<String, LawDocument> lawDocMap = new HashMap<>();

    protected Stack<LawTreeNode> parentNodeStack = new Stack<>();

    protected int sequenceNo = 0;

    /** --- Constructors --- */

    public LawBuilder(String lawId, LocalDate publishedDate) {
        this.lawId = lawId;
        this.publishedDate = publishedDate;
    }

    /** --- Methods --- */

    /**
     *
     *
     * @param rootDoc
     */
    public void addRootDocument(LawDocument rootDoc) {
        if (rootDoc == null) throw new IllegalArgumentException("Root document cannot be null!");
        rootNode = new LawTreeNode(rootDoc, ++sequenceNo);
        lawDocMap.put(rootDoc.getDocumentId(), rootDoc);
        parentNodeStack.push(this.rootNode);
    }

    /**
     *
     *
     * @param lawDoc
     */
    public void addNode(LawDocument lawDoc) {
        if (parentNodeStack.empty()) {
            throw new IllegalStateException("Failed to add node because it's parent node was not added!");
        }
        lawDocMap.put(lawDoc.getDocumentId(), lawDoc);
        parentNodeStack.peek().addChild(new LawTreeNode(lawDoc, ++sequenceNo));
    }

    /**
     *
     * @param masterDoc
     */
    public void rebuildTree(String masterDoc) {
        for (String docId : masterDoc.split("\\r?\\n")) {

        }
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
