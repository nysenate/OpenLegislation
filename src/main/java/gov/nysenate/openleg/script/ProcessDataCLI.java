package gov.nysenate.openleg.script;

import gov.nysenate.openleg.config.Environment;
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
public class ProcessDataCLI extends BaseScript
{
    private static final Logger logger = LoggerFactory.getLogger(ProcessDataCLI.class);

    @Autowired private Environment env;
    @Autowired private DataProcessor dataProcessor;

    public static void main(String[] args) throws Exception {
        SCRIPT_NAME = ProcessDataCLI.class.getName();
        AnnotationConfigApplicationContext ctx = init();
        ProcessDataCLI processDataCLI = ctx.getBean(ProcessDataCLI.class);
        CommandLine cmd = getCommandLine(processDataCLI.getOptions(), args);
        processDataCLI.execute(cmd);
        shutdown(ctx);
    }

    protected Options getOptions() {
        Options options = new Options();
        options.addOption("c", "collate", false, "Collate files from the incoming data directories.");
        options.addOption("i", "ingest", false, "Process all pending files.");
        options.addOption(null, "batch-sobis", false, "Queue updates from SOBI files to minimize the number of writes.");
        options.addOption(null, "disable-indexing", false, "Disables updates to search indices during ingest if set.");
        options.addOption("h", "help", false, "Display help");
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception {
        if (opts.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SCRIPT_NAME,
                "\nThis CLI is used for collating and processing incoming data. " +
                "By default, any incoming files will be collated and then ingested. (i.e. -ci) \n",
                getOptions(), "", true);
            return;
        }

        if (!env.isProcessingEnabled()) {
            logger.error("Data processing is disabled! Set 'data.process.enabled' to 'true' in your app.properties.");
            return;
        }

        boolean collate = opts.hasOption("collate");
        boolean ingest = opts.hasOption("ingest");

        if (opts.hasOption("disable-indexing")) {
            env.setElasticIndexing(false);
        }

        env.setSobiBatchEnabled(opts.hasOption("batch-sobis"));

        logger.info("Data processing settings: \n" +
                    "Sobi Batch Processing: {}\n" +
                    "Search indexing: {}\n",
             env.isSobiBatchEnabled(), env.isElasticIndexing());

        if (!collate && !ingest) {
            dataProcessor.run(this.getClass().getName() + " script");
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
