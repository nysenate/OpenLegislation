package gov.nysenate.openleg.script;

import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.processor.DataProcessor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        Environment env = null;// FIXME Application.getEnvironment();
        DataProcessor process = new DataProcessor();
        for (String task : tasks) {
            switch (task) {
                case "collate": process.collate(); break;
                case "ingest": process.ingest(); break;
                default:
                    System.err.println("Invalid task.");
                    System.exit(1);
            }
        }
    }
}
