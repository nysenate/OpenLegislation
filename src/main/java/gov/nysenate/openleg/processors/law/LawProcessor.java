package gov.nysenate.openleg.processors.law;

import gov.nysenate.openleg.legislation.law.LawTree;
import gov.nysenate.openleg.legislation.law.LawType;
import gov.nysenate.openleg.legislation.law.LawVersionId;
import gov.nysenate.openleg.legislation.law.dao.LawDataService;
import gov.nysenate.openleg.legislation.law.dao.LawTreeNotFoundEx;
import gov.nysenate.openleg.processors.AbstractDataProcessor;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import gov.nysenate.openleg.updates.law.BulkLawUpdateEvent;
import gov.nysenate.openleg.updates.law.LawTreeUpdateEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processes the initial/update law dumps and persists the data.
 */
@Service
public class LawProcessor extends AbstractDataProcessor
{
    /** Used to denote when the text of a LawDocument in lawDocMap should not be changed. */
    public static final String ONLY_TITLE_UPDATE = "****no text update****";

    private static final Logger logger = LoggerFactory.getLogger(LawProcessor.class);

    /** The law files are most likely sent in CP850 encoding. */
    private static final Charset LAWFILE_CHARSET = Charset.forName("CP850");

    /** Pattern for law doc headers.  */
    private static final Pattern lawHeader =
        Pattern.compile("\\.{1,2}SO DOC ((\\w{3})(.{13}))(.{8}) (.{15}) (?:LAWS\\(((?:UN)?CONSOLIDATED)\\))");

    @Autowired private LawDataService lawDataService;

    /**
     * Performs all the steps required to process and persist the supplied LawFile.
     *
     * @param lawFile LawFile
     */
    public void process(final LawFile lawFile) {
        boolean isInitial = lawFile.isInitialDump();
        DataProcessUnit unit = createDataProcessUnit(lawFile);
        try {
            logger.info("Processing law file {}", lawFile);
            List<LawBlock> lawBlocks = getLawBlocks(lawFile);
            if (isInitial) {
                processInitialLaws(lawFile, lawBlocks, unit);
            }
            else {
                processLawUpdates(lawFile, lawBlocks, unit);
            }
        }
        catch (IOException ex) {
            logger.error("Unexpected IOException during LawFile processing", ex);
            unit.addException("Unexpected IOException: " + ex.getMessage());
        }
        catch (LawParseException ex) {
            unit.addException("Fatal law parsing error, processing has been halted! " + ex.getMessage(), logger);
        }
        postDataUnitEvent(unit);
    }

    /** --- Internal Methods --- */

    /**
     * The initial laws are parsed such that the order of the documents indicates the structure of the laws
     * (i.e. there are no master documents).
     *
     * @param lawFile LawFile
     * @param lawBlocks List<LawBlock>
     */
    private void processInitialLaws(LawFile lawFile, List<LawBlock> lawBlocks, DataProcessUnit unit) {
        Map<String, LawBuilder> lawBuilders = new HashMap<>();
        for (LawBlock block : lawBlocks) {
            // Create the law builder for the law id if it doesn't already exist.
            if (!lawBuilders.containsKey(block.getLawId())) {
                LawBuilder lawBuilder = AbstractLawBuilder.makeLawBuilder(new LawVersionId
                        (block.getLawId(), block.getPublishedDate()), null);
                lawBuilders.put(block.getLawId(), lawBuilder);
                unit.addMessage("Processing initial docs for " + block.getLawId());
            }
            // Process the initial block
            lawBuilders.get(block.getLawId()).addInitialBlock(block, true, null);
        }
        // Persist the results
        persist(lawFile, lawBuilders);
    }

