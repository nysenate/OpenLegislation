package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatus;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ReportInfoView<ContentKey>
{
    protected String referenceType;
    protected LocalDateTime reportDateTime;
    protected Map<SpotCheckMismatchStatus, Integer> mismatchStatuses;
    protected Map<SpotCheckMismatchType, Integer> mismatchTypes;

    public ReportInfoView(SpotCheckReport<ContentKey> report) {
        if (report != null) {
            this.referenceType = report.getReferenceType().name();
            this.reportDateTime = report.getReportDateTime();
            this.mismatchStatuses = report.getMismatchStatusCounts();
            this.mismatchTypes = report.getMismatchTypeCounts();
        }
    }

    public String getReferenceType() {
        return referenceType;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }

    public Map<SpotCheckMismatchStatus, Integer> getMismatchStatuses() {
        return mismatchStatuses;
    }

    public Map<SpotCheckMismatchType, Integer> getMismatchTypes() {
        return mismatchTypes;
    }
}
