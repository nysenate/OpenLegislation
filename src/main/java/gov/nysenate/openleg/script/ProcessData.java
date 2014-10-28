package gov.nysenate.openleg.script;

import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.processor.DataProcessor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ProcessData extends BaseScript
{
    private static final Logger logger = LoggerFactory.getLogger(ProcessData.class);

    @Autowired
    private Environment env;

    @Autowired
    private DataProcessor dataProcessor;

    public static void main(String[] args) throws Exception {
        SCRIPT_NAME = "ProcessData";
        AnnotationConfigApplicationContext ctx = init();
        ProcessData processData = ctx.getBean(ProcessData.class);
        CommandLine cmd = getCommandLine(processData.getOptions(), args);
        processData.execute(cmd);
        shutdown(ctx);
    }

    protected Options getOptions() {
        Options options = new Options();
        options.addOption("c", "collate", false, "Will collate sobi files from the incoming data directories.");
        options.addOption("i", "ingest", false, "Will process all pending files.");
        options.addOption("l", "incremental", false, "Performs updates to the persistence layer with greater frequency. " +
                                                     "This will be disabled by default regardless of the property file settings.");
        options.addOption(null, "disable-indexing", false, "Disables updates to search indices during ingest if set.");
        options.addOption("h", "help", false, "Display help");
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception {
        if (opts.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SCRIPT_NAME, "\nThis CLI is used for collating and processing incoming data. " +
                            "By default, any incoming files will be collated and then ingested. " +
                            "To speed up sobi processing any database operations will be queued and " +
                            "performed in batches which will make the change log less granular. " +
                            "Use the incremental option if you want to have a full change log of updates.\n",
                    getOptions(), "", true);
            return;
        }
        boolean collate = opts.hasOption("collate");
        boolean ingest = opts.hasOption("ingest");
        boolean disableIndexing = opts.hasOption("disable-indexing");
        boolean incremental = opts.hasOption("incremental");

        if (disableIndexing) {
            env.setElasticIndexing(false);
        }
        env.setIncrementalUpdates(incremental);

        logger.info("Data processing settings: Incremental updates: {}, Allow search indexing: {}\n",
                     env.isIncrementalUpdates(), env.isElasticIndexing());

        if(!collate && !ingest) {
            dataProcessor.run();
        }
        else {
            if(collate) {
                dataProcessor.collate();
            }
            if(ingest) {
                dataProcessor.ingest();
            }
        }
    }
}
