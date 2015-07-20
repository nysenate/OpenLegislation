package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;

import java.time.LocalDateTime;

public class ReportIdView implements ViewObject{

    protected String referenceType;
    protected LocalDateTime referenceDateTime;
    protected LocalDateTime reportDateTime;

    public ReportIdView(SpotCheckReportId reportId) {
        if (reportId != null) {
            this.referenceType = reportId.getReferenceType() != null ? reportId.getReferenceType().name() : null;
            this.referenceDateTime = reportId.getReferenceDateTime() != null ? reportId.getReferenceDateTime() : null;
            this.reportDateTime = reportId.getReportDateTime() != null ? reportId.getReportDateTime() : null;
        }
    }

    @Override
    public String getViewType() {
        return "spotcheck-report-id";
    }

    public String getReferenceType() {
        return referenceType;
    }

    public LocalDateTime getReferenceDateTime() {
        return referenceDateTime;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }
}
