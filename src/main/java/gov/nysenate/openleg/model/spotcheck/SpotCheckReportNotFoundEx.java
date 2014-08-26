package gov.nysenate.openleg.model.spotcheck;

/**
 * Exception to throw when a report is requested but does not exist in the backing store.
 */
public class SpotCheckReportNotFoundEx extends RuntimeException
{
    protected SpotCheckReportId reportId;

    public SpotCheckReportNotFoundEx(SpotCheckReportId reportId) {
        super("No matching spot check report with id " + reportId + " could be found.");
    }
}
