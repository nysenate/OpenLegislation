package gov.nysenate.openleg.script;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.service.spotcheck.DaybreakCheckReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SpotCheckCLI extends BaseScript
{
    private static final Logger logger = LoggerFactory.getLogger(SpotCheckCLI.class);

    @Autowired
    protected DaybreakCheckReportService daybreakService;

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
        options.addOption("s", "startDate", true, "Fetched reference data will be active after/on this date.");
        options.addOption("e", "endDate", true, "Fetched reference data will be active prior/on this date.");
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception {
        String startDateArg = opts.getOptionValue("s");
        String endDateArg = opts.getOptionValue("e");
        LocalDateTime startDate = (startDateArg != null) ? LocalDateTime.parse(startDateArg) : DateUtils.longAgo();
        LocalDateTime endDate = (endDateArg != null) ? LocalDateTime.parse(endDateArg) : LocalDateTime.now();
        SpotCheckReport<BaseBillId> daybreakReport =
            daybreakService.generateReport(startDate, endDate);
        logger.info("Mismatch statuses : {}", daybreakReport.getMismatchStatusCounts());
        logger.info("Mismatch types : {}", daybreakReport.getMismatchTypeCounts());
        daybreakService.saveReport(daybreakReport);
        logger.info("Reporting actions complete.");
    }
}