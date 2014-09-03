package gov.nysenate.openleg.client.response.spotcheck;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.view.base.ViewList;
import gov.nysenate.openleg.client.view.spotcheck.ReportInfoView;

import java.time.LocalDate;

public class ReportSummaryResponse<ContentKey> extends BaseResponse
{
    protected ViewList<ReportInfoView<ContentKey>> reports;
    protected LocalDate fromDate;
    protected LocalDate toDate;

    public ReportSummaryResponse(ViewList<ReportInfoView<ContentKey>> reports, LocalDate fromDate, LocalDate toDate) {
        this.reports = reports;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.success = true;
    }

    public ViewList<ReportInfoView<ContentKey>> getReports() {
        return reports;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }
}
