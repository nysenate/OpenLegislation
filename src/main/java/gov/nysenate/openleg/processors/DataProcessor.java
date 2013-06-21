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
 * Usage:
 *  stage
 *  collate
 *  ingest
 *  push
 *  archive
 * @author graylinkim
 *
 */
public class DataProcessor
{
    public static String encoding = "CP850";

    Logger logger = Logger.getLogger(DataProcessor.class);

    public Collection<File> safeListFiles(File directory, String[] extensions, boolean recursive) throws IOException {
        FileUtils.forceMkdir(directory);
        return FileUtils.listFiles(directory, extensions, recursive);
    }

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
                    File calendarFile = new File(calendarDir, sobiFile.getName()+"-calendar-"+(fileCounter++)+".xml");
                    logger.info("Extracting calendar: "+calendarFile);
                    extractXml("</sencalendar.+", line, br, calendarFile);
                }
                else if(line.matches("<senagenda.+")) {
                    File agendaFile = new File(agendaDir, sobiFile.getName()+"-agenda-"+(fileCounter++)+".xml");
                    logger.info("Extracting agenda: "+agendaFile);
                    extractXml("</senagenda.+", line, br, agendaFile);
                }
                else if(line.matches("<senannotated.+")) {
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
                logger.error("Issue with "+file.getName(), e);
            } catch (JAXBException e) {
                logger.error("Issue with "+file.getName(), e);
            }
        }
        storage.flush();
    }

    public void push(Storage storage, List<Entry<String, Change>> changes, List<ServiceBase> services)
    {
        for(ServiceBase service : services) {
            try {
                service.process(changes, storage);
            } catch (Exception e) {
                logger.error("Fatal Error handling Service "+service.getClass().getName(), e);
            }
        }
    }

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

    public void archiveFiles(File sourceDir, File destDir, String subFolder) throws IOException
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

    public Collection<File> getSortedFiles(File directory, boolean recursive) throws IOException {
        Collection<File> files = safeListFiles(directory, null, recursive);
        Collections.sort((List<File>)files, new Comparator<File>(){
            public int compare(File a, File b) {
                return a.getName().compareTo(b.getName());
            }
        });
        return files;
    }

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
        String data = sb.append("</SENATEDATA>").toString();

        // TODO: Figure out all this matcher magic. How does it work?
        sb = new StringBuffer();
        Matcher m = Pattern.compile("<\\!\\[CDATA\\[(.*?)\\]\\]>").matcher(data);
        while(m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0).replaceAll("&newl;", "").replaceAll("\\\\n","\n")));
        }
        m.appendTail(sb);

        data = sb.toString().replaceAll("&newl;", "\n").replaceAll("(?!\n)\\p{Cntrl}","").replaceAll("(?!\\.{2})[ ]{2,}"," ");
        FileUtils.write(file, data);
    }
}
