package gov.nysenate.openleg.processor.law;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.law.LawFile;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.model.law.LawVersionId;
import gov.nysenate.openleg.service.law.data.LawDataService;
import gov.nysenate.openleg.service.law.data.LawTreeNotFoundEx;
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

    /** Set of law ids to ignore during processing. */
    protected static Set<String> ignoreLaws = Sets.newHashSet("CNS");

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
     * The initial laws are parsed such that the order of the documents indicates the structure of the laws
     * (i.e. there are no master documents).
     *
     * @param lawFile LawFile
     * @param lawBlocks List<LawBlock>
     */
    protected void processInitialLaws(LawFile lawFile, List<LawBlock> lawBlocks) {
        Map<String, LawBuilder> lawBuilders = new HashMap<>();
        for (LawBlock block : lawBlocks) {
            if (ignoreLaws.contains(block.getLawId())) continue;
            // Create the law builder for the law id if it doesn't already exist.
            if (!lawBuilders.containsKey(block.getLawId())) {
                LawBuilder lawBuilder = new LawBuilder(new LawVersionId(block.getLawId(), block.getPublishedDate()));
                lawBuilders.put(block.getLawId(), lawBuilder);
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
    protected void processLawUpdates(LawFile lawFile, List<LawBlock> lawBlocks) {
        Map<String, LawBuilder> lawBuilders = new HashMap<>();
        Map<String, LawTree> lawTrees = new HashMap<>();
        for (LawBlock block : lawBlocks) {
            if (ignoreLaws.contains(block.getLawId())) continue;
            LawVersionId lawVersionId = new LawVersionId(block.getLawId(), block.getPublishedDate());
            // Retrieve the existing law tree if it exists.
            if (!lawTrees.containsKey(block.getLawId())) {
                try {
                    LawTree lawTree = lawDataService.getLawTree(block.getLawId(), block.getPublishedDate());
                    lawTrees.put(block.getLawId(), lawTree);
                }
                catch (LawTreeNotFoundEx ex) {
                    lawTrees.put(block.getLawId(), null);
                    logger.warn("Update received for a law without an existing tree!");
                }
            }
            // Create the law builder for the law id if it doesn't already exist.
            if (!lawBuilders.containsKey(block.getLawId())) {
                LawBuilder lawBuilder = new LawBuilder(lawVersionId, lawTrees.get(block.getLawId()));
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
        lawBuilders.forEach((k,v) ->{
            logger.info("Persisting law documents for {}", k);
            v.getProcessedLawDocuments().forEach(d -> lawDataService.saveLawDocument(lawFile, d));
            logger.info("Persisting law tree for {}", k);
            lawDataService.saveLawTree(lawFile, v.getProcessedLawTree());
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
                if (block != null) {
                    rawDocList.add(block);
                }
                block = new LawBlock();
                block.setHeader(line);
                block.setDocumentId(headerMatcher.group(1).trim());
                block.setLawId(headerMatcher.group(2).trim());
                block.setLocationId(headerMatcher.group(3).trim());
                block.setMethod(headerMatcher.group(4).trim());
                block.setConsolidated(headerMatcher.group(6).equals("CONSOLIDATED"));
                block.setPublishedDate(lawFile.getPublishedDate());
            }
            else {
                if (block == null) throw new LawParseException("No doc header received prior to line: " + line);
                block.getText().append(line).append("\\n");
            }
        }
        if (block != null) {
            rawDocList.add(block);
        }
        return rawDocList;
    }
}