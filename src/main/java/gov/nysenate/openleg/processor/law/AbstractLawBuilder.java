package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.model.law.LawDocumentType.*;

public abstract class AbstractLawBuilder implements LawBuilder
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractLawBuilder.class);

    /** Pattern used for parsing the location ids to extract the document type and doc type id. */
    protected static Pattern locationPattern = Pattern.compile("^(R|ST|SP|SA|A|T|P|S|INDEX)(.+)");

    /** Pattern for certain chapter nodes that don't have the usual -CH pattern. */
    private static Pattern specialChapterPattern = Pattern.compile("^(AS|ASSEMBLYRULES|SENATERULES)$");

    /** String for city personal income tax on residents, an odd clause in the GCT law. */
    private static final String CITY_TAX_STR = "GCT25-A";

    /** Hints about the law hierarchy for certain laws that have inconsistent doc id naming. */
    private static Map<String, List<LawDocumentType>> expectedLawOrdering = new HashMap<>();
    static {
        expectedLawOrdering.put("EDN", Arrays.asList(TITLE, ARTICLE, SUBARTICLE, PART, SUB_PART));
        expectedLawOrdering.put("CPL", Arrays.asList(PART, TITLE, ARTICLE));
    }

    /** The location ids portions are prefixed with a code to indicate the different document types. */
    protected static Map<String, LawDocumentType> lawLevelCodes = new HashMap<>();
    static {
        lawLevelCodes.put("A", LawDocumentType.ARTICLE);
        lawLevelCodes.put("SA", LawDocumentType.SUBARTICLE);
        lawLevelCodes.put("T", LawDocumentType.TITLE);
        lawLevelCodes.put("ST", LawDocumentType.SUBTITLE);
        lawLevelCodes.put("P", LawDocumentType.PART);
        lawLevelCodes.put("SP", LawDocumentType.SUB_PART);
        lawLevelCodes.put("S", LawDocumentType.SECTION);
        lawLevelCodes.put("INDEX", LawDocumentType.INDEX);
        lawLevelCodes.put("R", LawDocumentType.RULE);
    }

    /** For use in Roman numeral conversion. */
    private static final TreeMap<Integer, String> NUMERALS = new TreeMap<>();
    static {
        NUMERALS.put(50, "L");
        NUMERALS.put(40, "XL");
        NUMERALS.put(10, "X");
        NUMERALS.put(9, "IX");
        NUMERALS.put(5, "V");
        NUMERALS.put(4, "IV");
        NUMERALS.put(1, "I");
    }

    /** A law version id that is obtained from the law blocks. */
    protected LawVersionId lawVersionId;

    /** The root node in the law tree. */
    protected LawTreeNode rootNode = null;

    /** Basic Chapter info. */
    protected LawInfo lawInfo;

    /** Map of all the documents that need to be persisted. */
    protected Map<String, LawDocument> lawDocMap = new HashMap<>();

    /** A sequence number is used to maintain the order of the nodes. */
    protected int sequenceNo = 0;

    /** --- Constructors --- */

    public AbstractLawBuilder(LawVersionId lawVersionId) {
        this.lawVersionId = lawVersionId;
    }

    public AbstractLawBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        this(lawVersionId);
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
        String lawID = lawVersionId.getLawId();
        if (lawID.equals(ConstitutionBuilder.CONS_STR))
            return new ConstitutionBuilder(lawVersionId, previousTree);
        if (lawID.equals(RulesBuilder.ASSEMBLY_RULES_STR) || lawID.equals(RulesBuilder.SENATE_RULES_STR))
            return new RulesBuilder(lawVersionId, previousTree);
        if (expectedLawOrdering.containsKey(lawID))
            return new HintBasedLawBuilder(lawVersionId, previousTree, expectedLawOrdering.get(lawID));
        return new IdBasedLawBuilder(lawVersionId, previousTree);
    }

    /** --- Abstract Methods --- */

    /**
     * The override of this method should be able to figure out which location id is the parent of the
     * given law document.
     *
     * @param block LawBlock
     * @return String
     */
    protected abstract String determineHierarchy(LawBlock block);

    /**
     * Handles any behaviors relating to adding a new child to the hierarchy.
     * @param node LawTreeNode
     */
    protected abstract void addChildNode(LawTreeNode node);

    protected abstract boolean isNodeListEmpty();

    protected abstract void clearParents();

    /** --- Methods --- */

    /**
     * {@inheritDoc}
     */
    public void addInitialBlock(LawBlock block, boolean isNewDoc) {
        final LawDocument lawDoc = new LawDocument(block);
        boolean isRootDoc = false;

        // For the initial law dumps, the first block that is processed for a law (usually) becomes the root node.
        if (rootNode == null) {
            logger.info("Processing root doc: {} for {} law.", lawDoc.getDocumentId(), lawDoc.getLawId());
            LawDocument chapterDoc;
            // If the block seems to be a chapter node, we'll treat this document as the root.
            Matcher specialChapter = specialChapterPattern.matcher(lawDoc.getLocationId());
            if (specialChapter.matches() || isLikelyChapterDoc(lawDoc)) {
                lawDoc.setDocType(LawDocumentType.CHAPTER);
                lawDoc.setDocTypeId(lawDoc.getLocationId().replaceFirst(specialChapter.matches() ? specialChapter.group(1) : "-CH", ""));
                chapterDoc = lawDoc;
                isRootDoc = true;
            }
            // Otherwise we have to create our own root node and process the current document as a child of it.
            else {
                chapterDoc = createRootDocument(block);
            }
            lawInfo = deriveLawInfo(chapterDoc.getLawId(), isRootDoc ? chapterDoc.getDocTypeId() : "");
            addRootDocument(chapterDoc, isNewDoc);
        }

        // If this block is not a root doc,
        if (!isRootDoc) {
            // Section docs are easy, since their location ids are simply
            // numbers (if it's not the Constitution) and they do not have
            // any children.
            if (isLikelySectionDoc(lawDoc)) {
                logger.debug("Processing section {}", lawDoc.getDocumentId());
                lawDoc.setDocType(LawDocumentType.SECTION);
                String docTypeId = lawDoc.getLocationId();
                if (lawDoc.getLawId().equals(ConstitutionBuilder.CONS_STR))
                    docTypeId = docTypeId.replaceAll("A\\d+S", "");
                if (lawDoc.getDocumentId().startsWith(CITY_TAX_STR + "-"))
                    docTypeId = lawDoc.getDocumentId().replace(CITY_TAX_STR + "-", "");
                lawDoc.setDocTypeId(docTypeId);
                if (isNewDoc) {
                    lawDocMap.put(lawDoc.getDocumentId(), lawDoc);
                }
                addChildNode(new LawTreeNode(lawDoc, ++sequenceNo));
            }

            else {
                String specificLocId = determineHierarchy(block);
                Matcher locMatcher = locationPattern.matcher(specificLocId);
                if (locMatcher.matches()) {
                    lawDoc.setDocType(lawLevelCodes.get(locMatcher.group(1)));
                    lawDoc.setDocTypeId(locMatcher.group(2));
                }
                else if (specificLocId.equals("AA1"))
                    lawDoc.setDocType(PREAMBLE);
                else {
                    if (lawDoc.getDocumentId().startsWith(CITY_TAX_STR))
                        lawDoc.setLocationId(CITY_TAX_STR.substring(3));
                    else
                        logger.warn("Failed to parse the following location {}. Setting as MISC type.", lawDoc.getDocumentId());
                    lawDoc.setDocType(LawDocumentType.MISC);
                    lawDoc.setDocTypeId(block.getLocationId());
                }
                addDocument(lawDoc, isNewDoc);
            }
        }
        // Set the title for the document
        setLawDocTitle(lawDoc, isNewDoc);
    }

    /**
     * {@inheritDoc}
     */
    public void addUpdateBlock(LawBlock block) {
        switch (block.getMethod()) {
            // Rebuild the law tree
            case "*MASTER*":
                rebuildTree(block.getText().toString());
                break;
            // Repeal the document
            case "*REPEAL*" :
                logger.info("{} , {}", block.getDocumentId(), rootNode);
                Optional<LawTreeNode> node = rootNode.findNode(block.getDocumentId(), false);
                if (node.isPresent()) {
                    logger.info("Repealing {}", block.getDocumentId());
                    node.get().setRepealedDate(block.getPublishedDate());
                }
                else
                    logger.warn("Failed to repeal document {} because it could not be located within the law tree!", block.getDocumentId());
                break;
            // Delete the document
            case "*DELETE*" :
                logger.info("Deleting {}", block.getDocumentId());
                rootNode.findNode(block.getDocumentId(), true);
                break;
            // Update the document
            case "" :
                if (rootNode == null)
                    throw new LawParseException("Can't add law document " + block.getDocumentId() + " without a prior law tree.");
                Optional<LawDocInfo> existingDocInfo = rootNode.find(block.getDocumentId());
                if (!existingDocInfo.isPresent())
                    throw new LawParseException("Can't add law document " + block.getDocumentId() +
                            " without a prior law tree structure including it.");

                existingDocInfo.get().setPublishedDate(block.getPublishedDate());
                LawDocument lawDoc = new LawDocument(existingDocInfo.get(), block.getText().toString());
                // Re-parse the titles
                setLawDocTitle(lawDoc, true);
                lawDocMap.put(lawDoc.getDocumentId(), lawDoc);
                logger.info("Updated {}", lawDoc.getDocumentId());
                break;
            default :
                throw new LawParseException("Don't know how to handle law block updates with method: " + block.getMethod());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildTree(String masterDoc) {
        LawTreeNode priorRootNode = this.rootNode;
        this.rootNode = null;
        logger.info("Rebuilding tree for {} with master document.", this.lawVersionId.getLawId());
        // Clear out any existing parents when rebuilding trees.
        clearParents();
        for (String docId : StringUtils.split(masterDoc, "\\n")) {
            // Apply doc id replacements if necessary
            final String resolvedDocId = LawDocIdFixer.applyReplacement(docId, this.lawVersionId.getPublishedDate());
            LawBlock block = new LawBlock();
            block.setDocumentId(resolvedDocId);
            block.setLawId(resolvedDocId.substring(0, 3));
            block.setLocationId(resolvedDocId.substring(3));
            // Use published date from existing law doc if present
            if (lawDocMap.containsKey(resolvedDocId)) {
                block.setPublishedDate(lawDocMap.get(resolvedDocId).getPublishedDate());
                logger.debug("Processed law doc id found for {} with published date {}", resolvedDocId, block.getPublishedDate());
                addInitialBlock(block, false);
                continue;
            }
            // Or from the previous tree node if set
            else if (priorRootNode != null) {
                Optional<LawDocInfo> existingDocInfo = priorRootNode.find(resolvedDocId);
                if (existingDocInfo.isPresent()) {
                    block.setPublishedDate(existingDocInfo.get().getPublishedDate());
                    addInitialBlock(block, false);
                    logger.debug("Found existing law with doc id {} with published date {}",
                        block.getDocumentId(), block.getPublishedDate());
                    continue;
                }
            }
            logger.info("New document id found in master document: {}", resolvedDocId);
            block.setPublishedDate(this.lawVersionId.getPublishedDate());
            addInitialBlock(block, true);
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
     * @param isNewDoc boolean - Set to true if this is a new document and should be persisted.
     */
    private void addRootDocument(LawDocument rootDoc, boolean isNewDoc) {
        if (rootDoc == null) throw new IllegalArgumentException("Root document cannot be null!");
        sequenceNo = 0;
        rootNode = new LawTreeNode(rootDoc, ++sequenceNo);
        if (isNewDoc) {
            lawDocMap.put(rootDoc.getDocumentId(), rootDoc);
        }
        addChildNode(this.rootNode);
    }

    /**
     * Add the document by associating it as a child of the current parent node and subsequently setting the
     * current parent node to point to this document.
     *
     * @param lawDoc LawDocument
     * @param isNewDoc boolean - Set to true if this is a new document and should be persisted.
     */
    protected void addDocument(LawDocument lawDoc, boolean isNewDoc) {
        if (isNodeListEmpty()) {
            throw new IllegalStateException("Failed to add node because it's parent node was not added!");
        }
        if (isNewDoc) {
            lawDocMap.put(lawDoc.getDocumentId(), lawDoc);
        }
        LawTreeNode node = new LawTreeNode(lawDoc, lawDoc.getDocType() == LawDocumentType.PREAMBLE ? 2 : ++sequenceNo);
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
        String locId = doc.getLocationId();
        return (locId.startsWith("-CH") ||
                (!locId.equals("1") && !locId.equals("AA1") &&
                        !locationPattern.matcher(locId).matches()));
    }

    /**
     * Section documents typically just have a location id with the number of the section (except in the constitution).
     * All other document types start with a character or symbol.
     *
     * @param lawDoc LawDocument
     * @return boolean - true if this block is most likely a section
     */
    protected boolean isLikelySectionDoc(LawDocument lawDoc) {
        if (lawDoc.getDocumentId().matches(CITY_TAX_STR + ".+"))
            return !lawDoc.getDocumentId().contains("P");
        return Character.isDigit(lawDoc.getLocationId().charAt(0));
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
        setLawDocTitle(dummyParent, true);
        return dummyParent;
    }

    protected void setLawDocTitle(LawDocument lawDoc, boolean isNewDoc) {
        lawDoc.setTitle(LawTitleParser.extractTitle(lawDoc, lawDoc.getText()));
    }

    /**
     * Quickly converts a number to a Roman numeral. Used to display Articles
     * as Roman numerals, as they are in the Constitution text.
     * @param number to convert.
     * @return a Roman numeral.
     */
    protected static String toNumeral(int number) {
        if (number == 0)
            return "";
        int next = NUMERALS.floorKey(number);
        return NUMERALS.get(next) + toNumeral(number-next);
    }
}