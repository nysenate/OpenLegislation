package gov.nysenate.openleg.script;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.search.BillSearchService;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class SearchIndexUpdate extends BaseScript
{
    private static final Logger logger = LoggerFactory.getLogger(SearchIndexUpdate.class);

    @Autowired
    private Environment env;

    @Autowired
    private BillDataService billDataService;

    @Autowired
    private BillSearchService billSearchService;

    public static void main(String[] args) throws Exception {
        SCRIPT_NAME = SearchIndexUpdate.class.getCanonicalName();
        AnnotationConfigApplicationContext ctx = init();
        SearchIndexUpdate searchIndexUpdate = ctx.getBean(SearchIndexUpdate.class);
        CommandLine cmd = getCommandLine(searchIndexUpdate.getOptions(), args);
        searchIndexUpdate.execute(cmd);
        shutdown(ctx);
    }

    @Override
    protected Options getOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "Help");
        options.addOption("d", "delete", false, "Delete Index");
        options.addOption("i", "index", false, "(Re)Index");
        options.addOption(null, "bills", false, "Bills");
        OptionGroup yearGroup = new OptionGroup();
        yearGroup.addOption(new Option("y", "year", true, "Year"));
        yearGroup.addOption(new Option("s", "session", true, "Session"));
        options.addOptionGroup(yearGroup);
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception {
        if (opts.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SCRIPT_NAME, "Manually push data to the search index", getOptions(), "", true);
        }
        boolean delete = (opts.hasOption("delete"));
        boolean index = (opts.hasOption("index"));

        if (!delete && !index) {
            System.err.println("You must specify if you want to perform a delete and/or index operation");
        }

        if (opts.hasOption("bills")) {
            if (delete) {
                logger.info("Deleting bill index");
            }
            if (index) {
                logger.info("Re index bills");
            }
        }
    }
}
