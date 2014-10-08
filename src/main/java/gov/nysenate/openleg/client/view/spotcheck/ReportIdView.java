package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;

public class ReportIdView implements ViewObject{

    protected String referenceType;
    protected String referenceDateTime;
    protected String reportDateTime;

    public ReportIdView(SpotCheckReportId reportId) {
        this.referenceType = reportId.getReferenceType().name();
        this.referenceDateTime = reportId.getReferenceDateTime().toString();
        this.reportDateTime = reportId.getReportDateTime().toString();
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
