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
import java.util.Arrays;
import java.util.List;

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
    public static void main(String[] args) throws Exception
    {
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

    /**{@inheritDoc}*/
    @Override
    protected boolean luceneReadOnly() {
        return false;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception
    {
        List<String> tasks = new ArrayList<String>();
        if (opts.hasOption("tasks")) {
            tasks = Arrays.asList(opts.getOptionValue("tasks").toLowerCase().split(",\\s*"));
        }
        else {
            System.err.println("-t|--tasks is a required option");
            this.printUsage(opts);
            System.exit(1);
        }

        List<String> pushTargets = new ArrayList<String>();
        if (opts.hasOption("push-targets")) {
            pushTargets = Arrays.asList(opts.getOptionValue("push-targets").toLowerCase().split(",\\s*"));
        }
        else if (tasks.contains("push")) {
            System.err.println("-p|--push-targets is required when using the push task.");
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

        ArrayList<ServiceBase> services = new ArrayList<ServiceBase>();
        for (String target : pushTargets) {
            if (target.equals("lucene")) {
                services.add(new Lucene());
            }
            else if (target.equals("varnish")) {
                services.add(new Varnish("127.0.0.1", 80));
            }
            else if (target.equals("reporter")) {
                services.add(new UpdateReporter());
            }
            else {
                System.err.println("Invalid push target: "+target);
                this.printUsage(opts);
                System.exit(1);
            }
        }

        Environment env = Application.getEnvironment();
        DataProcessor process = new DataProcessor();
        for (String task : tasks) {
            // TODO: With Java7 we can make this a switch case
            if (task.equals("stage")) {
                process.stage(env.getStagingDirectory(), env.getWorkingDirectory());
            }
            else if (task.equals("collate")) {
                process.collate(env.getWorkingDirectory());
            }
            else if (task.equals("ingest")) {
                process.ingest(env.getWorkingDirectory(), Application.getStorage());
                if (changeFile != null) {
                    ChangeLogger.writeToFile(changeFile);
                }
            }
            else if (task.equals("push")) {
                if (ChangeLogger.getChangeLog().isEmpty()) {
                    if (changeFile != null) {
                        ChangeLogger.readFromFile(changeFile);
                    }
                    else {
                        System.err.println("Unable to push with an empty change log.");
                    }
                }
                process.push(Application.getStorage(), ChangeLogger.getEntries(), services);
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
