package gov.nysenate.openleg.processors.law;

import gov.nysenate.openleg.legislation.law.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.legislation.law.LawChapterCode.*;
import static gov.nysenate.openleg.legislation.law.LawDocumentType.*;

public abstract class AbstractLawBuilder implements LawBuilder {
    private static final Logger logger = LoggerFactory.getLogger(AbstractLawBuilder.class);

    /** Pattern used for parsing the location ids to extract the document type and doc type id. */
    protected static final Pattern LOCATION_PATTERN = Pattern.compile("^(JR|ST|SP|SA|A|T|P|S|R|INDEX)(.+)");

    /** Pattern for certain chapter nodes that don't have the usual -CH pattern. */
    private static final Pattern SPECIAL_CHAPTER_PATTERN = Pattern.compile("^(AS|ASSEMBLYRULES|SENATERULES)$");

    /** String for city personal income tax on residents, an odd clause in the GCT law. */
    protected static final String CITY_TAX_STR = GCT.name() + "25-A";

    private static final String ROOT = "ROOT",
    /** Document ID for special document that's just a list of notwithstanding clauses. */
    ATTN = ACA.name() + "ATTN",

    /** Document ID for special tax law. */
    CUBIT = GCM.name() + "CUBIT",

    /** Location Id for Constitution Preamble. */
    PREAMBLE_LOC_ID = "AA1",

    /** Special law IDs. */
    A_RULES = CMA.name(), S_RULES = CMS.name();
    protected static final String CONS_STR = CNS.name();

    /** Hints about the law hierarchy for certain laws that have inconsistent doc id naming. */
    private static final Map<String, List<LawDocumentType>> EXPECTED_LAW_ORDERING = Map.of(
            EDN.name(), List.of(TITLE, ARTICLE, SUBARTICLE, PART, SUBPART), CPL.name(), List.of(PART, TITLE, ARTICLE));

    /** The location ids portions are prefixed with a code to indicate the different document types. */
    protected static Map<String, LawDocumentType> lawLevelCodes = Map.of("A", ARTICLE, "SA", SUBARTICLE, "T", TITLE,
            "ST", SUBTITLE, "P", PART, "SP", SUBPART, "S", SECTION, "INDEX", INDEX, "R", RULE,
            "JR", JOINT_RULE);

    /** A law version id that is obtained from the law blocks. */
    private final LawVersionId lawVersionId;

    /** The root node in the law tree. */
    protected LawTreeNode rootNode = null;

    /** Basic Chapter info. */
    protected LawInfo lawInfo;

    /** Map of all the documents that need to be persisted. */
    protected Map<String, LawDocument> lawDocMap = new HashMap<>();

    /** A sequence number is used to maintain the order of the nodes. */
    protected int sequenceNo = 0;