    /**
     * The update files will either contain a document for a new or changed law block, or a MASTER document
     * to indicate that the organization of the law has changed. The other types of actions include AMENDED
     * and REPEALED but we have not encountered those as of yet.
     *
     * @param lawFile LawFile
     * @param lawBlocks List<LawBlock>
     */
    private void processLawUpdates(LawFile lawFile, List<LawBlock> lawBlocks, DataProcessUnit unit) {
        Map<String, LawBuilder> lawBuilders = new HashMap<>();
        Map<String, LawTree> lawTrees = new HashMap<>();
        for (LawBlock block : lawBlocks) {
            LawVersionId lawVersionId = new LawVersionId(block.getLawId(), block.getPublishedDate());
            logger.debug("Processing law version id: {}", lawVersionId);
            // Retrieve the existing law tree if it exists.
            if (!lawTrees.containsKey(block.getLawId())) {
                try {
                    LawTree lawTree = lawDataService.getLawTree(block.getLawId(), block.getPublishedDate());
                    lawTrees.put(block.getLawId(), lawTree);
                }
                catch (LawTreeNotFoundEx ex) {
                    lawTrees.put(block.getLawId(), null);
                    unit.addException("Update received for a law " + block.getLawId() + " without an existing tree!", logger);
                }
            }
            // Create the law builder for the law id if it doesn't already exist.
            if (!lawBuilders.containsKey(block.getLawId())) {
                LawBuilder lawBuilder = AbstractLawBuilder.makeLawBuilder(lawVersionId, lawTrees.get(block.getLawId()));
                lawBuilders.put(block.getLawId(), lawBuilder);
            }
            // Process the update block
            try {
                lawBuilders.get(block.getLawId()).addUpdateBlock(block);
            } catch (LawParseException ex) {
                // Catch LawBlock parsing errors here so the rest of the blocks in the file will be processed.
                logger.error("LawParseException has occurred:\n{}", ExceptionUtils.getStackTrace(ex));
                unit.addException("Unable to process LawBlock, this block has been skipped: ", ex);
            }
        }
        persist(lawFile, lawBuilders);
    }

    /**
     * Iterates over the law builders and persists the processed output.
     *
     * @param lawFile LawFile - Used to keep track of the source
     * @param lawBuilders Map<String, LawBuilder>
     */
    private void persist(LawFile lawFile, Map<String, LawBuilder> lawBuilders) {
        // Persist the results
        lawBuilders.forEach((lawId, lawBuilder) ->{
            logger.info("Persisting law documents for {}", lawId);
            lawBuilder.getProcessedLawDocuments().forEach(d -> lawDataService.saveLawDocument(lawFile, d));
            eventBus.post(new BulkLawUpdateEvent(lawBuilder.getProcessedLawDocuments()));
            logger.info("Persisting law tree for {}", lawId);
            lawDataService.saveLawTree(lawFile, lawBuilder.getProcessedLawTree());
            eventBus.post(new LawTreeUpdateEvent(lawId));
        });
    }

    /**
     * Extracts a collection of LawBlocks from the given LawFile. Each block is represents all the meta data and
     * text for each document section in the law file (delineated by the ..SO DOC header). The LawBlock is just
     * a helper object that should be used to construct LawDocuments.
     *
     * @param lawFile LawFile - The LawFile to extract the blocks from.
     * @return List<ListBlock>
     * @throws IOException if there is an error reading the file.
     */
    private List<LawBlock> getLawBlocks(LawFile lawFile) throws IOException {
        List<LawBlock> rawDocList = new ArrayList<>();
        logger.debug("Extracting law blocks...");
        File file = lawFile.getFile();
        Iterator<String> fileItr = Files.lines(file.toPath(), LAWFILE_CHARSET).iterator();
        LawBlock block = null;
        Matcher headerMatcher;
        while (fileItr.hasNext()) {
            String line = fileItr.next();
            headerMatcher = lawHeader.matcher(line);
            if (headerMatcher.matches()) {
                if (block != null && !LawDocIdFixer.ignoreDocument(block.getDocumentId()))
                    rawDocList.add(block);
                block = new LawBlock();
                block.setHeader(line);
                block.setLawId(headerMatcher.group(2).trim());
                block.setPublishedDate(lawFile.getPublishedDate());
                block.setDocumentId(LawDocIdFixer.applyReplacement(headerMatcher.group(1).trim()));
                block.setLocationId(block.getDocumentId().substring(3));
                block.setMethod(headerMatcher.group(4).trim());
                block.setConsolidated(headerMatcher.group(6).equals(LawType.CONSOLIDATED.name()));
            }
            else {
                if (block == null)
                    throw new LawParseException("No doc header received prior to line: " + line);
                block.getText().append(line).append("\\n");
            }
        }
        if (block != null && !LawDocIdFixer.ignoreDocument(block.getDocumentId()))
            rawDocList.add(block);
        return rawDocList;
    }
}
