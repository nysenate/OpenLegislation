package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatus;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckSummary;

import java.util.Map;

public class SpotCheckSummaryView implements ViewObject {

    protected Map<SpotCheckMismatchStatus, Long> mismatchStatuses;
    protected Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> mismatchTypes;
    protected Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> ignoredMismatchTypes;

    public SpotCheckSummaryView(SpotCheckSummary summary) {
        this.mismatchStatuses = summary.getMismatchStatuses();
        this.mismatchTypes = summary.getMismatchTypes().rowMap();
        this.ignoredMismatchTypes = summary.getIgnoredMismatchTypes().rowMap();
    }

    public Long getOpenMismatches() {
        return mismatchStatuses.entrySet().stream()
                .filter(e -> !e.getKey().equals(SpotCheckMismatchStatus.RESOLVED))
                .map(Map.Entry::getValue).reduce(Long::sum).orElse(0L);
    }

    public Map<SpotCheckMismatchStatus, Long> getMismatchStatuses() {
        return mismatchStatuses;
    }

    public Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> getMismatchTypes() {
        return mismatchTypes;
    }

    public Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> getIgnoredMismatchTypes() {
        return ignoredMismatchTypes;
    }

    @Override
    public String getViewType() {
        return "spotcheck-summary";
    }
}
