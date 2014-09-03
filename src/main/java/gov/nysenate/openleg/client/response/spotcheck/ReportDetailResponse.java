package gov.nysenate.openleg.client.response.spotcheck;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.view.spotcheck.ReportDetailView;
import gov.nysenate.openleg.client.view.spotcheck.ReportInfoView;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;

public class ReportDetailResponse<ContentKey> extends BaseResponse
{
    protected ReportInfoView<ContentKey> reportInfo;
    protected ReportDetailView<ContentKey> details;

    public ReportDetailResponse(SpotCheckReport<ContentKey> report) {
        if (report != null) {
            this.reportInfo = new ReportInfoView<>(report);
            this.details = new ReportDetailView<>(report);
            this.success = true;
        }
    }

    public ReportInfoView<ContentKey> getReportInfo() {
        return reportInfo;
    }

    public ReportDetailView<ContentKey> getDetails() {
        return details;
    }
}
