package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.sobi.SobiFileDao;
import gov.nysenate.openleg.dao.sobi.SobiFragmentDao;
import gov.nysenate.openleg.model.sobi.SOBIFragmentType;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.processors.sobi.SobiProcessor;
import gov.nysenate.openleg.processors.sobi.agenda.AgendaProcessor;
import gov.nysenate.openleg.processors.sobi.agenda.AgendaVoteProcessor;
import gov.nysenate.openleg.processors.sobi.bill.BillProcessor;
import gov.nysenate.openleg.processors.sobi.calendar.CalendarActiveListProcessor;
import gov.nysenate.openleg.processors.sobi.calendar.CalendarProcessor;
import gov.nysenate.openleg.processors.sobi.entity.CommitteeProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.util.FileHelper.moveFileToDirectory;

/**
 * Contains an implementation for each step of the Open Legislation data processing pipeline.
 *
 * <ul>
 *   <li>
 *      {@link #stage(File, File) Stage}:
 *      Grab a distinct set of data files files to operate on. We are constantly receiving
 *      SOBI files so it is helpful to isolate the ones we are currently processing
 *   </li>
 *   <li>
 *      {@link #collate() Collate}:
 *      Each SOBI file can be thought of as a collection of sub documents. The collate step
 *      extracts these files, converts them to UTF-8 encoding, and writes them to file.
 *      Writing to file is useful for debugging because it provides a record of what data
 *      was sent into each processor.
 *   </li>
 *   <li>
 *      {@link #ingest() Ingest}:
 *      Process collated files in chronological order based on the time stamp encoded into
 *      the SOBI file name. Delegates file processing based on document type.
 *   </li>
 * </ul>
 */
