package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatus;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportSummary;

import java.util.Map;

public class ReportInfoView extends ReportIdView
{
    protected String notes;
    protected int observedCount;
    protected Map<SpotCheckMismatchStatus, Long> mismatchStatuses;
    protected Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> mismatchTypes;

    public ReportInfoView(SpotCheckReportSummary summary) {
        super(summary != null ? summary.getReportId() : null);
        if (summary != null) {
            this.notes = summary.getNotes();
            this.mismatchStatuses = summary.getMismatchStatuses();
            this.mismatchTypes = summary.getMismatchTypes().rowMap();
            this.observedCount = summary.getObservedCount();
        }
    }

    public ReportInfoView(SpotCheckReport<?> report) {
        super(report != null ? report.getReportId() : null);
        if (report != null) {
            this.notes = report.getNotes();
            this.mismatchStatuses = report.getMismatchStatusCounts();
            this.mismatchTypes = report.getMismatchTypeStatusCounts();
            this.observedCount = report.getObservations().size();
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
            .map(Map.Entry::getValue).reduce(Long::sum).orElse(0L);
    }

    public int getObservedCount() {
        return observedCount;
    }

    @Override
    public String getViewType() {
        return referenceType + " report-info";
    }
}