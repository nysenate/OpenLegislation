package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;
import gov.nysenate.openleg.service.law.data.LawDataService;
import gov.nysenate.openleg.service.law.data.LawDocumentNotFoundEx;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Works with the {@link LawBuilder} class to process the initial law dumps and updates and perform any
 * necessary persistence.
 */
@Service
public class LawProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(LawProcessor.class);

    /** The law files are most likely sent in CP850 encoding. */
    protected static Charset LAWFILE_CHARSET = Charset.forName("CP850");

    /** Pattern for law doc headers.  */
    protected static Pattern lawHeader =
        Pattern.compile("\\.\\.SO DOC ((\\w{3})(.{13}))(.{8}) (.{15}) (?:LAWS\\(((?:UN)?CONSOLIDATED)\\))");

    /** Pattern used for parsing the location ids to extract the document type and doc type id. */
    protected static Pattern locationPattern = Pattern.compile("^(ST|SP|A|T|P|S|INDEX)(.*)");

    /** Pattern for certain chapter nodes that don't have the usual -CH pattern. */
    protected static Pattern specialChapterPattern = Pattern.compile("^(AS|ASSEMBLYRULES|SENATERULES)$");

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

    @Autowired
    private LawDataService lawDataService;

    /**
     * Performs all the steps required to process and persist the supplied LawFile.
     *
     * @param lawFile LawFile
     */
    public void process(final LawFile lawFile) {
        boolean isInitial = lawFile.isInitialDump();
        try {
            logger.info("Processing law file {}", lawFile);
            LocalDate publishDate = lawFile.getPublishedDate();
            List<LawBlock> lawBlocks = getLawBlocks(lawFile);
            if (isInitial) {
                processInitialLaws(lawFile, lawBlocks);
            }
            else {
                processLawUpdates(lawFile, lawBlocks);
            }
        }
        catch (IOException e) {
            logger.error("Failed to ", e);
        }
    }

    /** --- Internal Methods --- */

    /**
     * The initial laws are parsed a little differently in that the order of the documents indicates
     * the structure of the laws (i.e. there are no master documents).
     *
     * @param lawFile LawFile
     * @param lawBlocks List<LawBlock>
     */
    protected void processInitialLaws(LawFile lawFile, List<LawBlock> lawBlocks) {
        Map<String, LawTreeNode> rootNodeMap = new HashMap<>();
        Map<String, LawDocument> lawDocMap = new HashMap<>();
        Map<String, LawBuilder> lawBuilders = new HashMap<>();

        Stack<LawTreeNode> parentStack = new Stack<>();
        LawTreeNode currParent = null;
        int sequenceNo = 0;

        for (LawBlock block : lawBlocks) {
            LawChapter chapter = null;
            try {
                chapter = LawChapter.valueOf(block.getLawId());
            }
            catch (IllegalArgumentException ex) {
                logger.warn("Unmapped law chapter {}", block.getLawId());
            }

            logger.info("Processing block {}", block.getDocumentId());
            LawDocument lawDoc = new LawDocument(block);
            lawDoc.setPublishedDate(lawFile.getPublishedDate());

            boolean isRootDoc = false;

            if (!lawBuilders.containsKey(block.getLawId())) {
                LawBuilder lawBuilder = new LawBuilder(block.getLawId(), lawFile.getPublishedDate());
                lawBuilders.put(block.getLawId(), lawBuilder);
            }

            // A block with a new law id needs to have a chapter doc to serve as the root node.
            if (!rootNodeMap.containsKey(block.getLawId())) {
                logger.debug("Chapter node {}", block.getDocumentId());
                LawDocument chapterDoc;
                if (isLikelyChapterNode(block)) {
                    lawDoc.setDocType(LawDocumentType.CHAPTER);
                    lawDoc.setDocTypeId(block.getLocationId().replaceFirst("-CH", ""));
                    chapterDoc = lawDoc;
                    isRootDoc = true;
                }
                // Otherwise we have to create a dummy chapter node and process the current document as a child of it.
                else {
                    chapterDoc = createDummyChapter(lawFile, block);
                }

                sequenceNo = 0;
                LawTreeNode chapterNode = new LawTreeNode(chapterDoc, ++sequenceNo);
                rootNodeMap.put(block.getLawId(), chapterNode);
                lawDocMap.put(block.getDocumentId(), lawDoc);
                parentStack.push(chapterNode);
                currParent = chapterNode;
                logger.debug("Chapter node doc type id: {}", chapterDoc.getDocTypeId());

                lawDataService.saveLawDocument(lawFile, chapterDoc);
            }

            if (!isRootDoc) {
                if (isLikelySectionDoc(block)) {
                    logger.debug("Found section {}", block.getDocumentId());
                    lawDoc.setDocType(LawDocumentType.SECTION);
                    lawDoc.setDocTypeId(block.getLocationId());
                    lawDocMap.put(block.getDocumentId(), lawDoc);
                    if (currParent == null) {
                        throw new LawParseException("Received a section document before an initial chapter document!");
                    }
                    currParent.addChild(new LawTreeNode(lawDoc, ++sequenceNo));
                }
                // Otherwise determine the current parent node by comparing the location ids, popping the parent node
                // stack until we have matching prefixes or we arrive at a chapter (root) node.
                else {
                    if (currParent == null) {
                        throw new LawParseException("The parent document node is null, can't add any children.");
                    }
                    String locationId = block.getLocationId();
                    while (!currParent.isRootNode()) {
                        if (StringUtils.startsWith(block.getLocationId(), currParent.getLocationId())) {
                            String trimmedLocId = StringUtils.removeStart(block.getLocationId(), currParent.getLocationId());
                            if (locationPattern.matcher(trimmedLocId).matches()) {
                                locationId = trimmedLocId;
                                break;
                            }
                        }
                        parentStack.pop();
                        currParent = parentStack.peek();
                    }
                    Matcher locMatcher = locationPattern.matcher(locationId);
                    if (locMatcher.matches()) {
                        lawDoc.setDocType(lawLevelCodes.get(locMatcher.group(1)));
                        lawDoc.setDocTypeId(locMatcher.group(2));
                    }
                    else {
                        logger.warn("Failed to parse the following location {} in file {}",
                            block.getDocumentId(), lawFile.getFileName());
                        lawDoc.setDocType(LawDocumentType.MISC);
                        lawDoc.setDocTypeId(block.getLocationId());
                    }
                    lawDocMap.put(block.getDocumentId(), lawDoc);
                    logger.debug(" Location id for {} reduced to {}, doc type: {}, type id {} with parent {}",
                            block.getLocationId(), locationId, lawDoc.getDocType(), lawDoc.getDocTypeId(), currParent.getDocumentId());
                    LawTreeNode node = new LawTreeNode(lawDoc, ++sequenceNo);
                    currParent.addChild(node);
                    parentStack.push(node);
                    currParent = node;
                }

                lawDataService.saveLawDocument(lawFile, lawDoc);
            }
        }
        rootNodeMap.forEach((k,v) -> {
            lawDataService.saveLawTree(lawFile, new LawTree(k, lawFile.getPublishedDate(), v));
        });
    }

    protected void processLawUpdates(LawFile lawFile, List<LawBlock> lawBlocks) {
        Set<String> masterDocsReceived = new HashSet<>();
        Map<String, LawTree> lawTrees = new HashMap<>();

        for (LawBlock block : lawBlocks) {
            String method = block.getMethod();

            if (!lawTrees.containsKey(block.getLawId())) {
                lawTrees.put(block.getLawId(), lawDataService.getLawTree(block.getLawId(), lawFile.getPublishedDate()));
            }

            // Rebuild the law tree
            if (method.trim().equals("*MASTER*")) {
                logger.info("FOUND MASTER DOCUMENT FOR {}", block.getLawId());
                masterDocsReceived.add(block.getLawId());
            }
            // Otherwise add/update the document
            else if (method.isEmpty()) {
                LawTree tree = lawTrees.get(block.getLawId());

                if (tree.getRootNode().find(block.getDocumentId()).isPresent()) {
                    // Find the law document info from the law tree and use that to build a new law doc

                    logger.info("GOT UPDATE DOC ! {}", block.getDocumentId());
                }
                else {
                    logger.info("GOT NEW DOC {}", block.getDocumentId());
                }
            }
            else {
                logger.warn("GOT UNKNOWN METHOD: " + block.getMethod());
            }
        }
    }

    /**
     * Section documents typically just have a location id with the number of the section (except in the constitution).
     * All other document types start with a character or symbol.
     *
     * @param block LawBlock
     * @return boolean - true if this block is most likely a section
     */
    protected boolean isLikelySectionDoc(LawBlock block) {
        return Character.isDigit(block.getLocationId().charAt(0));
    }

    /**
     * Indicates if the block is potentially a chapter node. Consolidated laws will typically begin with a -CH
     * which is not a problem, but some unconsolidated laws have the year or in some cases start right with the
     * section or article. We're checking to make sure those cases do not exist for this block.
     *
     * @param block LawBlock
     * @return boolean
     */
    protected boolean isLikelyChapterNode(LawBlock block) {
        return ( block.getLocationId().startsWith("-CH") || specialChapterPattern.matcher(block.getLocationId()).matches() ||
                 (!block.getLocationId().equals("1") && !locationPattern.matcher(block.getLocationId()).matches()));
    }

    /**
     * Create out own Chapter law doc to serve as the root document in the event that we don't receive a top level
     * doc from the dumps. This is common for unconsolidated laws.
     *
     * @param lawFile LawFile
     * @param block LawBlock
     */
    protected LawDocument createDummyChapter(LawFile lawFile, LawBlock block) {
        LawDocument dummyParent = new LawDocument();
        dummyParent.setLawId(block.getLawId());
        dummyParent.setDocumentId(block.getLawId() + "-ROOT");
        dummyParent.setLocationId("-ROOT");
        dummyParent.setDocType(LawDocumentType.CHAPTER);
        dummyParent.setDocTypeId("ROOT");
        dummyParent.setPublishedDate(lawFile.getPublishedDate());
        dummyParent.setText("");
        return dummyParent;
    }

    /**
     * Extracts a collection of LawBlocks from the given LawFile. Each block is represents all the meta data and
     * text for each document section in the law file (delineated by the ..SO DOC header). The LawBlock is just
     * a helper object that should be used to construct LawDocuments.
     *
     * @param lawFile LawFile - The LawFile to extract the blocks from.
     * @return List<ListBlock>
     * @throws IOException
     */
    public List<LawBlock> getLawBlocks(LawFile lawFile) throws IOException {
        List<LawBlock> rawDocList = new ArrayList<>();
        logger.debug("Extracting law blocks...");
        File file = lawFile.getFile();
        Iterator<String> fileItr = Files.lines(file.toPath(), LAWFILE_CHARSET).iterator();
        LawBlock doc = null;
        Matcher headerMatcher;
        while (fileItr.hasNext()) {
            String line = fileItr.next();
            headerMatcher = lawHeader.matcher(line);
            if (headerMatcher.matches()) {
                if (doc != null) {
                    rawDocList.add(doc);
                }
                doc = new LawBlock();
                doc.setHeader(line);
                doc.setDocumentId(headerMatcher.group(1).trim());
                doc.setLawId(headerMatcher.group(2).trim());
                doc.setLocationId(headerMatcher.group(3).trim());
                doc.setMethod(headerMatcher.group(4).trim());
                doc.setConsolidated(headerMatcher.group(6).equals("CONSOLIDATED"));
            }
            else {
                if (doc == null) throw new LawParseException("No doc header received prior to line: " + line);
                doc.getText().append(line);
            }
        }
        return rawDocList;
    }
}