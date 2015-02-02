package gov.nysenate.openleg.client.response.spotcheck;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.spotcheck.ReportInfoView;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReportSummaryResponse<ContentKey> extends BaseResponse
{
    protected ListView<ReportInfoView<ContentKey>> reports;
    protected LocalDateTime fromDate;
    protected LocalDateTime toDate;

    public ReportSummaryResponse(ListView<ReportInfoView<ContentKey>> reports, LocalDateTime fromDate, LocalDateTime toDate) {
        this.reports = reports;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.success = true;
        this.responseType = (reports.getSize() > 0 ? reports.getItems().get(0).getReferenceType() : "empty") + " report summary";
    }

    public ListView<ReportInfoView<ContentKey>> getReports() {
        return reports;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }
}
