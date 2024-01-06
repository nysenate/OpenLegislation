package gov.nysenate.openleg.processors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.processors.bill.*;
import gov.nysenate.openleg.processors.bill.sobi.SobiLineType;
import gov.nysenate.openleg.processors.bill.sobi.SobiProcessOptions;
import gov.nysenate.openleg.processors.config.ProcessConfig;
import gov.nysenate.openleg.processors.log.DataProcessUnit;
import gov.nysenate.openleg.processors.log.DataProcessUnitEvent;
import gov.nysenate.openleg.processors.sourcefile.SourceFileFsDao;
import gov.nysenate.openleg.processors.sourcefile.SourceFileRefDao;
import gov.nysenate.openleg.processors.sourcefile.sobi.LegDataFragmentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This LegDataProcessService implementation processes SOBI and XML files.
 */
@Service
public class ManagedLegDataProcessService implements LegDataProcessService {
    private static final Logger logger = LoggerFactory.getLogger(ManagedLegDataProcessService.class);
    private static final Pattern patchTagPattern = Pattern.compile("^\\s*</?PATCH>\\s*$");

    private final List<SourceFileFsDao> sourceFileFsDaos;
    private final SourceFileRefDao sourceFileRefDao;
    private final LegDataFragmentDao legDataFragmentDao;
    private final EventBus eventBus;
    private final OpenLegEnvironment env;
    private final ProcessConfig processConfig;
    private final List<LegDataProcessor> legDataProcessors;

    @Autowired
    public ManagedLegDataProcessService(List<SourceFileFsDao> sourceFileFsDaos,
                                        SourceFileRefDao sourceFileRefDao,
                                        LegDataFragmentDao legDataFragmentDao, EventBus eventBus,
                                        OpenLegEnvironment env, ProcessConfig processConfig,
                                        List<LegDataProcessor> legDataProcessors) {
        this.sourceFileFsDaos = sourceFileFsDaos;
        this.sourceFileRefDao = sourceFileRefDao;
        this.legDataFragmentDao = legDataFragmentDao;
        this.eventBus = eventBus;
        this.env = env;
        this.processConfig = processConfig;
        this.legDataProcessors = legDataProcessors;
    }

    private ImmutableMap<SourceType, SourceFileFsDao> sourceFileDaoMap;

