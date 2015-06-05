package gov.nysenate.openleg.processor.law;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.law.LawDocumentType;
import gov.nysenate.openleg.model.law.LawFile;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.model.law.LawVersionId;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.service.law.data.LawDataService;
import gov.nysenate.openleg.service.law.data.LawTreeNotFoundEx;
import gov.nysenate.openleg.service.law.event.BulkLawUpdateEvent;
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

import static gov.nysenate.openleg.model.law.LawDocumentType.*;

/**
 * Processes the initial/update law dumps and persists the data.
 */
@Service
public class LawProcessor extends AbstractDataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(LawProcessor.class);

    /** The law files are most likely sent in CP850 encoding. */
    protected static Charset LAWFILE_CHARSET = Charset.forName("CP850");

    /** Pattern for law doc headers.  */
    protected static Pattern lawHeader =
        Pattern.compile("\\.\\.SO DOC ((\\w{3})(.{13}))(.{8}) (.{15}) (?:LAWS\\(((?:UN)?CONSOLIDATED)\\))");

    /** Hints about the law hierarchy for certain laws that have inconsistent doc id naming. */
    protected static Map<String, List<LawDocumentType>> expectedLawOrdering = new HashMap<>();
    static {
        expectedLawOrdering.put("EDN", Arrays.asList(TITLE, ARTICLE, SUBARTICLE, PART, SUB_PART));
        expectedLawOrdering.put("CPL", Arrays.asList(PART, TITLE, ARTICLE));
    }

    /** Set of law ids to ignore during processing. */
    protected Set<String> ignoreLaws = Sets.newHashSet("CNS");

    /** Set of law ids to only allow processing of. Overrides 'ignoreLaws'. */
    protected Set<String> onlyLaws = Sets.newHashSet();

    @Autowired private LawDataService lawDataService;

    @Override
    public void init() {
        initBase();
    }

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

    /** --- Basic Getters/Setters --- */

    public Set<String> getIgnoreLaws() {
        return ignoreLaws;
    }

    public void setIgnoreLaws(String... ignoreLaws) {
        this.ignoreLaws = Sets.newHashSet(ignoreLaws);
    }

    public Set<String> getOnlyLaws() {
        return onlyLaws;
    }

    public void setOnlyLaws(String... lawIds) {
        this.onlyLaws = Sets.newHashSet(lawIds);
    }

    /** --- Internal Methods --- */

    /**
     * The initial laws are parsed such that the order of the documents indicates the structure of the laws
     * (i.e. there are no master documents).
     *
     * @param lawFile LawFile
     * @param lawBlocks List<LawBlock>
     */
    protected void processInitialLaws(LawFile lawFile, List<LawBlock> lawBlocks, DataProcessUnit unit) {
        Map<String, LawBuilder> lawBuilders = new HashMap<>();
        for (LawBlock block : lawBlocks) {
            if (!shouldProcessLaw(block)) continue;
            // Create the law builder for the law id if it doesn't already exist.
            if (!lawBuilders.containsKey(block.getLawId())) {
                LawBuilder lawBuilder = createLawBuilder(new LawVersionId(block.getLawId(), block.getPublishedDate()), null);
                lawBuilders.put(block.getLawId(), lawBuilder);
                unit.addMessage("Processing initial docs for " + block.getLawId());
            }
            // Process the initial block
            lawBuilders.get(block.getLawId()).addInitialBlock(block, true);
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
    protected void processLawUpdates(LawFile lawFile, List<LawBlock> lawBlocks, DataProcessUnit unit) {
        Map<String, LawBuilder> lawBuilders = new HashMap<>();
        Map<String, LawTree> lawTrees = new HashMap<>();
        for (LawBlock block : lawBlocks) {
            if (!shouldProcessLaw(block)) continue;
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
                LawBuilder lawBuilder = createLawBuilder(lawVersionId, lawTrees.get(block.getLawId()));
                lawBuilders.put(block.getLawId(), lawBuilder);
            }
            // Process the update block
            lawBuilders.get(block.getLawId()).addUpdateBlock(block);
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
            eventBus.post(new BulkLawUpdateEvent(lawBuilder.getProcessedLawDocuments()));
            lawBuilder.getProcessedLawDocuments().forEach(d -> lawDataService.saveLawDocument(lawFile, d));
            logger.info("Persisting law tree for {}", lawId);
            lawDataService.saveLawTree(lawFile, lawBuilder.getProcessedLawTree());
        });
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
    protected List<LawBlock> getLawBlocks(LawFile lawFile) throws IOException {
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
                if (block != null && !LawDocIdFixer.ignoreDocument(block.getDocumentId(), block.getPublishedDate())) {
                    rawDocList.add(block);
                }
                block = new LawBlock();
                block.setHeader(line);
                block.setLawId(headerMatcher.group(2).trim());
                block.setPublishedDate(lawFile.getPublishedDate());
                block.setDocumentId(
                    LawDocIdFixer.applyReplacement(headerMatcher.group(1).trim(), lawFile.getPublishedDate()));
                block.setLocationId(block.getDocumentId().substring(3));
                block.setMethod(headerMatcher.group(4).trim());
                block.setConsolidated(headerMatcher.group(6).equals("CONSOLIDATED"));
            }
            else {
                if (block == null) throw new LawParseException("No doc header received prior to line: " + line);
                block.getText().append(line).append("\\n");
            }
        }
        if (block != null && !LawDocIdFixer.ignoreDocument(block.getDocumentId(), block.getPublishedDate())) {
            rawDocList.add(block);
        }
        return rawDocList;
    }

    protected boolean shouldProcessLaw(LawBlock block) {
        return (onlyLaws.contains(block.getLawId())) ||
               (onlyLaws.isEmpty() && !ignoreLaws.contains(block.getLawId()));
    }

    protected LawBuilder createLawBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        if (expectedLawOrdering.containsKey(lawVersionId.getLawId())) {
            return new HintBasedLawBuilder(lawVersionId, previousTree, expectedLawOrdering.get(lawVersionId.getLawId()));
        }
        else {
            return new IdBasedLawBuilder(lawVersionId, previousTree);
        }
    }
}