package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatus;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;

import java.time.LocalDateTime;
import java.util.Map;

public class ReportInfoView<ContentKey> extends ReportIdView
{
    protected String notes;
    protected Map<SpotCheckMismatchStatus, Long> mismatchStatuses;
    protected Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> mismatchTypes;

    public ReportInfoView(SpotCheckReport<ContentKey> report) {
        super(report != null ? report.getReportId() : null);
        if (report != null) {
            this.notes = report.getNotes();
            this.mismatchStatuses = report.getMismatchStatusCounts();
            this.mismatchTypes = report.getMismatchTypeStatusCounts();
        }
    }

    public String getNotes() {
        return notes;
    }

    public Map<SpotCheckMismatchStatus, Long> getMismatchStatuses() {
        return mismatchStatuses;
    }

    public Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> getMismatchTypes() {
        return mismatchTypes;
    }

    public Long getOpenMismatches() {
        return mismatchStatuses.entrySet().stream()
            .filter(e -> !e.getKey().equals(SpotCheckMismatchStatus.RESOLVED))
            .map(e -> e.getValue()).reduce(Long::sum).orElse(0L);
    }

    @Override
    public String getViewType() {
        return referenceType + " report-info";
    }
}