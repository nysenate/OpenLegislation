package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class LawBuilderImpl
{
    private static final Logger logger = LoggerFactory.getLogger(LawBuilderImpl.class);

    /** Pattern used for parsing the location ids to extract the document type and doc type id. */
    protected static Pattern locationPattern = Pattern.compile("^(ST|SP|A|T|P|S|INDEX)(.*)");

    /** Pattern for certain chapter nodes that don't have the usual -CH pattern. */
    protected static Pattern specialChapterPattern = Pattern.compile("^(AS|ASSEMBLYRULES|SENATERULES)$");

    /** The location ids portions are prefixed with a code to indicate the different document types. */
    protected static Map<String, LawDocumentType> lawLevelCodes = new HashMap<>();
    static {
        lawLevelCodes.put("A", LawDocumentType.ARTICLE);
        lawLevelCodes.put("T", LawDocumentType.TITLE);
        lawLevelCodes.put("ST", LawDocumentType.SUBTITLE);
        lawLevelCodes.put("P", LawDocumentType.PART);
        lawLevelCodes.put("SP", LawDocumentType.SUB_PART);
        lawLevelCodes.put("S", LawDocumentType.SECTION);
        lawLevelCodes.put("INDEX", LawDocumentType.INDEX);
    }

    /** A law version id that is obtained from the law blocks. */
    protected LawVersionId lawVersionId;

    /** The root node in the law tree. */
    protected LawTreeNode rootNode = null;

    /** Basic Chapter info. */
    protected LawInfo lawInfo;

    /** Map of all the documents that need to be persisted. */
    protected Map<String, LawDocument> lawDocMap = new HashMap<>();

    /** Stack of the current parent nodes, used to determine hierarchy. */
    protected Stack<LawTreeNode> parentNodes = new Stack<>();

    /** A sequence number is used to maintain the order of the nodes. */
    protected int sequenceNo = 0;

    /** --- Constructors --- */

    public LawBuilderImpl(LawVersionId lawVersionId) {
        this.lawVersionId = lawVersionId;
    }

    public LawBuilderImpl(LawVersionId lawVersionId, LawTree previousTree) {
        this(lawVersionId);
        if (previousTree != null) {
            this.rootNode = previousTree.getRootNode();
            this.lawInfo = previousTree.getLawInfo();
        }
    }

    /** --- Methods --- */

    /**
     * Adds a LawDocument to the law tree and document map.
     *
     * @param block LawBlock - The LawBlock to convert into a document and store within the tree
     * @param isNewDoc boolean - Set to true if this is a new document and should be persisted.
     */
    public void addInitialBlock(LawBlock block, boolean isNewDoc) {
        final LawDocument lawDoc = new LawDocument(block);
        boolean isRootDoc = false;

        // For the initial law dumps, the first block that is processed for a law (usually) becomes the root node.
        if (rootNode == null) {
            logger.info("Processing root doc: {} for {} law.", lawDoc.getDocumentId(), lawDoc.getLawId());
            LawDocument chapterDoc;
            // If the block seems to be a chapter node, we'll treat this document as the root.
            if (isLikelyChapterDoc(lawDoc)) {
                lawDoc.setDocType(LawDocumentType.CHAPTER);
                lawDoc.setDocTypeId(lawDoc.getLocationId().replaceFirst("-CH", ""));
                chapterDoc = lawDoc;
                isRootDoc = true;
            }
            // Otherwise we have to create our own root node and process the current document as a child of it.
            else {
                chapterDoc = createRootDocument(block);
            }
            lawInfo = deriveLawInfo(chapterDoc.getLawId(), (isRootDoc) ? chapterDoc.getDocTypeId() : "");
            addRootDocument(chapterDoc, isNewDoc);
        }

        // If this block is not a root doc,
        if (!isRootDoc) {
            // Section docs are easy, since their location ids are simply numbers and they do not have any children.
            if (isLikelySectionDoc(lawDoc)) {
                logger.debug("Processing section {}", lawDoc.getDocumentId());
                lawDoc.setDocType(LawDocumentType.SECTION);
                lawDoc.setDocTypeId(lawDoc.getLocationId());
                if (isNewDoc) {
                    lawDocMap.put(lawDoc.getDocumentId(), lawDoc);
                }
                currParent().addChild(new LawTreeNode(lawDoc, ++sequenceNo));
            }
            else {
                String specificLocId = locateDocument(block, lawDoc);
                Matcher locMatcher = locationPattern.matcher(specificLocId);
                if (locMatcher.matches()) {
                    lawDoc.setDocType(lawLevelCodes.get(locMatcher.group(1)));
                    lawDoc.setDocTypeId(locMatcher.group(2));
                }
                else {
                    logger.warn("Failed to parse the following location {}. Setting as MISC type.", lawDoc.getDocumentId());
                    lawDoc.setDocType(LawDocumentType.MISC);
                    lawDoc.setDocTypeId(block.getLocationId());
                }
                addDocument(lawDoc, isNewDoc);
            }
        }

        // Set the title for the document
        lawDoc.setTitle(LawTitleParser.extractTitle(lawDoc, lawDoc.getText()));
    }

    /**
     * Updates a LawDocument within an existing tree or creates a new one if a master doc is received.
     *
     * @param block LawBlock - The LawBlock to convert into a document and store within the tree
     */
    public void addUpdateBlock(LawBlock block) {
        // Rebuild the law tree
        if (block.getMethod().equals("*MASTER*")) {
            rebuildTree(block.getText().toString());
        }
        // Repeal the document
        else if (block.getMethod().equals("*REPEAL*")) {
            Optional<LawTreeNode> node = rootNode.findNode(block.getDocumentId(), false);
            if (node.isPresent()) {
                logger.info("Repealing {}", block.getDocumentId());
                node.get().setRepealedDate(block.getPublishedDate());
            }
            else {
                logger.warn("Failed to repeal document {} because it could not be located within the law tree!");
            }
        }
        // Delete the document
        else if (block.getMethod().equals("*DELETE*")) {
            logger.info("Deleting {}", block.getDocumentId());
            rootNode.findNode(block.getDocumentId(), true);
        }
        // Update the document
        else if (block.getMethod().isEmpty()) {
            if (rootNode != null) {
                Optional<LawDocInfo> existingDocInfo = rootNode.find(block.getDocumentId());
                if (existingDocInfo.isPresent()) {
                    existingDocInfo.get().setPublishedDate(block.getPublishedDate());
                    LawDocument lawDoc = new LawDocument(existingDocInfo.get(), block.getText().toString());
                    // Re-parse the titles
                    lawDoc.setTitle(LawTitleParser.extractTitle(lawDoc, block.getText().toString()));
                    lawDocMap.put(lawDoc.getDocumentId(), lawDoc);
                    logger.info("Updated {}", lawDoc.getDocumentId());
                }
                else {
                    throw new LawParseException("Can't add law document " + block.getDocumentId() +
                                                " without a prior law tree structure including it.");
                }
            }
            else {
                throw new LawParseException("Can't add law document " + block.getDocumentId() + " without a prior law tree.");
            }
        }
        else {
            throw new LawParseException("Don't know how to handle law block updates with method: " + block.getMethod());
        }
    }

    /**
     * The master document has a line for each document id and indicates that the law tree needs to be restructured
     * accordingly. If an update was received for a particular doc id or a node existed in the previous law tree, we
     * will reuse those. Otherwise we have to create a blank law doc with the current published date.
     *
     * @param masterDoc String
     */
    public void rebuildTree(String masterDoc) {
        LawTreeNode priorRootNode = this.rootNode;
        this.rootNode = null;
        logger.info("Rebuilding tree for {} with master document.", this.lawVersionId.getLawId());
        for (String docId : StringUtils.split(masterDoc, "\\n")) {
            LawBlock block = new LawBlock();
            block.setDocumentId(docId);
            block.setLawId(docId.substring(0, 3));
            block.setLocationId(docId.substring(3));
            // Use published date from existing law doc if present
            if (lawDocMap.containsKey(docId)) {
                block.setPublishedDate(lawDocMap.get(docId).getPublishedDate());
                addInitialBlock(block, false);
                continue;
            }
            // Or from the previous tree node if set
            else if (priorRootNode != null) {
                Optional<LawDocInfo> existingDocInfo = priorRootNode.find(docId);
                if (existingDocInfo.isPresent()) {
                    block.setPublishedDate(existingDocInfo.get().getPublishedDate());
                    addInitialBlock(block, false);
                    continue;
                }
            }
            logger.info("New document id found in master document: {}", docId);
            block.setPublishedDate(this.lawVersionId.getPublishedDate());
            addInitialBlock(block, true);
        }
    }

    /**
     * Constructs a new LawTree with the documents processed for this law.
     *
     * @return LawTree
     */
    public LawTree getProcessedLawTree() {
        return new LawTree(lawVersionId, rootNode, lawInfo);
    }

    /**
     * Return all the processed law documents as a list in NO particular order.
     *
     * @return List<LawDocument>
     */
    public List<LawDocument> getProcessedLawDocuments() {
        return lawDocMap.values().stream().collect(toList());
    }

    /**
     * We determine the position of a node based on whether any of the nodes in the parent stack has a location id
     * that prefixes the current document's location id. The specific location id (the matched prefix from the parent
     * document removed) is returned.
     *
     * For example given the locationId 'A2P1SP3', we will pop the parent stack until we find it's parent 'A2P1'
     * or reach the root node. The new portion 'SP3' would be the returned value which serves as the docTypeId.
     *
     * @param block LawBlock
     * @param lawDoc LawDocument
     * @return String
     */
    private String locateDocument(LawBlock block, LawDocument lawDoc) {
        String docTypeId = lawDoc.getLocationId();
        while (!currParent().isRootNode()) {
            if (StringUtils.startsWith(block.getLocationId(), currParent().getLocationId())) {
                String trimLocId = StringUtils.removeStart(block.getLocationId(), currParent().getLocationId());
                if (locationPattern.matcher(trimLocId).matches()) {
                    docTypeId = trimLocId;
                    break;
                }
            }
            parentNodes.pop();
        }
        return docTypeId;
    }

    /**
     * Add the root document which does not have to be associated with a parent.
     *
     * @param rootDoc LawDocument
     * @param isNewDoc boolean - Set to true if this is a new document and should be persisted.
     */
    private void addRootDocument(LawDocument rootDoc, boolean isNewDoc) {
        if (rootDoc == null) throw new IllegalArgumentException("Root document cannot be null!");
        sequenceNo = 0;
        rootNode = new LawTreeNode(rootDoc, ++sequenceNo);
        if (isNewDoc) {
            lawDocMap.put(rootDoc.getDocumentId(), rootDoc);
        }
        parentNodes.push(this.rootNode);
    }

    /**
     * Add the document by associating it as a child of the current parent node and subsequently setting the
     * current parent node to point to this document.
     *
     * @param lawDoc LawDocument
     * @param isNewDoc boolean - Set to true if this is a new document and should be persisted.
     */
    private void addDocument(LawDocument lawDoc, boolean isNewDoc) {
        if (parentNodes.empty()) {
            throw new IllegalStateException("Failed to add node because it's parent node was not added!");
        }
        if (isNewDoc) {
            lawDocMap.put(lawDoc.getDocumentId(), lawDoc);
        }
        LawTreeNode node = new LawTreeNode(lawDoc, ++sequenceNo);
        currParent().addChild(node);
        parentNodes.push(node);
    }

    /**
     * Constructs the LawInfo based on the LawChapterType mapping.
     *
     * @param lawId String
     * @param chapterId String
     * @return LawInfo
     */
    private LawInfo deriveLawInfo(String lawId, String chapterId) {
        LawInfo chapter = new LawInfo();
        chapter.setLawId(lawId);
        chapter.setChapterId(chapterId);
        try {
            LawChapterCode chapterType = LawChapterCode.valueOf(lawId);
            chapter.setName(chapterType.getName());
            chapter.setType(chapterType.getType());
        }
        catch (IllegalArgumentException ex) {
            chapter.setName("");
            chapter.setType(LawType.MISC);
        }
        return chapter;
    }

    /**
     * Indicates if the document is potentially a chapter node. Consolidated laws will typically begin with a -CH
     * which is not a problem, but some unconsolidated laws have the year or in some cases start right with the
     * section or article. We're checking to make sure those cases do not exist for this block.
     *
     * @param doc LawDocument
     * @return boolean
     */
    private boolean isLikelyChapterDoc(LawDocument doc) {
        return (doc.getLocationId().startsWith("-CH") || specialChapterPattern.matcher(doc.getLocationId()).matches() ||
                (!doc.getLocationId().equals("1") && !locationPattern.matcher(doc.getLocationId()).matches()));
    }

    /**
     * Section documents typically just have a location id with the number of the section (except in the constitution).
     * All other document types start with a character or symbol.
     *
     * @param lawDoc LawDocument
     * @return boolean - true if this block is most likely a section
     */
    private boolean isLikelySectionDoc(LawDocument lawDoc) {
        return Character.isDigit(lawDoc.getLocationId().charAt(0));
    }

    /** Convenience method to peek at the parent node stack which holds the current parent. */
    private LawTreeNode currParent() {
        return parentNodes.peek();
    }

    /**
     * Create our own root law doc to serve as the root document in the event that we don't receive a top level doc
     * from the dumps. This is common for unconsolidated laws where they just start with the first section or article.
     *
     * @param block LawBlock
     */
    private LawDocument createRootDocument(LawBlock block) {
        LawDocument dummyParent = new LawDocument();
        dummyParent.setLawId(block.getLawId());
        dummyParent.setDocumentId(block.getLawId() + "-ROOT");
        dummyParent.setLocationId("-ROOT");
        dummyParent.setDocType(LawDocumentType.CHAPTER);
        dummyParent.setDocTypeId("ROOT");
        dummyParent.setPublishedDate(block.getPublishedDate());
        dummyParent.setText("");
        dummyParent.setTitle(LawTitleParser.extractTitleFromChapter(dummyParent));
        return dummyParent;
    }
}
