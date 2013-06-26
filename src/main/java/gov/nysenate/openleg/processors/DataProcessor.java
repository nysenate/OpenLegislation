package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.services.ServiceBase;
import gov.nysenate.openleg.util.Change;
import gov.nysenate.openleg.util.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

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
 *      {@link #collate(File) Collate}:
 *      Each SOBI file can be thought of as a collection of sub documents. The collate step
 *      extracts these files, converts them to UTF-8 encoding, and writes them to file.
 *      Writing to file is useful for debugging because it provides a record of what data
 *      was sent into each processor.
 *   </li>
 *   <li>
 *      {@link #ingest(File, Storage) Ingest}:
 *      Process collated files in chronological order based on the time stamp encoded into
 *      the SOBI file name. Delegates file processing based on document type.
 *   </li>
 *   <li>
 *      {@link #push(Storage, List, List) Push}:
 *      Pushes a set of changes out to a list of services.
 *   </li>
 *   <li>
 *      {@link #archive(File, File) Archive}:
 *      Archives all working files for reference when going through logs in the future.
 *   </li>
 * </ul>
 *
 * @author GraylinKim
 */
public class DataProcessor
{
    Logger logger = Logger.getLogger(DataProcessor.class);

    /**
     * SOBI.DYYMMDD.THHMMSS.TXT files are (mostly) in a CP850 or similar encoding. This
     * was determined from the byte mapping of paragraph/section characters to 244/245.
     */
    protected static String encoding = "CP850";

    /**
     * Any directory that we attempt to list files from should exist. If it doesn't then
     * make it so. This makes the processes robust against incomplete environment setups.
     *
     * @param directory - The directory to list files from (and create if necessary)
     * @param extensions - A list of extensions to grab. null for all extensions.
     * @param recursive - true when you want to list files recursively.
     * @return A collection of matching filenames
     * @throws IOException
     */
    public Collection<File> safeListFiles(File directory, String[] extensions, boolean recursive) throws IOException {
        FileUtils.forceMkdir(directory);
        return FileUtils.listFiles(directory, extensions, recursive);
    }

    /**
     * Stages CMS.TEXT, transcripts/, hearings/ and SOBI format files from the source
     * directory into their respective destinations subfolders in the work directory.
     *
     * @param sourceDir - The directory to pull files from
     * @param workDir - The directory to stage the files into
     * @throws IOException
     */
    public void stage(File sourceDir, File workDir) throws IOException
    {
        File rulesFile = new File(sourceDir,"CMS.TEXT");
        if (rulesFile.exists()) {
            FileUtils.moveFileToDirectory(rulesFile, workDir, true);
        }

        // Everything else on this level should be a SOBI file
        File sobiDir = new File(workDir, "sobis");
        for (File sobiFile : safeListFiles(sourceDir, null, false)) {
            FileUtils.moveFileToDirectory(sobiFile, sobiDir, true);
        }

        File hearingDir = new File(workDir, "hearings");
        for (File hearingFile : safeListFiles(new File(sourceDir, "hearings"), null, false)) {
            FileUtils.moveFileToDirectory(hearingFile, hearingDir, true);
        }

        File transcriptDir = new File(workDir, "transcripts");
        for (File transcriptFile : safeListFiles(new File(sourceDir, "transcripts"), null, false)) {
            FileUtils.moveFileToDirectory(transcriptFile, transcriptDir, true);
        }
    }

    /**
     * Looks for SOBI files in the sobis directory and extracts the various sub-documents
     * into properly encoded UTF-8 files in their respective directories for later
     * processing.
     *
     * @param workDir - The working directory with files to collate
     * @throws IOException
     */
    public void collate(File workDir) throws IOException
    {
        // Folders for our extracted sub documents. Extracting them and writing them to file
        // provides an easily inspectable record of how this step went.
        File billDir = new File(workDir, "bills");
        File agendaDir = new File(workDir, "agendas");
        File calendarDir = new File(workDir, "calendars");
        File annotationDir = new File(workDir, "annotations");

        for (File sobiFile : safeListFiles(new File(workDir, "sobis"), null, true)) {
            String line = null;
            int fileCounter = 1;
            StringBuffer billBuffer = new StringBuffer();
            BufferedReader br = new BufferedReader(new StringReader(FileUtils.readFileToString(sobiFile, encoding)));

            while((line = br.readLine()) != null) {
                if(line.matches("<sencalendar.+")) {
                    // Extract calendars and active lists
                    File calendarFile = new File(calendarDir, sobiFile.getName()+"-calendar-"+(fileCounter++)+".xml");
                    logger.info("Extracting calendar: "+calendarFile);
                    extractXml("</sencalendar.+", line, br, calendarFile);
                }
                else if(line.matches("<senagenda.+")) {
                    // Extract agendas and corresponding votes
                    File agendaFile = new File(agendaDir, sobiFile.getName()+"-agenda-"+(fileCounter++)+".xml");
                    logger.info("Extracting agenda: "+agendaFile);
                    extractXml("</senagenda.+", line, br, agendaFile);
                }
                else if(line.matches("<senannotated.+")) {
                    // Extract unused annotation files. We stopped getting these in 2009.
                    File annotationFile = new File(annotationDir, sobiFile.getName()+"-annotation-"+(fileCounter++)+".xml");
                    logger.info("Extracting annotation: "+annotationFile);
                    extractXml("</senannotated.+", line, br, annotationFile);
                }
                else if(line.matches("[0-9]{4}[A-Z][0-9]{5}[ A-Z].+")) {
                    if (line.charAt(11) == 'M') {
                        // Memos are latin1 encoding
                        line = new String(line.getBytes(encoding), "latin1");
                    }
                    billBuffer.append(line).append("\n");
                }
            }

            br.close();
            logger.info("Writing bill sobi to "+sobiFile);
            File billFile = new File(billDir, sobiFile.getName()+"-bill-"+(fileCounter++)+".sobi");
            FileUtils.write(billFile, billBuffer.toString());
        }
    }

    /**
     * Processes all staged/collated files in (mostly) chronological order by filename.
     * CMS.TXT, transcripts, and hearings may be processed in any order. The appropriate
     * file processor for each file is based on the directory they are stored in.
     *
     * @param workingDir - The working directory with collated files to process
     * @param storage - The Storage object to use for persistence.
     * @throws IOException
     */
    public void ingest(File workingDir, Storage storage) throws IOException
    {
        BillProcessor billProcessor = new BillProcessor();
        AgendaProcessor agendaProcessor = new AgendaProcessor();
        CalendarProcessor calendarProcessor = new CalendarProcessor();
        TranscriptProcessor transcriptProcessor = new TranscriptProcessor();

        for (File file : getSortedFiles(workingDir, true)) {
            try {
                String type = file.getParentFile().getName();
                if (type.equals("bills")) {
                    billProcessor.process(file, storage);
                } else if (type.equals("calendars")) {
                    calendarProcessor.process(file, storage);
                } else if (type.equals("agendas")) {
                    agendaProcessor.process(file, storage);
                } else if (type.equals("annotations")) {
                    continue; // we don't process or receive these anymore
                } else if (type.equals("transcripts")) {
                    transcriptProcessor.process(file, storage);
                } else if (type.equals("hearings")) {
                    continue; // we don't process or receive these yet.
                } else if (file.getName().equals("CMS.TEXT")) {
                    // The rules don't really need processing, just put them somewhere for later
                    FileUtils.copyFileToDirectory(file, storage.getStorageDir());
                }

                // To avoid memory issues, occasionally flush changes to file-system and truncate memory
                if (storage.memory.size() > 4000) {
                    storage.flush();
                    storage.clearCache();
                }
            }
            catch (IOException e) {
                logger.error("IO issue with "+file.getName(), e);
            } catch (JAXBException e) {
                logger.error("XML issue with "+file.getName(), e);
            }
        }
        storage.flush();
    }

    /**
     * Pushes the specified changes to the indicated services in the provided order.
     *
     * @param storage - The Storage object used for persistence
     * @param changes - A list of changes to push out to all services
     * @param services - A list of services to push change to.
     */
    public void push(Storage storage, List<Entry<String, Change>> changes, List<ServiceBase> services)
    {
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
    public void archive(File workingDir, File archiveDir) throws IOException
    {
        File rulesFile = new File(workingDir, "CMS.TEXT");
        if (rulesFile.exists()) {
            FileUtils.moveFileToDirectory(rulesFile, archiveDir, true);
        }

        File transcriptsArchiveDir = new File(archiveDir, "transcripts");
        for (File file : safeListFiles(new File(workingDir, "transcripts"), null, false)) {
            FileUtils.moveFileToDirectory(file, transcriptsArchiveDir, true);
        }

        File hearingsArchiveDir = new File(archiveDir, "hearings");
        for (File file : safeListFiles(new File(workingDir, "hearings"), null, false)) {
            FileUtils.moveFileToDirectory(file, hearingsArchiveDir, true);
        }

        archiveFiles(new File(workingDir, "sobis"), archiveDir, "sobis");
        archiveFiles(new File(workingDir, "bills"), archiveDir, "bills");
        archiveFiles(new File(workingDir, "calendars"), archiveDir, "calendars");
        archiveFiles(new File(workingDir, "agendas"), archiveDir, "agendas");
        archiveFiles(new File(workingDir, "annotations"), archiveDir, "annotations");
    }

    /**
     * Attempts to use the SOBI file naming convention to properly archive files based
     * on the year that they were issued to us.
     *
     * @param sourceDir - The directory with files to archive, non-recursive.
     * @param destDir - The base directory to archive files to.
     * @param subFolder - The name of the sub-directory to store the files. Cannot be null!
     * @throws IOException
     */
    protected void archiveFiles(File sourceDir, File destDir, String subFolder) throws IOException
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");
        for (File file : safeListFiles(sourceDir, null, false)) {
            try {
                calendar.setTime(sobiDateFormat.parse(file.getName()));
                File finalDir = new File(new File(destDir, String.valueOf(calendar.get(Calendar.YEAR))), subFolder);
                FileUtils.moveFileToDirectory(file, finalDir, true);
            }
            catch (ParseException e) {
                FileUtils.moveFileToDirectory(file, new File(destDir, subFolder), true);
            }
        }
    }

    /**
     * Returns a collection of files sorted by file name (not file path!)
     *
     * @param directory - The directory to get files from.
     * @param recursive - true to retrieve files from sub-directories.
     * @return
     * @throws IOException
     */
    protected Collection<File> getSortedFiles(File directory, boolean recursive) throws IOException {
        Collection<File> files = safeListFiles(directory, null, recursive);
        Collections.sort((List<File>)files, new Comparator<File>(){
            public int compare(File a, File b) {
                return a.getName().compareTo(b.getName());
            }
        });
        return files;
    }

    /**
     * Extracts a well formed XML document from a BufferedReader and writes it to the given
     * file. This depends strongly on escape sequences being on their own line; otherwise
     * we'll get malformed XML docs.
     *
     * @param escape - Regex matching the escape sequence for the XML document. e.g. {@literal </senagenda.+}
     * @param line - The starting line of the document
     * @param br - The buffered reader used to retrieve additional lines from the document
     * @param file - The file that the resulting XML document should be written to.
     * @throws IOException
     */
    protected void extractXml(String escape, String line, BufferedReader br, File file) throws IOException
    {
        StringBuffer sb = new StringBuffer(
            "<?xml version='1.0' encoding='UTF-8'?>&newl;" +
            "<SENATEDATA>&newl;" +
             line+"&newl;"
        );

        String in;
        while((in = br.readLine()) != null) {
            sb.append(in.replaceAll("\\xb9","&sect;") + "&newl;");
            if(in.matches(escape))
                break;
        }

        if (in == null) {
            // This is bad, but don't throw a flag. If the resulting XML document
            // is malformed we'll throw the exception during ingest.
            logger.error("Unterminated XML document: "+line);
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
        FileUtils.write(file, data);
    }
}
