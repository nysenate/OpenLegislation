package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;
import gov.nysenate.openleg.service.law.data.LawDataService;
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
    protected static Pattern locationPattern =
        Pattern.compile("^(ST|SP|A|T|P|S|INDEX)(.*)");

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

        Stack<LawTreeNode> parentStack = new Stack<>();
        LawTreeNode currParent = null;

        for (LawBlock block : lawBlocks) {
            LawChapter chapter = null;
            try {
                chapter = LawChapter.valueOf(block.getLawId());
            }
            catch (IllegalArgumentException ex) {
                logger.warn("Unmapped law chapter {}", block.getLawId());
            }

//            if (chapter != null && (chapter.getType().equals(LawType.MISC) || chapter.getType().equals(LawType.RULES)))
//                return;

            logger.info("Processing block {}", block.getDocumentId());
            LawDocument lawDoc = new LawDocument();
            lawDoc.setDocumentId(block.getDocumentId());
            lawDoc.setLawId(block.getLawId());
            lawDoc.setLocationId(block.getLocationId());
            lawDoc.setText(block.getText().toString());
            lawDoc.setPublishDate(lawFile.getPublishedDate());

            // A block with a new law id indicates the top level document for a consolidated law,
            // but not necessarily the case for un-consolidated laws.
            if (!rootNodeMap.containsKey(block.getLawId())) {
                logger.info(" Found chapter node {}", block.getDocumentId());
                lawDoc.setDocType(LawDocumentType.CHAPTER);
                lawDoc.setDocTypeId(block.getLocationId().replaceAll("-CH", ""));
                LawTreeNode chapterNode = new LawTreeNode(lawDoc);
                rootNodeMap.put(block.getLawId(), chapterNode);
                lawDocMap.put(block.getDocumentId(), lawDoc);
                parentStack.push(chapterNode);
                currParent = chapterNode;
                logger.info(" Chapter node doc type id: {}", lawDoc.getDocTypeId());
            }
            else {
                // If the location id starts with a number, we'll assume it's a section and add it as a child
                // of the current parent node.
                if (Character.isDigit(block.getLocationId().charAt(0))) {
                    logger.info(" Found section {}", block.getDocumentId());
                    lawDoc.setDocType(LawDocumentType.SECTION);
                    lawDoc.setDocTypeId(block.getLocationId());
                    lawDocMap.put(block.getDocumentId(), lawDoc);
                    if (currParent == null) {
                        throw new LawParseException("Received a section document before an initial chapter document!");
                    }
                    currParent.addChild(new LawTreeNode(lawDoc));
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
                        lawDocMap.put(block.getDocumentId(), lawDoc);
                        logger.info(" Location id for {} reduced to {}, doc type: {}, type id {} with parent {}",
                                block.getLocationId(), locationId, lawDoc.getDocType(), lawDoc.getDocTypeId(), currParent.getDocumentId());
                        LawTreeNode node = new LawTreeNode(lawDoc);
                        currParent.addChild(node);
                        parentStack.push(node);
                        currParent = node;
                    }
                    // Throw a meaningful exception because we should be able to parse out the locations.
                    else {
                        throw new LawParseException("Failed to parse the following location " + locationId + " for " +
                                "document id: " + block.getDocumentId() + " in file: " + lawFile.getFileName());
                    }
                }
            }

            lawDataService.saveLawDocument(lawFile, lawDoc);
        }
    }

    /**
     *
     *
     * @param lawFile LawFile
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