package gov.nysenate.openleg.script;

import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import gov.nysenate.openleg.service.spotcheck.daybreak.DaybreakCheckMailService;
import gov.nysenate.openleg.service.spotcheck.daybreak.DaybreakSpotcheckProcessService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpotCheckCLI extends BaseScript
{
    private static final Logger logger = LoggerFactory.getLogger(SpotCheckCLI.class);

    @Autowired
    protected DaybreakSpotcheckProcessService spotcheckRunService;

    @Autowired
    protected DaybreakCheckMailService checkMailService;

    @Autowired
    protected DaybreakProcessService daybreakProcessService;

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx = init();
        SpotCheckCLI spotCheckCLI = ctx.getBean(SpotCheckCLI.class);
        CommandLine cmd = getCommandLine(spotCheckCLI.getOptions(), args);
        spotCheckCLI.execute(cmd);
        shutdown(ctx);
    }

    @Override
    protected Options getOptions() {
        Options options = new Options();
        // If no action options are set then the full spotcheck process will execute
        options.addOption("m", "check-mail", false, "Will check the email account for daybreak messages if set");
        options.addOption("c", "collate", false, "Will collate all incoming daybreak files if set");
        options.addOption("i", "ingest", false, "Will ingest all unprocessed daybreak fragments if set");
        options.addOption("r", "report", false, "Will run a report for the latest unchecked daybreak references if set");
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception {
        boolean checkMail = opts.hasOption("check-mail");
        boolean collate = opts.hasOption("collate");
        boolean ingest = opts.hasOption("ingest");
        boolean report = opts.hasOption("report");
        if (checkMail || collate || ingest || report) {
            if (checkMail) {
                checkMailService.checkMail();
            }
            if (collate) {
                daybreakProcessService.collate();
            }
            if (ingest) {
                daybreakProcessService.ingest();
            }
            if (report) {
                // TODO: call report running implementation
            }
        } else {
            // TODO: call report running implementation
        }
    }
}