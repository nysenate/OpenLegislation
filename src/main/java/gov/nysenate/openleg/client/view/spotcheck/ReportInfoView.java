package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatus;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;

import java.time.LocalDateTime;
import java.util.Map;

public class ReportInfoView
{
    protected String referenceType;
    protected LocalDateTime reportDateTime;
    protected Map<SpotCheckMismatchStatus, Long> mismatchStatuses;
    protected Map<SpotCheckMismatchType, Integer> mismatchTypes;

    public ReportInfoView(SpotCheckReport report) {
        this.referenceType = report.getReferenceType().name();
        this.reportDateTime = report.getReportDateTime();
        this.mismatchStatuses = report.getMismatchStatusCounts();



    }
}
