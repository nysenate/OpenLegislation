package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.sobi.SOBIFileDao;
import gov.nysenate.openleg.dao.sobi.SOBIFragmentDao;
import gov.nysenate.openleg.model.Change;
import gov.nysenate.openleg.model.sobi.SOBIFile;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import gov.nysenate.openleg.model.sobi.SOBIFragmentType;
import gov.nysenate.openleg.processors.agenda.AgendaProcessor;
import gov.nysenate.openleg.processors.agenda.AgendaVoteProcessor;
import gov.nysenate.openleg.processors.bill.BillProcessor;
import gov.nysenate.openleg.processors.calendar.CalendarActiveListProcessor;
import gov.nysenate.openleg.processors.calendar.CalendarProcessor;
import gov.nysenate.openleg.processors.entity.CommitteeProcessor;
import gov.nysenate.openleg.processors.sobi.SOBIProcessor;
import gov.nysenate.openleg.service.ServiceBase;
import gov.nysenate.openleg.util.Storage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.util.FileHelper.*;

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
    private static Logger logger = Logger.getLogger(DataProcessor.class);

    protected static String encoding = "CP850";

    private Map<SOBIFragmentType, SOBIProcessor> processorMap;

    /** --- DAO instances --- */

    @Autowired
    private SOBIFileDao sobiFileDao;

    @Autowired
    private SOBIFragmentDao sobiFragmentDao;

    /** --- Processor Instances --- */

    @Autowired
    private BillProcessor billProcessor;

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
        this.processorMap.put(SOBIFragmentType.COMMITTEE, new CommitteeProcessor());
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
        sobiFileDao.stageSOBIFiles(true);

        // TODO: Stage all hearings to the work directory.

        // TODO: Stage all transcripts
    }

    public void collate() throws IOException, ParseException {
        for (SOBIFile sobiFile : sobiFileDao.getPendingSOBIFiles(SortOrder.ASC)) {
            StringBuilder billBuffer = new StringBuilder();
            int fragmentCounter = 1;
            List<SOBIFragment> sobiFragments = new ArrayList<>();
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
                        SOBIFragment fragment = new SOBIFragment(sobiFile, fragmentType, xmlText, fragmentCounter++);
                        sobiFragments.add(fragment);
                    }
                }
            }
            if (billBuffer.length() > 0) {
                SOBIFragment billFragment = new SOBIFragment(sobiFile, SOBIFragmentType.BILL, billBuffer.toString(), 1);
                sobiFragments.add(billFragment);
            }
            // Persist the fragments
            sobiFragmentDao.saveSOBIFragments(sobiFragments);
        }
    }

    /**
     * Processes all staged/collated files in (mostly) chronological order by filename.
     * CMS.TXT, transcripts, and hearings may be processed in any order. The appropriate
     * file processor for each file is based on the directory they are stored in.
     *
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public void ingest() throws IOException {
        // Process SOBI files
        for (SOBIFile sobiFile : sobiFileDao.getPendingSOBIFiles(SortOrder.ASC)) {
            List<SOBIFragment> fragments = sobiFragmentDao.getSOBIFragments(sobiFile, SortOrder.ASC);
            ingestFragments(fragments);
            markSobiFileAsProcessed(sobiFile);
        }

        // TODO: Process Transcripts / Public Hearings
        // TODO: Handle CMS.TEXT (Rules file)
    }

    /**
     * Processes the SOBI Fragments using the registered implementations.
     * @param fragments List<SOBIFragment>
     */
    @Transactional
    private void ingestFragments(List<SOBIFragment> fragments) {
        for (SOBIFragment fragment : fragments) {
            if (processorMap.containsKey(fragment.getType())) {
                processorMap.get(fragment.getType()).process(fragment);
            }
            else {
                logger.warn("No processors have been registered to handle: " + fragment);
            }
        }
    }

    /**
     * Updates the status of the SOBIFile such that it is marked as processed.
     * @param sobiFile SOBIFile
     */
    private void markSobiFileAsProcessed(SOBIFile sobiFile) {
        sobiFile.incrementProcessedCount();
        sobiFile.setProcessedDateTime(new Date());
        sobiFile.setPendingProcessing(false);
        sobiFileDao.updateSOBIFile(sobiFile);
    }

    /**
     * Pushes the specified changes to the indicated services in the provided order.
     *
     * @param storage - The Storage object used for persistence
     * @param changes - A list of changes to push out to all services
     * @param services - A list of services to push change to.
     */
    public void push(Storage storage, List<Entry<String, Change>> changes, List<ServiceBase> services) {
        for(ServiceBase service : services) {
            try {
                service.process(changes, storage);
            } catch (Exception e) {
                // Services should all be independent so log the issue and move on
                logger.error("Service exception from "+service.getClass().getName(), e);
            }
        }
    }

    /**
     * Archives all files in the working directory to the archive directory split
     * up by year to avoid folder size limits and for easier grepping through the
     * archives when debugging data issues.
     *
     * @param workingDir - The directory containing all the work files.
     * @param archiveDir - The directory to archive all work files to.
     * @throws IOException
     */
    public void archive(File workingDir, File archiveDir) throws IOException {
        File rulesFile = new File(workingDir, "CMS.TEXT");
        if (rulesFile.exists()) {
            moveFileToDirectory(rulesFile, archiveDir, true);
        }

        File transcriptsArchiveDir = safeGetFolder(archiveDir, "transcripts");
        for (File file : safeListFiles(safeGetFolder(workingDir, "transcripts"), null, false)) {
            moveFileToDirectory(file, transcriptsArchiveDir, true);
        }

        File hearingsArchiveDir = safeGetFolder(archiveDir, "hearings");
        for (File file : safeListFiles(safeGetFolder(workingDir, "hearings"), null, false)) {
            moveFileToDirectory(file, hearingsArchiveDir, true);
        }

        archiveFiles(safeGetFolder(workingDir, "sobis"), archiveDir, "sobis");
        archiveFiles(safeGetFolder(workingDir, "bills"), archiveDir, "bills");
        archiveFiles(safeGetFolder(workingDir, "calendars"), archiveDir, "calendars");
        archiveFiles(safeGetFolder(workingDir, "committees"), archiveDir, "committees");
        archiveFiles(safeGetFolder(workingDir, "agendas"), archiveDir, "agendas");
        archiveFiles(safeGetFolder(workingDir, "annotations"), archiveDir, "annotations");
    }

    /** --- Internal Methods --- */

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
     * Attempts to use the SOBI file naming convention to properly archive files based
     * on the file type and the year that they were issued to us.
     *
     * e.g. 2013/bill/SOBI.D130628.T101200
     *
     * @param sourceDir - The directory with files to archive, non-recursive. Must exist.
     * @param destDir - The base directory to archive files to. Must exist.
     * @param subFolder - The name of the sub-directory to store the files. Cannot be null!
     * @throws IOException
     */
    protected void archiveFiles(File sourceDir, File destDir, String subFolder) throws IOException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");
        for (File file : safeListFiles(sourceDir, null, false)) {
            try {
                calendar.setTime(sobiDateFormat.parse(file.getName()));
                File finalDir = safeGetFolder(new File(destDir, String.valueOf(calendar.get(Calendar.YEAR))), subFolder);
                moveFileToDirectory(file, finalDir, true);
            }
            catch (ParseException e) {
                moveFileToDirectory(file, new File(destDir, subFolder), true);
            }
        }
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
