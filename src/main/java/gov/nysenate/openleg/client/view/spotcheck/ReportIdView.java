package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;

public class ReportIdView implements ViewObject{

    protected String referenceType;
    protected String referenceDateTime;
    protected String reportDateTime;

    public ReportIdView(SpotCheckReportId reportId) {
        if (reportId != null) {
            this.referenceType = reportId.getReferenceType() != null ? reportId.getReferenceType().name() : null;
            this.referenceDateTime = reportId.getReferenceDateTime() != null ? reportId.getReferenceDateTime().toString() : null;
            this.reportDateTime = reportId.getReportDateTime() != null ? reportId.getReportDateTime().toString() : null;
        }
    }

    @Override
    public String getViewType() {
        return "spotcheck-report-id";
    }

    public String getReferenceType() {
        return referenceType;
    }

    public String getReferenceDateTime() {
        return referenceDateTime;
    }

    public String getReportDateTime() {
        return reportDateTime;
    }
}
