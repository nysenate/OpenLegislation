package gov.nysenate.openleg.spotchecks;

import gov.nysenate.openleg.common.script.BaseScript;
import gov.nysenate.openleg.spotchecks.daybreak.DaybreakCheckMailService;
import gov.nysenate.openleg.spotchecks.daybreak.process.DaybreakProcessService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpotCheckCLI extends BaseScript {
    private final DaybreakCheckMailService checkMailService;
    private final DaybreakProcessService daybreakProcessService;

    @Autowired
    public SpotCheckCLI(DaybreakCheckMailService checkMailService,
                        DaybreakProcessService daybreakProcessService) {
        this.checkMailService = checkMailService;
        this.daybreakProcessService = daybreakProcessService;
    }

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