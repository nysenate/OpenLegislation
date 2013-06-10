package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.services.Lucene;
import gov.nysenate.openleg.services.ServiceBase;
import gov.nysenate.openleg.services.UpdateReporter;
import gov.nysenate.openleg.services.Varnish;
import gov.nysenate.openleg.util.Change;
import gov.nysenate.openleg.util.ChangeLogger;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

public class Push extends BaseScript
{
    private static Logger logger = Logger.getLogger(Push.class);

    public static void main(String[] args) throws Exception
    {
        new Push().run(args);
    }

    protected Options getOptions()
    {
        Options options = new Options();
        options.addOption("l", "lucene", true, "Push changes to the Lucene service");
        options.addOption("v", "varnish", false, "Push changes to the Varnish service");
        options.addOption("u", "updateReporter", false, "Push updates to html");
        options.addOption("f", "change-file", true, "Path of changeLog file.");
        options.addOption("c", "changes", true, "A newline delimited list of changes");
        options.addOption("h", "help", false, "Print this message");
        return options;
    }

    protected void execute(CommandLine opts)
    {
        String[] required = opts.getArgs();
        if (required.length != 1) {
            System.err.println("Storage is a required argument.");
            printUsage(opts);
            System.exit(1);
        }

        // Parse the specified changes into a hash
        HashMap<String, Change> changes = null;
        if (opts.hasOption("change-file")) {
            try {
                File changeFile = new File(opts.getOptionValue("change-file"));
                ChangeLogger.readFromFile(changeFile);
                changes = ChangeLogger.getChangeLog();
            } catch (IOException e) {
                System.err.println("Error reading change-file: "+opts.getOptionValue("changes"));
                System.exit(1);
            }
        } else if (opts.hasOption("changes")) {
            changes = ChangeLogger.parseChanges(Arrays.asList(opts.getOptionValue("changes").split("\n")));
        } else {
            System.err.println("Changes to push must be specified with either --change-file or --changes");
            System.exit(1);
        }

        // Currently there is a Lucene hook and varnish hook, more to come
        ArrayList<ServiceBase> services = new ArrayList<ServiceBase>();
        if(opts.hasOption("lucene")) {
            services.add(new Lucene(opts.getOptionValue("lucene")));
        }

        if(opts.hasOption("varnish")) {
            services.add(new Varnish("127.0.0.1", 80));
        }

        if(opts.hasOption("updateReporter")) {
            services.add(new UpdateReporter());
        }

        // Pass the change log through a set of service hooks
        Storage storage = new Storage(required[0]);
        for(ServiceBase service:services) {
            try {
                service.process(changes, storage);
            } catch (Exception e) {
                logger.error("Fatal Error handling Service "+service.getClass().getName(), e);
            }
        }
    }

}
