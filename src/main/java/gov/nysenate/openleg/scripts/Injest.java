package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.processors.AgendaProcessor;
import gov.nysenate.openleg.processors.BillProcessor;
import gov.nysenate.openleg.processors.CalendarProcessor;
import gov.nysenate.openleg.services.Lucene;
import gov.nysenate.openleg.services.ServiceBase;
import gov.nysenate.openleg.services.Varnish;
import gov.nysenate.openleg.util.Config;
import gov.nysenate.openleg.util.Storage;
import gov.nysenate.openleg.util.Timer;
import gov.nysenate.openleg.util.serialize.XmlHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.UnmarshalException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Injest {

    public static Logger logger = Logger.getLogger(Injest.class);

    public static BillProcessor billProcessor = new BillProcessor();
    public static CalendarProcessor calendarProcessor = new CalendarProcessor();
    public static AgendaProcessor agendaProcessor = new AgendaProcessor();

    public static void main(String[] args) throws Exception {
        Options options = buildOptions();
        try {
            CommandLine line = new PosixParser().parse(options, args);
            if(line.hasOption("-h")) {
                new HelpFormatter().printHelp("posix", options );
                System.exit(0);
            }

            String sobiDir = line.hasOption("sobi-directory") ? line.getOptionValue("sobi-directory") : Config.get("data.sobi");
            String jsonDir = line.hasOption("json-directory") ? line.getOptionValue("json-directory") : Config.get("data.json");

            if( sobiDir == null || jsonDir == null ) {
                throw new org.apache.commons.cli.ParseException("sobi-directory and json-directory are both required parameters.");
            }

            Storage storage = new Storage(jsonDir);
            HashMap<String, Storage.Status> changeLog = storage.changeLog;
            ArrayList<ServiceBase> services = new ArrayList<ServiceBase>();

            if(line.hasOption("index")) {
                services.add(new Lucene(Config.get("data.lucene")));
            }

            if(line.hasOption("purge-cache")) {
                services.add(new Varnish("http://127.0.0.1", 80));
            }

            if(line.hasOption("generate-xml")) {
                XmlHelper.generateXml(sobiDir);
            }

            // Get the source file list and persistent storage layer
            if(line.hasOption("write")) {
                Timer timer = new Timer();
                timer.start();
                Collection<File> files = FileUtils.listFiles(new File(sobiDir), null, true);
                Collections.sort((List<File>)files);
                changeLog = injest(files, storage);
                logger.info(timer.stop()+" seconds to injest "+files.size()+" files.");
            }

            // Pass the change log through a set of service hooks
            // Currently there is a Lucene hook and varnish hook, more to come
            for(ServiceBase service:services) {
                try {
                    service.process(changeLog, storage);
                } catch (Exception e) {
                    logger.error("Fatal Error handling Service "+service.getClass().getName(), e);
                }
            }

            if(line.hasOption("clear-log")) {
                storage.clearLog();
            } else {
                storage.saveLog();
            }

        } catch( org.apache.commons.cli.ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        }
    }

    public static HashMap<String, Storage.Status> injest(Collection<File> files, Storage storage) throws Exception {
        // Process each file individually, flushing changes to storage as necessary
        // Each file processor should produce a change log indicating what happened.
        for(File file : files) {
            try {
                String fileName = file.getName();
                logger.debug(fileName);
                // process the file
                if(fileName.startsWith("SOBI") && fileName.endsWith(".TXT")) {
                    billProcessor.process(file, storage);
                } else if(fileName.contains("-calendar-")) {
                    calendarProcessor.process(file, storage);
                } else if(fileName.contains("-agenda-")){
                    agendaProcessor.process(file, storage);
                } else if(fileName.contains("-transcript-")){
                    continue;
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
        return storage.changeLog;
    }

    public static Options buildOptions() {
        Options options = new Options();

        options.addOption("sd", "sobi-directory", true, "The path to your SOBI directory");
        options.addOption("jd", "json-directory", true, "The path to your JSON directory");

        options.addOption("w", "write", false, "Write SOBI's in sobi-directory to JSON in json-directory");
        options.addOption("i", "index", false, "Index logged changes");
        options.addOption("pc", "purge-cache", false, "Purge cache while indexing");
        options.addOption("cl", "clear-log", false, "Clear the log after sending to services.");

        options.addOption("id", "index-document", true, "Index JSON document specified by argument (path to file)");
        options.addOption("dt", "document-type", true, "Type of document being indexed with -id (REQUIRED WITH -id).. (bill|calendar|agenda|transcript)");

        options.addOption("gx", "generate-xml", false, "Will pull XML data from SOBI documents");

        options.addOption("h", "help", false, "Print this message");
        return options;
    }
}