@Service
public class DataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);

    protected static String encoding = "CP850";

    private Map<SOBIFragmentType, SobiProcessor> processorMap;

    /** --- DAO instances --- */

    @Autowired
    private SobiFileDao sobiFileDao;

    @Autowired
    private SobiFragmentDao sobiFragmentDao;

    /** --- Processor Instances --- */

    @Autowired
    private BillProcessor billProcessor;

    @Autowired
    private CommitteeProcessor committeeProcessor;

    /** --- Constructors --- */

    public DataProcessor() {
        this.processorMap = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        this.processorMap.put(SOBIFragmentType.BILL, billProcessor);
        this.processorMap.put(SOBIFragmentType.AGENDA, new AgendaProcessor());
        this.processorMap.put(SOBIFragmentType.AGENDA_VOTE, new AgendaVoteProcessor());
        this.processorMap.put(SOBIFragmentType.CALENDAR, new CalendarProcessor());
        this.processorMap.put(SOBIFragmentType.CALENDAR_ACTIVE, new CalendarActiveListProcessor());
        this.processorMap.put(SOBIFragmentType.COMMITTEE, committeeProcessor);
    }

    /** --- Processing methods --- */

    /**
     * Stages CMS.TEXT, transcripts/, hearings/ and SOBI format files from the source
     * directory into their respective destinations subfolders in the work directory.
     *
     * @param sourceDir - The directory to pull files from
     * @param workDir - The directory to stage the files into
     * @throws IOException
     * @throws ParseException
     */
    public void stage(File sourceDir, File workDir) throws IOException, ParseException {
        File rulesFile = new File(sourceDir,"CMS.TEXT");
        if (rulesFile.exists()) {
            moveFileToDirectory(rulesFile, workDir, true);
        }

        // Prepares incoming SOBI files for processing
        sobiFileDao.stageSobiFiles(true);

        // TODO: Stage all hearings to the work directory.
        // TODO: Stage all transcripts
    }

    /**
     *
     * @throws IOException
     * @throws ParseException
     */
    public void collate() throws IOException, ParseException {
        int offset = 0;
        List<SobiFile> pendingSobiFiles = sobiFileDao.getPendingSobiFiles(SortOrder.ASC, 1000, offset);
        while (!pendingSobiFiles.isEmpty()) {
            logger.info("Collating batch of {} sobi files.", pendingSobiFiles.size());
            offset += pendingSobiFiles.size();
            for (SobiFile sobiFile : pendingSobiFiles) {
                StringBuilder billBuffer = new StringBuilder();
                int fragmentCounter = 1;
                List<SobiFragment> sobiFragments = new ArrayList<>();
                List<String> lines = Arrays.asList(sobiFile.getText().split("\\r?\\n"));
                Iterator<String> lineIterator = lines.iterator();
                // Construct fragments from SOBI text
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    SOBIFragmentType fragmentType = getFragmentTypeFromLine(line);
                    if (fragmentType != null) {
                        // Bill portions are appended into a single buffer
                        if (fragmentType.equals(SOBIFragmentType.BILL)) {
                            if (line.charAt(11) == 'M') {
                                // Memos are latin1 encoding
                                line = new String(line.getBytes(encoding), "latin1");
                            }
                            line = line.replace((char)193, '°');
                            billBuffer.append(line).append("\n");
                        }
                        // Other fragment types are in XML format
                        else {
                            String xmlText = extractXmlText(fragmentType, line, lineIterator);
                            SobiFragment fragment = new SobiFragment(sobiFile, fragmentType, xmlText, fragmentCounter++);
                            sobiFragments.add(fragment);
                        }
                    }
                }
                if (billBuffer.length() > 0) {
                    SobiFragment billFragment = new SobiFragment(sobiFile, SOBIFragmentType.BILL, billBuffer.toString(), 1);
                    sobiFragments.add(billFragment);
                }
                // Persist the fragments
                sobiFragmentDao.saveSOBIFragments(sobiFragments);
            }
            pendingSobiFiles = sobiFileDao.getPendingSobiFiles(SortOrder.ASC, 1000, offset);
        }
    }

    /**
     * Processes all staged/collated files in (mostly) chronological order by filename.
     * CMS.TXT, transcripts, and hearings may be processed in any order. The appropriate
     * file processor for each file is based on the directory they are stored in.
     *
     * @throws IOException
     */
    public void ingest() throws IOException {
        // Process SOBI files
        List<SobiFile> pendingSobiFiles = sobiFileDao.getPendingSobiFiles(SortOrder.ASC, 1000, 0);
        while (!pendingSobiFiles.isEmpty()) {
            for (SobiFile sobiFile : pendingSobiFiles) {
                List<SobiFragment> fragments = sobiFragmentDao.getSOBIFragments(sobiFile, SortOrder.ASC);
                ingestSobiFragments(fragments);
                markSobiFileAsProcessed(sobiFile);
            }
            pendingSobiFiles = sobiFileDao.getPendingSobiFiles(SortOrder.ASC, 1000, 0);
        }

        // TODO: Process Transcripts / Public Hearings
        // TODO: Handle CMS.TEXT (Rules file)
    }

    /**
     * Processes the SOBI Fragments using the registered implementations.
     * @param fragments List<SobiFragment>
     */
    @Transactional
    private void ingestSobiFragments(List<SobiFragment> fragments) {
        for (SobiFragment fragment : fragments) {
            if (processorMap.containsKey(fragment.getType())) {
                processorMap.get(fragment.getType()).process(fragment);
            }
            else {
                logger.warn("No processors have been registered to handle: " + fragment);
            }
        }
    }

    /** --- Internal Methods --- */

    /**
     * Updates the status of the SobiFile such that it is marked as processed.
     * @param sobiFile SobiFile
     */
    private void markSobiFileAsProcessed(SobiFile sobiFile) {
        sobiFile.incrementProcessedCount();
        sobiFile.setProcessedDateTime(new Date());
        sobiFile.setPendingProcessing(false);
        sobiFileDao.updateSobiFile(sobiFile);
    }

    /**
     * Check the given SOBI line to determine if it matches the start of a SOBI Fragment type.
     * @param line String
     * @return SOBIFragmentType or null if no match
     */
    protected SOBIFragmentType getFragmentTypeFromLine(String line) {
        for (SOBIFragmentType fragmentType : SOBIFragmentType.values()) {
            if (line.matches(fragmentType.getStartPattern())) {
                return fragmentType;
            }
        }
        return null;
    }

    /**
     * Extracts a well formed XML document from the lines and writes it to the given
     * file. This depends strongly on escape sequences being on their own line; otherwise
     * we'll get malformed XML docs. TODO: EDIT THIS DOC
     *
     * @param fragmentType SOBIFragmentType
     * @param line String - The starting line of the document
     * @param iterator Iterator<String>
     *
     * @return String - The resulting XML string.
     * @throws IOException
     */
    protected String extractXmlText(SOBIFragmentType fragmentType, String line, Iterator<String> iterator) throws IOException {
        String endPattern = fragmentType.getEndPattern();

        StringBuffer sb = new StringBuffer(
            "<?xml version='1.0' encoding='UTF-8'?>&newl;" +
            "<SENATEDATA>&newl;" +
             line+"&newl;"
        );

        String in = null;
        while (iterator.hasNext()) {
            in = iterator.next();
            sb.append(in.replaceAll("\\xb9","&sect;") + "&newl;");
            if (in.matches(endPattern)) {
                break;
            }
        }

        if (in == null) {
            // This is bad, but don't throw an exception. If the resulting XML document
            // is malformed we'll throw the exception during ingest.
            logger.error("Unterminated XML document: " + line);
        }

        String data = sb.append("</SENATEDATA>").toString();

        // TODO: Figure out all this matcher magic. How does it work?
        sb = new StringBuffer();
        Matcher m = Pattern.compile("<\\!\\[CDATA\\[(.*?)\\]\\]>").matcher(data);
        while(m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0).replaceAll("&newl;", "").replaceAll("\\\\n","\n")));
        }
        m.appendTail(sb);

        // TODO: What exactly are we replacing here?
        data = sb.toString().replaceAll("&newl;", "\n").replaceAll("(?!\n)\\p{Cntrl}","").replaceAll("(?!\\.{2})[ ]{2,}"," ");
        return data;
    }
}
