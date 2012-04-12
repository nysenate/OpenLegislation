package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.services.Lucene;
import gov.nysenate.openleg.services.ServiceBase;
import gov.nysenate.openleg.services.Varnish;
import gov.nysenate.openleg.util.Config;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Push {
    private static Logger logger = Logger.getLogger(Push.class);

    public static HashMap<String, Storage.Status> parseChanges(Iterable<String> lines) {
        HashMap<String, Storage.Status> changes = new HashMap<String, Storage.Status>();
        for (String line : lines) {
            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                logger.fatal("Malformed change line: "+line);
                System.exit(0);
            }
            changes.put(parts[0], Storage.Status.valueOf(parts[1].toUpperCase()));
        }
        return changes;
    }

    public static void main(String[] args) {
        CommandLine opts = null;
        try {
            Options options = new Options()
                .addOption("t", "storage", true, "The path to the storage directory")
                .addOption("l", "lucene", false, "Push changes to the Lucene service")
                .addOption("v", "varnish", false, "Push changes to the Varnish service")
                .addOption("f", "change-file", true, "Path of changeLog file.")
                .addOption("c", "changes", true, "A newline delimited list of changes")
                .addOption("h", "help", false, "Print this message");
            opts = new PosixParser().parse(options, args);
            if(opts.hasOption("-h")) {
                new HelpFormatter().printHelp("posix", options );
                System.exit(0);
            }
        } catch (ParseException e) {
            logger.fatal("Error parsing arguments: ", e);
            System.exit(0);
        }

        // Parse the specified changes into a hash
        HashMap<String, Storage.Status> changes = null;
        if (opts.hasOption("change-file")) {
            try {
                File changeFile = new File(opts.getOptionValue("change-file"));
                changes = parseChanges(FileUtils.readLines(changeFile, "UTF-8"));
            } catch (IOException e) {
                logger.fatal("Error reading change-file: "+opts.getOptionValue("changes"), e);
                System.exit(0);
            }
        } else if (opts.hasOption("changes")) {
            changes = parseChanges(Arrays.asList(opts.getOptionValue("changes").split("\n")));
        } else {
            logger.fatal("Nothing to do. Specify changes or a change-file");
            System.exit(0);
        }

        String storageDir = opts.getOptionValue("storage", Config.get("data.json"));
        Storage storage = new Storage(storageDir);

        // Currently there is a Lucene hook and varnish hook, more to come
        ArrayList<ServiceBase> services = new ArrayList<ServiceBase>();
        if(opts.hasOption("lucene")) {
            services.add(new Lucene(Config.get("data.lucene")));
        }

        if(opts.hasOption("varnish")) {
            services.add(new Varnish("http://127.0.0.1", 80));
        }

        // Pass the change log through a set of service hooks
        for(ServiceBase service:services) {
            try {
                service.process(changes, storage);
            } catch (Exception e) {
                logger.error("Fatal Error handling Service "+service.getClass().getName(), e);
            }
        }
    }

}
