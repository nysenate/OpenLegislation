package gov.nysenate.openleg.processor.sobi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.sobi.SobiDao;
import gov.nysenate.openleg.model.process.DataProcessAction;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.process.DataProcessUnitEvent;
import gov.nysenate.openleg.model.sobi.*;
import gov.nysenate.openleg.processor.agenda.AgendaProcessor;
import gov.nysenate.openleg.processor.agenda.AgendaVoteProcessor;
import gov.nysenate.openleg.processor.bill.BillSobiProcessor;
import gov.nysenate.openleg.processor.bill.BillXMLBillDigestProcessor;
import gov.nysenate.openleg.processor.bill.BillXMLBillTextProcessor;
import gov.nysenate.openleg.processor.calendar.ActiveListProcessor;
import gov.nysenate.openleg.processor.calendar.CalendarProcessor;
import gov.nysenate.openleg.processor.entity.CommitteeProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This SobiProcessService implementation processes every type of sobi fragment.
 */
@Service
public class ManagedSobiProcessService implements SobiProcessService
{
    private static final Logger logger = LoggerFactory.getLogger(ManagedSobiProcessService.class);

    private static final Pattern patchTagPattern = Pattern.compile("^\\s*</?PATCH>\\s*$");

    @Autowired private SobiDao sobiDao;
    @Autowired private EventBus eventBus;
    @Autowired private Environment env;

    /** --- Processor Dependencies --- */

    @Autowired private AgendaProcessor agendaProcessor;
    @Autowired private AgendaVoteProcessor agendaVoteProcessor;
    @Autowired private BillSobiProcessor billSobiProcessor;
    @Autowired private CalendarProcessor calendarProcessor;
    @Autowired private ActiveListProcessor activeListProcessor;
    @Autowired private CommitteeProcessor committeeProcessor;

    // XML Processors
    @Autowired private BillXMLBillTextProcessor billXMLBillTextProcessor;
    @Autowired private BillXMLBillDigestProcessor billXMLBillDigestProcessor;