    protected AbstractLawBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        this.lawVersionId = lawVersionId;
        if (previousTree != null) {
            this.rootNode = previousTree.getRootNode();
            this.lawInfo = previousTree.getLawInfo();
        }
    }

    /**
     * Used so other classes don't need to interact with subclasses directly.
     * @param lawVersionId the 3 letter code.
     * @param previousTree if the law has already existing data.
     * @return the proper LawBuilder.
     */
    public static LawBuilder makeLawBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        String lawID = lawVersionId.lawId();
        if (lawID.equals(CONS_STR))
            return new ConstitutionLawBuilder(lawVersionId, previousTree);
        if (lawID.equals(A_RULES) || lawID.equals(S_RULES))
            return new RulesLawBuilder(lawVersionId, previousTree);
        if (EXPECTED_LAW_ORDERING.containsKey(lawID))
            return new HintBasedLawBuilder(lawVersionId, previousTree, EXPECTED_LAW_ORDERING.get(lawID));
        return new IdBasedLawBuilder(lawVersionId, previousTree);
    }

    /* --- Abstract Methods --- */

    /**
     * The override of this method should be able to figure out which location id is the parent of the
     * given law document.
     *
     * @param docId String
     * @return String
     */
    protected abstract String determineHierarchy(String docId);

    /**
     * Handles any behaviors relating to adding a new child to the hierarchy.
     * @param node LawTreeNode
     */
    protected abstract void addChildNode(LawTreeNode node);

    protected abstract boolean isNodeListEmpty();

    protected abstract void clearParents();

    /* --- Methods --- */

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInitialBlock(LawBlock block, boolean isNewDoc, LawTreeNode priorRoot) {
        final LawDocument lawDoc = new LawDocument(block);
        boolean isRootDoc = false;

        // For the initial law dumps, the first block that is processed for a law (usually) becomes the root node.
        if (rootNode == null) {
            logger.info("Processing root doc: {} for {} law.", lawDoc.getDocumentId(), lawDoc.getLawId());
            LawDocument chapterDoc;
            // If the block seems to be a chapter node, we'll treat this document as the root.
            Matcher specialChapter = SPECIAL_CHAPTER_PATTERN.matcher(lawDoc.getLocationId());
            if (specialChapter.matches() || isChapterDoc(lawDoc)) {
                lawDoc.setDocType(CHAPTER);
                String docTypeId = (specialChapter.matches() ? "" : lawDoc.getLocationId().replace("-CH", ""));
                lawDoc.setDocTypeId(docTypeId);
                chapterDoc = lawDoc;
                isRootDoc = true;
            }
            // Otherwise we have to create our own root node and process the current document as a child of it.
            else {
                chapterDoc = createRootDocument(block, priorRoot);
                if (isNewDoc)
                    lawDocMap.put(chapterDoc.getDocumentId(), chapterDoc);
            }
            lawInfo = deriveLawInfo(chapterDoc.getLawId(), isRootDoc ? chapterDoc.getDocTypeId() : "");
            addRootDocument(chapterDoc);
        }

        if (!isRootDoc) {
            if (isNodeListEmpty())
                throw new IllegalStateException("Failed to add node because it's parent node was not added!");
            if (isSectionDoc(lawDoc))
                processSection(lawDoc);
            else
                processNonSection(lawDoc);
        }
        if (isNewDoc)
            lawDocMap.put(lawDoc.getDocumentId(), lawDoc);
        setLawDocTitle(lawDoc);
    }

    /**
     * {@inheritDoc}
     */
    public void addUpdateBlock(LawBlock block) {
        // Re-parse the titles
        switch (LawMethod.stringToMethod(block.getMethod())) {
            case MASTER -> rebuildTree(block.getText().toString());
            case REPEAL -> {
                logger.info("{} , {}", block.getDocumentId(), rootNode);
                Optional<LawTreeNode> node = rootNode.findNode(block.getDocumentId(), false);
                if (node.isPresent()) {
                    logger.info("Repealing {}", block.getDocumentId());
                    node.get().setRepealedDate(block.getPublishedDate());
                } else
                    logger.warn("Failed to repeal document {} because it could not be located within the law tree!", block.getDocumentId());
            }
            case DELETE -> {
                logger.info("Deleting {}", block.getDocumentId());
                rootNode.findNode(block.getDocumentId(), true);
            }
            case UPDATE -> {
                if (rootNode == null)
                    throw new LawParseException("Can't add law document " + block.getDocumentId() + " without a prior law tree.");
                Optional<LawDocInfo> existingDocInfo = rootNode.find(block.getDocumentId());
                if (existingDocInfo.isEmpty())
                    throw new LawParseException("Can't add law document " + block.getDocumentId() +
                            " without a prior law tree structure including it.");
                existingDocInfo.get().setPublishedDate(block.getPublishedDate());
                LawDocument lawDoc = new LawDocument(existingDocInfo.get(), block.getText().toString());
                setLawDocTitle(lawDoc);
                existingDocInfo.get().setTitle(lawDoc.getTitle());
                lawDocMap.put(lawDoc.getDocumentId(), lawDoc);
                logger.info("Updated {}", lawDoc.getDocumentId());
            }
            case UNKNOWN -> throw new LawParseException("Don't know how to handle law block updates with method: " + block.getMethod());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildTree(String masterDoc) {
        LawTreeNode priorRootNode = this.rootNode;
        this.rootNode = null;
        Set<String> processed = new HashSet<>();
        logger.info("Rebuilding tree for {} with master document.", this.lawVersionId.lawId());

        // Clear out any existing parents when rebuilding trees.
        clearParents();
        for (String docId : StringUtils.split(masterDoc, "\\n")) {
            // Apply doc id replacements if necessary
            final String resolvedDocId = LawDocIdFixer.applyReplacement(docId);
            // Repeat DocIDs should also be ignored.
            if (processed.contains(resolvedDocId) || LawDocIdFixer.ignoreDocument(docId))
                continue;
            LawBlock block = new LawBlock();
            block.setDocumentId(resolvedDocId);
            block.setLawId(resolvedDocId.substring(0, 3));
            block.setLocationId(resolvedDocId.substring(3));

            LocalDate publishedDate = this.lawVersionId.publishedDate();
            boolean isNewDoc = true;
            // Use published date from existing law doc if present
            if (lawDocMap.containsKey(resolvedDocId)) {
                publishedDate = lawDocMap.get(resolvedDocId).getPublishedDate();
                isNewDoc = false;
                logger.debug("Processed law doc id found for {} with published date {}",
                        resolvedDocId, block.getPublishedDate());
            }
            // Or from the previous tree node if set
            else if (priorRootNode != null) {
                Optional<LawDocInfo> existingDocInfo = priorRootNode.find(resolvedDocId);
                if (existingDocInfo.isPresent()) {
                    publishedDate = existingDocInfo.get().getPublishedDate();
                    isNewDoc = false;
                    logger.debug("Found existing law with doc id {} with published date {}",
                            resolvedDocId, block.getPublishedDate());
                }
            }
            if (isNewDoc)
                logger.info("New document id found in master document: {}", resolvedDocId);
            block.setPublishedDate(publishedDate);
            addInitialBlock(block, isNewDoc, priorRootNode);
            processed.add(resolvedDocId);
        }
    }

    /**
     * {@inheritDoc}
     */
    public LawTree getProcessedLawTree() {
        return new LawTree(lawVersionId, rootNode, lawInfo);
    }

    /**
     * {@inheritDoc}
     */
    public List<LawDocument> getProcessedLawDocuments() {
        return new ArrayList<>(lawDocMap.values());
    }

    /**
     * Add the root document which does not have to be associated with a parent.
     *
     * @param rootDoc LawDocument
     */
    private void addRootDocument(LawDocument rootDoc) {
        sequenceNo = 0;
        rootNode = new LawTreeNode(rootDoc, ++sequenceNo);
        addChildNode(this.rootNode);
    }

    /**
     * Add the document by associating it as a child of the current parent node and subsequently setting the
     * current parent node to point to this document.
     *
     * @param lawDoc LawDocument
     */
    protected void addDocument(LawDocument lawDoc) {
        LawTreeNode node = new LawTreeNode(lawDoc, lawDoc.getDocType() == PREAMBLE ? 2 : ++sequenceNo);
        addChildNode(node);
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
            chapter.setName(chapterType.getChapterName());
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
    private static boolean isChapterDoc(LawDocument doc) {
        String locId = doc.getLocationId();
        return (locId.startsWith("-CH") ||
                (!locId.equals("1") && !locId.equals(PREAMBLE_LOC_ID) &&
                        !LOCATION_PATTERN.matcher(locId).matches()));
    }

    /**
     * Section documents typically just have a location id with the number of the section (except in the constitution).
     * All other document types start with a character or symbol.
     * @param lawDoc LawDocument
     * @return boolean - true if this block is a section.
     */
    private static boolean isSectionDoc(LawDocument lawDoc) {
        String docID = lawDoc.getDocumentId(), lawID = lawDoc.getLawId(), locId = lawDoc.getLocationId();
        if (docID.matches(CITY_TAX_STR + ".+"))
            return !docID.contains("P");
        if (lawID.equals(CONS_STR) || lawID.equals(A_RULES) || lawID.equals(S_RULES))
            return locId.matches(".*S\\d+-?[A-Z]?");
        return Character.isDigit(locId.charAt(0));
    }

    /**
     * Create our own root law doc to serve as the root document in the event that we don't receive a top level doc
     * from the dumps. This is common for unconsolidated laws where they just start with the first section or article.
     *
     * If the previous root exists and was a dummy parent, reuse it.
     * @param block LawBlock
     */
    private LawDocument createRootDocument(LawBlock block, LawTreeNode priorRoot) {
        // Reuse the old root doc if it was a dummy
        if (priorRoot != null && priorRoot.getLawDocInfo().isDummy())
            return new LawDocument(priorRoot.getLawDocInfo(), "");

        LawDocument dummyParent = new LawDocument();
        dummyParent.setDummy(true);
        dummyParent.setLawId(block.getLawId());
        dummyParent.setDocumentId(block.getLawId() + "-" + ROOT);
        dummyParent.setLocationId("-" + ROOT);
        dummyParent.setDocType(CHAPTER);
        dummyParent.setDocTypeId(ROOT);
        dummyParent.setPublishedDate(block.getPublishedDate());
        dummyParent.setText("");
        setLawDocTitle(dummyParent);
        return dummyParent;
    }

    protected void setLawDocTitle(LawDocument lawDoc) {
        lawDoc.setTitle(LawTitleParser.extractTitle(lawDoc, lawDoc.getText()));
    }

    /**
     * Section docs are easy, since their location ids are simply numbers (if it's not the
     * Constitution) and they do not have any children.
     * @param lawDoc to process.
     */
    private void processSection(LawDocument lawDoc) {
        logger.debug("Processing section {}", lawDoc.getDocumentId());
        String lawId = lawDoc.getLawId();
        lawDoc.setDocType(SECTION);
        String docTypeId = lawDoc.getLocationId().replace(CITY_TAX_STR.substring(3) + "-", "");
        if (lawId.equals(CONS_STR) || lawId.equals(A_RULES) || lawId.equals(S_RULES))
            docTypeId = docTypeId.replaceAll("[AJR]+\\d+S", "");
        lawDoc.setDocTypeId(docTypeId);
        addChildNode(new LawTreeNode(lawDoc, ++sequenceNo));
    }

    /**
     * Processes documents that aren't sections, with various exceptions to normal parsing rules.
     * @param lawDoc to process.
     */
    private void processNonSection(LawDocument lawDoc) {
        String specificLocId = determineHierarchy(lawDoc.getDocumentId());
        Matcher locMatcher = LOCATION_PATTERN.matcher(specificLocId);
        if (specificLocId.equals(PREAMBLE_LOC_ID)) {
            lawDoc.setDocType(PREAMBLE);
            lawDoc.setDocTypeId("");
        }
        else if (locMatcher.matches() && !lawDoc.getDocumentId().equals(ATTN)) {
            LawDocumentType type = lawLevelCodes.get(locMatcher.group(1));
            // GCM has some Subparts labeled with an S.
            if (lawDoc.getLawId().equals(GCM.name()) && locMatcher.group(1).equals("S"))
                type = SUBPART;
            lawDoc.setDocType(type);
            String docTypeId = locMatcher.group(2);
            lawDoc.setDocTypeId(docTypeId);
        }
        else {
            if (!lawDoc.getDocumentId().matches(CUBIT + "|" + ATTN))
                logger.warn("Failed to parse the following location {}. Setting as MISC type.", lawDoc.getDocumentId());
            lawDoc.setDocType(MISC);
            lawDoc.setDocTypeId(lawDoc.getLocationId());
        }
        addDocument(lawDoc);
    }
}
