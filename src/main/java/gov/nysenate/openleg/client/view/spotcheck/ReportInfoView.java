package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatus;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;

import java.time.LocalDateTime;
import java.util.Map;

public class ReportInfoView<ContentKey> implements ViewObject
{
    protected String referenceType;
    protected LocalDateTime referenceDateTime;
    protected LocalDateTime reportDateTime;
    protected Map<SpotCheckMismatchStatus, Long> mismatchStatuses;
    protected Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> mismatchTypes;

    public ReportInfoView(SpotCheckReport<ContentKey> report) {
        if (report != null) {
            this.referenceType = report.getReferenceType().name();
            this.referenceDateTime = report.getReferenceDateTime();
            this.reportDateTime = report.getReportDateTime();
            this.mismatchStatuses = report.getMismatchStatusCounts();
            this.mismatchTypes = report.getMismatchTypeStatusCounts();
        }
    }

    public String getReferenceType() {
        return referenceType;
    }

    public LocalDateTime getReferenceDateTime() {
        return referenceDateTime;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
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