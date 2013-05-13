package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.processors.AgendaProcessor;
import gov.nysenate.openleg.processors.BillProcessor;
import gov.nysenate.openleg.processors.CalendarProcessor;
import gov.nysenate.openleg.processors.TranscriptProcessor;
import gov.nysenate.openleg.util.Change;
import gov.nysenate.openleg.util.ChangeLogger;
import gov.nysenate.openleg.util.Storage;
import gov.nysenate.openleg.util.Timer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.UnmarshalException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Ingest extends BaseScript
{
    protected static Logger logger = Logger.getLogger(Ingest.class);

    protected String SCRIPT_NAME = "Ingest";
    protected String USAGE = "USAGE: Ingest SOURCE STORAGE [--change-file FILE]";

    public static void main(String[] args) throws Exception
    {
        new Ingest().run(args);
    }

    public static class FileNameComparator implements Comparator<File> {
        @Override
        public int compare(File a, File b) {
            return a.getName().compareTo(b.getName());
        }
    }

    protected Options getOptions()
    {
        Options options = new Options();
        options.addOption("f", "change-file", true, "The path to store the changes");
        // options.addOption("dt", "document-type", true, "Type of document being indexed with -id (REQUIRED WITH -id).. (bill|calendar|agenda|transcript)");
        // options.addOption("id", "index-document", true, "Index JSON document specified by argument (path to file)");
        return options;
    }

    protected void execute(CommandLine opts) throws Exception
    {
        String[] required = opts.getArgs();
        if (required.length != 2) {
            System.err.println("Both source and storage directories are required.");
            printUsage(opts);
            System.exit(1);
        }

        Timer timer = new Timer();
        Storage storage = new Storage(required[1]);
        Collection<File> files = FileUtils.listFiles(new File(required[0]), null, true);
        Collections.sort((List<File>)files, new FileNameComparator());

        BillProcessor billProcessor = new BillProcessor();
        CalendarProcessor calendarProcessor = new CalendarProcessor();
        AgendaProcessor agendaProcessor = new AgendaProcessor();
        TranscriptProcessor transcriptProcessor = new TranscriptProcessor();

        // Process each file individually, flushing changes to storage as necessary
        // Each file processor should produce a change log indicating what happened
        timer.start();
        for(File file : files) {
            try {
                logger.debug("Ingesting: "+file);
                String type = file.getParentFile().getName();
                if (type.equals("bills")) {
                    billProcessor.process(file, storage);
                } else if (type.equals("calendars")) {
                    calendarProcessor.process(file, storage);
                } else if (type.equals("agendas")) {
                    agendaProcessor.process(file, storage);
                } else if (type.equals("annotations")) {
                    continue;
                } else if (type.equals("transcripts")) {
                    transcriptProcessor.process(file, storage);
                }

                // To avoid memory issues, occasionally flush changes to file-system and truncate memory
                if (storage.memory.size() > 4000) {
                    storage.flush();
                    storage.clearCache();
                }

            } catch (IOException e) {
                logger.error("Issue with "+file.getName(), e);
            } catch (UnmarshalException e) {
                logger.error("Issue with "+file.getName(), e);
            }
        }
        storage.flush();
        logger.info(timer.stop()+" seconds to injest "+files.size()+" files.");

        // Dump out the change log
        StringBuffer out = new StringBuffer();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        for (Entry<String, Change> entry : ChangeLogger.getChangeLog().entrySet()) {
            date = entry.getValue().getDate();
            if (date != null) {
                out.append(entry.getKey()+"\t"+entry.getValue().getStatus()+"\t"+sdf.format(date).toString()+"\n");
            } else {
                // TODO temporary solution.
                // If no date information available, set date to current time.
                out.append(entry.getKey()+"\t"+entry.getValue().getStatus()+"\t"+sdf.format(new Date()).toString() +"\n");
            }
        }

        if (opts.hasOption("change-file")) {
            try {
                FileUtils.write(new File(opts.getOptionValue("change-file")), out);
            } catch (IOException e) {
                logger.error("Could not open changeLog for writing", e);
            }
        } else {
            System.out.print(out);
        }
    }
}
