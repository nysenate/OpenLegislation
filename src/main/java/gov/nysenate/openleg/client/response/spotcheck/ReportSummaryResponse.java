package gov.nysenate.openleg.client.response.spotcheck;

import gov.nysenate.openleg.client.response.base.PaginationResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.spotcheck.ReportInfoView;
import gov.nysenate.openleg.dao.base.LimitOffset;

import java.time.LocalDateTime;

public class ReportSummaryResponse<ContentKey> extends PaginationResponse
{
    protected ListView<ReportInfoView> reports;
    protected LocalDateTime fromDate;
    protected LocalDateTime toDate;

    public ReportSummaryResponse(ListView<ReportInfoView> reports, LocalDateTime fromDate, LocalDateTime toDate,
                                 int total, LimitOffset limitOffset) {
        super(total, limitOffset);
        this.reports = reports;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.success = true;
        this.responseType = (reports.getSize() > 0 ? reports.getItems().get(0).getReferenceType() : "empty") + " report summary";
    }

    public ListView<ReportInfoView> getReports() {
        return reports;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }
}
