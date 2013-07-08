package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.model.Report;
import gov.nysenate.openleg.model.ReportError;
import gov.nysenate.openleg.model.ReportObservation;
import gov.nysenate.openleg.util.Application;

import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

public class CreateErrors extends BaseScript
{
    public static Logger logger = Logger.getLogger(CreateErrors.class);

    public static void main(String[] args) throws Exception
    {
        new CreateErrors().run(args);
    }

    protected void execute(CommandLine opts) throws Exception
    {
        BeanListHandler<Report> reportHandler = new BeanListHandler<Report>(Report.class);
        BeanListHandler<ReportError> reportErrorHandler = new BeanListHandler<ReportError>(ReportError.class);
        BeanListHandler<ReportObservation> reportObservationHandler = new BeanListHandler<ReportObservation>(ReportObservation.class);

        QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
        Collection<Report> reports = runner.query("SELECT * FROM report order by time ASC", reportHandler);
        for (Report report : reports) {
            logger.info("Generating errors for report: "+report);
            Collection<ReportError> oldOpenErrors = runner.query("SELECT * FROM report_error WHERE closedAt IS NULL" , reportErrorHandler);

            Collection<ReportObservation> observations = runner.query("SELECT * FROM report_observation WHERE reportId = ?", reportObservationHandler, report.getId());
            for (ReportObservation observation : observations) {
                boolean matched = false;
                for(ReportError error : oldOpenErrors) {
                    if (error.getOid().equals(observation.getOid()) && error.getField().equals(observation.getField())) {
                        // A matched error is still open so remove it from the "old" list
                        oldOpenErrors.remove(error);
                        matched = true;
                        break;
                    }
                }

                if (!matched) {
                    // Insert a new open error
                    runner.update("INSERT INTO report_error (oid, field, openedAt, closedAt) VALUES (?, ?, ?, NULL)", observation.getOid(), observation.getField(), report.getTime());
                }
            }

            // Close old open errors
            for (ReportError error : oldOpenErrors) {
                runner.update("UPDATE report_error SET closedAt = ? WHERE id = ?", report.getTime(), error.getId());
            }
        }
    }
}
