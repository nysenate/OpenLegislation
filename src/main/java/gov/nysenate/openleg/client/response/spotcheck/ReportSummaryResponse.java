package gov.nysenate.openleg.client.response.spotcheck;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.spotcheck.ReportInfoView;

import java.time.LocalDate;

public class ReportSummaryResponse<ContentKey> extends BaseResponse
{
    protected ListView<ReportInfoView<ContentKey>> reports;
    protected LocalDate fromDate;
    protected LocalDate toDate;

    public ReportSummaryResponse(ListView<ReportInfoView<ContentKey>> reports, LocalDate fromDate, LocalDate toDate) {
        this.reports = reports;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.success = true;
        this.responseType = (reports.getSize() > 0 ? reports.getItems().get(0).getReferenceType() : "empty") + " report summary";
    }

    public ListView<ReportInfoView<ContentKey>> getReports() {
        return reports;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }
}
