package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.processors.DataProcessor;
import gov.nysenate.openleg.services.Lucene;
import gov.nysenate.openleg.services.ServiceBase;
import gov.nysenate.openleg.services.UpdateReporter;
import gov.nysenate.openleg.services.Varnish;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.ChangeLogger;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Usage: bin/run.sh ProcessData --environment app.properties --tasks collate,ingest,push,archive --push-targets lucene,varnish,reporter --change-file logs/D20130511.T123500.change.log &>> logs/D20130511.T123500.process.log
 *
 * @author graylinkim
 *
 */
public class ProcessData extends BaseScript
{
    public static void main(String[] args) throws Exception {
        new ProcessData().run(args);
    }

    protected Options getOptions()
    {
        Options options =  new Options();
        options.addOption("f","change-file",true,"File to write the changes to.");
        options.addOption("t","tasks",true,"Comma separated list of tasks to perform in the specified environment: collate,ingest,push,archive");
        options.addOption("p","push-targets",true,"Comma separated list of push targets: lucene,reporter,varnish");
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception
    {

        String[] tasks = {};
        if (opts.hasOption("tasks")) {
            tasks = opts.getOptionValue("tasks").split(",\\s*");
        }
        else {
            System.err.println("-t|--tasks is a required options");
            this.printUsage(opts);
            System.exit(1);
        }

        File changeFile = null;
        if (opts.hasOption("change-file")) {
            changeFile = new File(opts.getOptionValue("change-file"));
            if (changeFile.exists() && (!changeFile.canWrite() || !changeFile.canRead())) {
                System.err.println("change-file must be readable and writable");
                System.exit(1);
            }
        }

        String[] pushTargets = {};
        if (opts.hasOption("push-targets")) {
            pushTargets = opts.getOptionValue("push-targets").split(",\\s*");
        }

        ArrayList<ServiceBase> services = new ArrayList<ServiceBase>();
        for (String target : pushTargets) {
            if (target.equalsIgnoreCase("lucene")) {
                services.add(new Lucene(Application.getConfig().getValue("lucene.directory")));
            }
            else if (target.equalsIgnoreCase("varnish")) {
                services.add(new Varnish("127.0.0.1", 80));
            }
            else if (target.equalsIgnoreCase("reporter")) {
                services.add(new UpdateReporter());
            }
            else {
                System.err.println("Invalid push target: "+target);
                System.exit(1);
            }
        }

        Environment env = Application.getEnvironment();
        DataProcessor process = new DataProcessor();
        for (String task : tasks) {
            if (task.equalsIgnoreCase("stage")) {
                process.stage(env.getStagingDirectory(), env.getWorkingDirectory());
            }
            else if (task.equalsIgnoreCase("collate")) {
                process.collate(env.getWorkingDirectory());
            }
            else if (task.equalsIgnoreCase("ingest")) {
                process.ingest(env.getWorkingDirectory(), Application.getStorage());
                if (changeFile != null) {
                    ChangeLogger.writeToFile(changeFile);
                }
            }
            else if (task.equalsIgnoreCase("push")) {
                if (ChangeLogger.getChangeLog().isEmpty()) {
                    if (changeFile != null) {
                        ChangeLogger.readFromFile(changeFile);
                    }
                    else {
                        System.err.println("Unable to push with an empty change log.");
                    }
                }
                process.push(Application.getStorage(), ChangeLogger.getChangeLog(), services);
            }
            else if (task.equalsIgnoreCase("archive")) {
                process.archive(env.getWorkingDirectory(), env.getArchiveDirectory());
            }
            else {
                System.err.println("Invalid task.");
                System.exit(1);
            }
        }
    }
}
