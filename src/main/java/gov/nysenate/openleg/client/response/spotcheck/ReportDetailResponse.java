package gov.nysenate.openleg.client.response.spotcheck;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.view.spotcheck.ReportDetailView;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;

public class ReportDetailResponse<ContentKey> extends BaseResponse
{
    protected ReportDetailView<ContentKey> details;

    public ReportDetailResponse(SpotCheckReport<ContentKey> report) {
        if (report != null) {
            this.details = new ReportDetailView<>(report);
            this.success = true;
            this.responseType = details.getViewType();
        }
    }

    public ReportDetailView<ContentKey> getDetails() {
        return details;
    }
}