    /** Register processors to handle a specific SobiFragment via this mapping. */
    private ImmutableMap<SobiFragmentType, SobiProcessor> processorMap;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
        processorMap = ImmutableMap.<SobiFragmentType, SobiProcessor>builder()
            .put(SobiFragmentType.AGENDA, agendaProcessor)
            .put(SobiFragmentType.AGENDA_VOTE, agendaVoteProcessor)
            .put(SobiFragmentType.BILL, billSobiProcessor)
            .put(SobiFragmentType.CALENDAR, calendarProcessor)
            .put(SobiFragmentType.CALENDAR_ACTIVE, activeListProcessor)
            .put(SobiFragmentType.COMMITTEE, committeeProcessor)
            .put(SobiFragmentType.BILLTEXT,billXMLBillTextProcessor)
            .put(SobiFragmentType.LDSUMM,billXMLBillDigestProcessor)
            .build();
    }

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public int collate() {
        return collateSobiFiles();
    }

    /** {@inheritDoc} */
    @Override
    public int ingest() {
        return processPendingFragments(SobiProcessOptions.builder().build());
    }

    @Override
    public String getCollateType() {
        return "sobi file";
    }

    @Override
    public String getIngestType() {
        return "sobi fragment";
    }

    /** {@inheritDoc} */
    @Override
    public int collateSobiFiles() {
        try {
            int totalCollated = 0;
            List<SobiFile> newSobis;
            do {
                // Iterate through all the new sobi files in small batches to avoid saturating memory.
                newSobis = sobiDao.getIncomingSobiFiles(SortOrder.ASC, new LimitOffset(env.getSobiBatchSize()));
                logger.debug((newSobis.isEmpty()) ? "No more sobi files to collate."
                                                  : "Collating {} sobi files.", newSobis.size());
                for (SobiFile sobiFile : newSobis) {
                    // Do some slightly different processing for SOBI and XML files
                    DataProcessUnit unit;
                    if (sobiFile.getFileName().substring(sobiFile.getFileName().length()-3).toLowerCase().equals("xml")) {
                        // Create DataProcessUnit specific for XMLs
                        unit = new DataProcessUnit("XML-FILE", sobiFile.getFileName(), LocalDateTime.now(), DataProcessAction.COLLATE);
                    } else {
                        // Create DataProcessUnit specific for SOBIs
                        unit = new DataProcessUnit("SOBI-FILE", sobiFile.getFileName(), LocalDateTime.now(), DataProcessAction.COLLATE);
                    }
                    List<SobiFragment> fragments = createFragments(sobiFile);
                    logger.info("Created {} fragments", fragments.size());
                    // Record the sobi file in the backing store.
                    sobiDao.updateSobiFile(sobiFile);
                    // Save the extracted fragments. They will be marked as pending processing.
                    for (SobiFragment fragment : fragments) {
                        logger.info("Saving fragment {}", fragment.getFragmentId());
                        fragment.setPendingProcessing(true);
                        sobiDao.updateSobiFragment(fragment);
                        unit.addMessage("Saved " + fragment.getFragmentId());
                    }
                    // Done with this sobi file so let's archive it.
                    sobiDao.archiveAndUpdateSobiFile(sobiFile);
                    totalCollated++;
                    unit.setEndDateTime(LocalDateTime.now());
                    eventBus.post(new DataProcessUnitEvent(unit));
                }
            }
            while (!newSobis.isEmpty() && env.isProcessingEnabled());
            return totalCollated;
        }
        catch (IOException ex) {
            String errMessage = "Error encountered during collation of sobi files.";
            throw new DataIntegrityViolationException(errMessage, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SobiFragment> getPendingFragments(SortOrder sortByPubDate, LimitOffset limitOffset) {
        return sobiDao.getPendingSobiFragments(sortByPubDate, limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    public int processFragments(List<SobiFragment> fragments, SobiProcessOptions options) {
        logger.debug((fragments.isEmpty()) ? "No more fragments to process"
                                          : "Iterating through {} fragments", fragments.size());
        for (SobiFragment fragment : fragments) {
            // Hand off processing to specific implementations based on fragment type.
            if (processorMap.containsKey(fragment.getType())) {
                processorMap.get(fragment.getType()).process(fragment);
            }
            else {
                logger.error("No processors have been registered to handle: " + fragment);
            }
            fragment.setProcessedCount(fragment.getProcessedCount() + 1);
            fragment.setProcessedDateTime(LocalDateTime.now());
        }
        // Perform any necessary post-processing/cleanup
        processorMap.values().forEach(p -> p.postProcess());
        // Set the fragments as processed and update
        fragments.forEach(f -> {
            f.setPendingProcessing(false);
            sobiDao.updateSobiFragment(f);
        });

        return fragments.size();
    }

    /** {@inheritDoc}
     *
     *  Perform the operation in small batches so memory is not saturated.
     */
    @Override
    public int processPendingFragments(SobiProcessOptions options) {
        List<SobiFragment> fragments;
        int processCount = 0;
        do {
            ImmutableSet<SobiFragmentType> allowedTypes = options.getAllowedFragmentTypes();
            LimitOffset limOff = (env.isSobiBatchEnabled()) ? new LimitOffset(env.getSobiBatchSize()) : LimitOffset.ONE;
            fragments = sobiDao.getPendingSobiFragments(allowedTypes, SortOrder.ASC, limOff);
            processCount += processFragments(fragments, options);
        }
        while (!fragments.isEmpty() && env.isProcessingEnabled());
        return processCount;
    }

    /** {@inheritDoc} */
    @Override
    public void updatePendingProcessing(String fragmentId, boolean pendingProcessing)
                                        throws SobiFragmentNotFoundEx {
        try {
            SobiFragment fragment = sobiDao.getSobiFragment(fragmentId);
            fragment.setPendingProcessing(pendingProcessing);
            sobiDao.updateSobiFragment(fragment);
        }
        catch (DataAccessException ex) {
            throw new SobiFragmentNotFoundEx();
        }
    }

    /** --- Internal Methods --- */

    /**
     * Extracts a list of SobiFragments from the given SobiFile.
     */
    private List<SobiFragment> createFragments(SobiFile sobiFile) throws IOException {
        List<SobiFragment> sobiFragments = new ArrayList<>();

        // If the file passed in is an XML file, return a list containing the one fragment
        if (sobiFile.getFileName().substring(sobiFile.getFileName().length()-3).toLowerCase().equals("xml")) {
            SobiFragment fragment;
            if (sobiFile.getFileName().contains("BILLTEXT")) {
                // For bill text XML files
                fragment = new SobiFragment(sobiFile, SobiFragmentType.BILLTEXT, sobiFile.getText(), 1);
            } else {
                // For digest summary XML files (will not be the else block when we get more file types)
                // TODO: make this into a switch based on the file type
                fragment = new SobiFragment(sobiFile, SobiFragmentType.LDSUMM, sobiFile.getText(), 1);
            }
            sobiFragments.add(fragment);
            return sobiFragments;
        }

        // Else continue with splitting the SOBI file into fragments
        StringBuilder billBuffer = new StringBuilder();

        boolean isPatch = false;
        StringBuilder patchMessage = new StringBuilder();

        // Incrementing sequenceNo maintains the order in which the sobi fragments were
        // found in the source sobiFile. However the sequence number for the bill fragment
        // is always set to 0 to ensure that they are always processed first.
        int sequenceNo = 1;

        // Replace the null characters with spaces and split by newline.
        List<String> lines = Arrays.asList(sobiFile.getText().replace('\0', ' ').split("\\r?\\n"));
        Iterator<String> lineIterator = lines.iterator();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            // Check for a patch tag indicating a manual fix
            if (patchTagPattern.matcher(line).matches()) {
                isPatch = true;
                extractPatchMessage(lineIterator, patchMessage);
            }
            SobiFragmentType fragmentType = getFragmentTypeFromLine(line);
            if (fragmentType != null) {
                // Bill fragments are in the sobi format and appended into a single buffer
                if (fragmentType.equals(SobiFragmentType.BILL)) {
                    // Memos need to be converted to latin1 encoding
                    if (line.charAt(11) == SobiLineType.SPONSOR_MEMO.getTypeCode()) {
                        line = new String(line.getBytes(sobiFile.getEncoding()), "latin1");
                    }
                    line = line.replace((char)193, '°');
                    billBuffer.append(line).append("\n");
                }
                // Other fragment types are in XML format. The iterator moves past the closing xml
                // tag and the xml text is stored in the fragment.
                else {
                    String xmlText = extractXmlText(fragmentType, line, lineIterator);
                    SobiFragment fragment = new SobiFragment(sobiFile, fragmentType, xmlText, sequenceNo++);
                    sobiFragments.add(fragment);
                }
            }
        }
        // Convert the billBuffer into a single bill fragment (if applicable) with sequence no set to 0.
        if (billBuffer.length() > 0) {
            SobiFragment billFragment = new SobiFragment(sobiFile, SobiFragmentType.BILL, billBuffer.toString(), 0);
            sobiFragments.add(billFragment);
        }
        // Set manual fix flag and add notes if this file was a patch
        if (isPatch) {
            String notes = patchMessage.toString();
            sobiFragments.forEach(fragment -> {
                fragment.setManualFix(true);
                fragment.setManualFixNotes(notes);
            });
        }
        return sobiFragments;
    }

    /**
     * Check the given SOBI line to determine if it matches the start of a SOBI Fragment type.
     *
     * @param line String
     * @return SobiFragmentType or null if no match
     */
    private SobiFragmentType getFragmentTypeFromLine(String line) {
        for (SobiFragmentType fragmentType : SobiFragmentType.values()) {
            if (line.matches(fragmentType.getStartPattern())) {
                return fragmentType;
            }
        }
        return null;
    }

    /**
     * Gets a patch sobi message from within a set of patch tags, appending it to the given string builder
     * @param lineIterator Iterator<String>
     * @param patchMessage StringBuilder
     */
    private void extractPatchMessage(Iterator<String> lineIterator, StringBuilder patchMessage) {
        while(lineIterator.hasNext()) {
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
     * @param fragmentType SobiFragmentType
     * @param line String - The starting line of the document
     * @param iterator Iterator<String> - Current iterator from the sobi file's text body
     *
     * @return String - The resulting XML string.
     * @throws java.io.IOException
     */
    private String extractXmlText(SobiFragmentType fragmentType, String line, Iterator<String> iterator) throws IOException {
        String endPattern = fragmentType.getEndPattern();
        StringBuffer xmlBuffer = new StringBuffer(
            "<?xml version='1.0' encoding='UTF-8'?>&newl;" +
                "<SENATEDATA>&newl;" + line + "&newl;"
        );
        String in = null;
        while (iterator.hasNext()) {
            in = iterator.next();
            xmlBuffer.append(in.replaceAll("\\xb9", "&sect;")).append("&newl;");
            if (in.matches(endPattern)) {
                break;
            }
        }
        if (in == null) {
            // This is bad, but don't throw an exception. If the resulting XML document
            // is malformed we'll throw the exception during ingest.
            logger.error("Unterminated XML document: " + line);
        }
        String xmlString = xmlBuffer.append("</SENATEDATA>").toString();

        // TODO: Figure out this magic.
        xmlBuffer = new StringBuffer();
        Matcher m = Pattern.compile("<\\!\\[CDATA\\[(.*?)\\]\\]>").matcher(xmlString);
        while(m.find()) {
            m.appendReplacement(xmlBuffer, Matcher.quoteReplacement(m.group(0).replaceAll("&newl;", "").replaceAll("\\\\n","\n")));
        }
        m.appendTail(xmlBuffer);

        // TODO: Figure out this magic as well.
        xmlString = xmlBuffer.toString().replaceAll("&newl;", "\n").replaceAll("(?!\n)\\p{Cntrl}","").replaceAll("(?!\\.{2})[ ]{2,}"," ");
        return xmlString;
    }
}
