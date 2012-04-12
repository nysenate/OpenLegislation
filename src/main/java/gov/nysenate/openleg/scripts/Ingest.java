package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.processors.AgendaProcessor;
import gov.nysenate.openleg.processors.BillProcessor;
import gov.nysenate.openleg.processors.CalendarProcessor;
import gov.nysenate.openleg.processors.TranscriptProcessor;
import gov.nysenate.openleg.util.Config;
import gov.nysenate.openleg.util.Storage;
import gov.nysenate.openleg.util.Storage.Status;
import gov.nysenate.openleg.util.Timer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.UnmarshalException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Ingest {
    public static Logger logger = Logger.getLogger(Ingest.class);

    public static BillProcessor billProcessor = new BillProcessor();
    public static CalendarProcessor calendarProcessor = new CalendarProcessor();
    public static AgendaProcessor agendaProcessor = new AgendaProcessor();
    public static TranscriptProcessor transcriptProcessor = new TranscriptProcessor();

    public static class FileNameComparator implements Comparator<File> {
        @Override
        public int compare(File a, File b) {
            return a.getName().compareTo(b.getName());
        }
    }

    public static void main(String[] args) throws Exception {
        CommandLine opts = null;
        try {
            Options options = new Options()
                .addOption("h", "help", false, "Print this message")
                //.addOption("dt", "document-type", true, "Type of document being indexed with -id (REQUIRED WITH -id).. (bill|calendar|agenda|transcript)")
                //.addOption("id", "index-document", true, "Index JSON document specified by argument (path to file)")
                .addOption("t", "storage", true, "The path to the storage directory")
                .addOption("s", "source", true, "The path to the files to ingest")
                .addOption("f", "change-file", true, "The path to store the changes");
            opts = new PosixParser().parse(options, args);
            if(opts.hasOption("-h")) {
                new HelpFormatter().printHelp("posix", options );
                System.exit(0);
            }
        } catch (ParseException e) {
            logger.fatal("Error parsing arguments: ", e);
            System.exit(0);
        }

        String sourceDir = opts.getOptionValue("source", Config.get("data.sobi"));
        String storageDir = opts.getOptionValue("storage", Config.get("data.json"));

        if( sourceDir == null || storageDir == null ) {
            throw new org.apache.commons.cli.ParseException("source and storage are both required parameters.");
        }
        Storage storage = new Storage(storageDir);


        Timer timer = new Timer();
        Collection<File> files = FileUtils.listFiles(new File(sourceDir), null, true);
        Collections.sort((List<File>)files, new FileNameComparator());

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
        for (Entry<String, Status> entry : storage.changeLog.entrySet()) {
            out.append(entry.getKey()+"\t"+entry.getValue()+"\n");
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