    /**
     * Register processors to handle a specific LegDataFragment via this mapping.
     */
    private ImmutableMap<LegDataFragmentType, LegDataProcessor> processorMap;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
        processorMap = Maps.uniqueIndex(legDataProcessors, LegDataProcessor::getSupportedType);
        sourceFileDaoMap = Maps.uniqueIndex(sourceFileFsDaos, SourceFileFsDao::getSourceType);
    }

    /** --- Implemented Methods --- */

    /**
     * {@inheritDoc}
     */
    @Override
    public int collate() {
        return collateSourceFiles();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int ingest() {
        return processPendingFragments(SobiProcessOptions.builder().build());
    }

    @Override
    public String getCollateType() {
        return "leg_data file";
    }

    @Override
    public String getIngestType() {
        return "leg_data fragment";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int collateSourceFiles() {
        try {
            int totalCollated = 0;
            List<SourceFile> newSources;
            do {
                newSources = getIncomingSourceFiles();
                for (SourceFile sourceFile : newSources) {
                    collateSourceFile(sourceFile);
                    totalCollated++;
                }
            } while (!newSources.isEmpty() && env.isProcessingEnabled());
            return totalCollated;
        } catch (IOException ex) {
            String errMessage = "Error encountered during collation of source files.";
            throw new DataIntegrityViolationException(errMessage, ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LegDataFragment> getPendingFragments(SortOrder sortByPubDate, LimitOffset limitOffset) {
        return legDataFragmentDao.getPendingLegDataFragments(sortByPubDate, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int processFragments(List<LegDataFragment> fragments, SobiProcessOptions options) {
        logger.debug((fragments.isEmpty()) ? "No more fragments to process"
                : "Iterating through {} fragments", fragments.size());
        final List<LegDataFragment> filteredFragments = processConfig.filterFileFragments(fragments);
        if (fragments.size() > 1) {
            logger.info("Processing {} fragments ({} ignored)", filteredFragments.size(), fragments.size() - filteredFragments.size());
        } else if (fragments.size() == 1 && filteredFragments.isEmpty()) {
            logger.info("Ignoring fragment {} due to process config.", fragments.get(0).getFragmentId());
        }
        for (LegDataFragment fragment : filteredFragments) {
            fragment.startProcessing();
            legDataFragmentDao.updateLegDataFragment(fragment);
            // Hand off processing to specific implementations based on fragment type.
            LegDataProcessor currentProcessor = processorMap.get(fragment.getType());
            if (currentProcessor != null) {
                currentProcessor.process(fragment);
                currentProcessor.checkIngestCache();
            } else {
                logger.error("No processors have been registered to handle: " + fragment);
            }
            fragment.setProcessedCount(fragment.getProcessedCount() + 1);
            fragment.setProcessedDateTime(LocalDateTime.now());
        }
        // Perform any necessary post-processing/cleanup
        processorMap.values().forEach(LegDataProcessor::postProcess);
        // Set the fragments as processed and update
        legDataFragmentDao.setPendProcessingFalse(fragments);
        return fragments.size();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Perform the operation in small batches so memory is not saturated.
     */
    @Override
    public int processPendingFragments(SobiProcessOptions options) {
        List<LegDataFragment> fragments;
        int processCount = 0;

        do {
            ImmutableSet<LegDataFragmentType> allowedTypes = options.getAllowedFragmentTypes();
            LimitOffset limOff = new LimitOffset(env.getLegDataBatchSize());
            fragments = legDataFragmentDao.getPendingLegDataFragments(allowedTypes, SortOrder.ASC, limOff);
            // Process fragments in a batch, or one by one depending on sobi batch config.
            if (env.isLegDataBatchEnabled()) {
                processCount += processFragments(fragments, options);
            } else {
                for (LegDataFragment fragment : fragments) {
                    processCount += processFragments(Collections.singletonList(fragment), options);
                }
            }
        } while (!fragments.isEmpty() && env.isProcessingEnabled());

        return processCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePendingProcessing(String fragmentId, boolean pendingProcessing)
            throws LegDataFragmentNotFoundEx {
        try {
            LegDataFragment fragment = legDataFragmentDao.getLegDataFragment(fragmentId);
            fragment.setPendingProcessing(pendingProcessing);
            legDataFragmentDao.updateLegDataFragment(fragment);
        } catch (DataAccessException ex) {
            throw new LegDataFragmentNotFoundEx();
        }
    }

    /** --- Internal Methods --- */

    /**
     * Gets incoming {@link SourceFile}s from multiple sources
     * @return {@link List<SourceFile>}
     * @throws IOException
     */
    private List<SourceFile> getIncomingSourceFiles() throws IOException {
        List<SourceFile> incomingSourceFiles = new ArrayList<>();
        final int batchSize = env.getLegDataBatchSize();
        for (SourceFileFsDao<?> sourceFsDao : sourceFileFsDaos) {
            LimitOffset remainingLimit = new LimitOffset(batchSize - incomingSourceFiles.size());
            incomingSourceFiles.addAll(sourceFsDao.getIncomingSourceFiles(SortOrder.ASC, remainingLimit));
        }

        return incomingSourceFiles;
    }

    /**
     * Performs collate operations on a single source file
     * @param sourceFile
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void collateSourceFile(SourceFile sourceFile) throws IOException {
        // Do some slightly different processing for SOBI and XML files
        DataProcessUnit unit = new DataProcessUnit(
                sourceFile.getSourceType().name(), sourceFile.getFileName(),
                LocalDateTime.now(), DataProcessAction.COLLATE);

        List<LegDataFragment> fragments = createFragments(sourceFile); //When Switching to XML Only we can get rid of the list and save some memory
        logger.info("Created {} fragment{}", fragments.size(), fragments.size() == 1 ? "" : "s");

        // Record the source file in the backing store.
        sourceFileRefDao.updateSourceFile(sourceFile);
        // Save the extracted fragments. They will be marked as pending processing.
        for (LegDataFragment fragment : fragments) {
            logger.info("Saving fragment {}", fragment.getFragmentId());
            fragment.setPendingProcessing(true);
            legDataFragmentDao.updateLegDataFragment(fragment);
            unit.addMessage("Saved " + fragment.getFragmentId());
        }
        // Done with this source file so let's archive it.
        final SourceFileFsDao relevantFsDao = sourceFileDaoMap.get(sourceFile.getSourceType());
        relevantFsDao.archiveSourceFile(sourceFile);
        sourceFileRefDao.updateSourceFile(sourceFile);
        unit.setEndDateTime(LocalDateTime.now());
        eventBus.post(new DataProcessUnitEvent(unit));
    }

    /**
     * Extracts a list of SobiFragments from the given SobiFile.
     */
    private List<LegDataFragment> createFragments(SourceFile sourceFile) throws IOException {
        List<LegDataFragment> legDataFragments = new ArrayList<>();

        // Else continue with splitting the SOBI file into fragments
        StringBuilder billBuffer = new StringBuilder();

        boolean isPatch = false;
        StringBuilder patchMessage = new StringBuilder();

        // Incrementing sequenceNo maintains the order in which the sobi fragments were
        // found in the source sobiFile. However the sequence number for the bill fragment
        // is always set to 0 to ensure that they are always processed first.
        int sequenceNo = 1; //TODO verify this is supposed to be 1

        // Replace the null characters with spaces and split by newline.
        List<String> lines = Arrays.asList(sourceFile.getText().replace('\0', ' ').split("\\r?\\n"));
        Iterator<String> lineIterator = lines.iterator();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            // Check for a patch tag indicating a manual fix
            if (patchTagPattern.matcher(line).matches()) {
                isPatch = true;
                extractPatchMessage(lineIterator, patchMessage);
            }
            LegDataFragmentType fragmentType = LegDataFragmentType.matchFragmentType(line);
            if (fragmentType != null) {
                // Bill fragments are in the sobi format and appended into a single buffer
                if (fragmentType.equals(LegDataFragmentType.BILL)) {
                    // Memos need to be converted to latin1 encoding
                    if (line.charAt(11) == SobiLineType.SPONSOR_MEMO.getTypeCode()) {
                        line = new String(line.getBytes(sourceFile.getEncoding()), "latin1");
                    }
                    line = line.replace((char) 193, '°');
                    billBuffer.append(line).append("\n");
                }
                // Other fragment types are in XML format. The iterator moves past the closing xml
                // tag and the xml text is stored in the fragment.
                else {
                    String xmlText = extractXmlText(fragmentType, line, lineIterator);
                    LegDataFragment fragment = new LegDataFragment(sourceFile, fragmentType, xmlText, sequenceNo++);
                    legDataFragments.add(fragment);
                }
            }
        }
        // Convert the billBuffer into a single bill fragment (if applicable) with sequence no set to 0.
        if (billBuffer.length() > 0) {
            LegDataFragment billFragment = new LegDataFragment(sourceFile, LegDataFragmentType.BILL, billBuffer.toString(), 0);
            legDataFragments.add(billFragment);
        }
        // Set manual fix flag and add notes if this file was a patch
        if (isPatch) {
            String notes = patchMessage.toString();
            legDataFragments.forEach(fragment -> {
                fragment.setManualFix(true);
                fragment.setManualFixNotes(notes);
            });
        }
        return legDataFragments;
    }

    /**
     * Gets a patch sobi message from within a set of patch tags, appending it to the given string builder
     *
     * @param lineIterator Iterator<String>
     * @param patchMessage StringBuilder
     */
    private void extractPatchMessage(Iterator<String> lineIterator, StringBuilder patchMessage) {
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            if (patchTagPattern.matcher(line).matches()) {
                return;
            }
            if (patchMessage.length() > 0) {
                patchMessage.append("\n");
            }
            patchMessage.append(line.trim());
        }
    }

    /**
     * Extracts a well formed XML document from the lines and writes it to the given
     * file. This depends strongly on escape sequences being on their own line; otherwise
     * we'll get malformed XML docs.
     *
     * @param fragmentType LegDataFragmentType
     * @param line         String - The starting line of the document
     * @param iterator     Iterator<String> - Current iterator from the sobi file's text body
     * @return String - The resulting XML string.
     */
    private String extractXmlText(LegDataFragmentType fragmentType, String line, Iterator<String> iterator) {
        String endPattern = fragmentType.getEndPattern();
        var xmlBuffer = new StringBuilder(
                "<?xml version='1.0' encoding='UTF-8'?>&newl;" + line + "&newl;"
        );
        while (iterator.hasNext()) {
            String next = iterator.next();
            xmlBuffer.append(next.replaceAll("\\xb9", "&sect;")).append("&newl;");
            if (next.matches(endPattern)) {
                break;
            }
        }
        String xmlString = xmlBuffer.toString();

        /*
        This code searches and replaces new lines with \n for consistent formatting
        Other excess newlines .
        The xmlString contains the entire document when this method parses through the whole document
         */
        xmlString = xmlString.replaceAll("&newl;", "\n");//.replaceAll("(?!\n)\\p{Cntrl}", "");
        return xmlString;
    }
}
