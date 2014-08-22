package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A SpotCheckReport is basically a collection of observations that have 1 or more mismatches associated
 * within them. The ContentKey is templated to allow for reports on specific content types.
 *
 * @see gov.nysenate.openleg.service.spotcheck.SpotCheckReportService
 * @param <ContentKey>
 */
public class SpotCheckReport<ContentKey>
{
    /** Identifier for this report. */
    protected SpotCheckReportId reportId;

    /** All observations associated with this report. */
    protected Map<ContentKey, SpotCheckObservation<ContentKey>> observations;

    /** --- Constructors --- */

    public SpotCheckReport() {}

    /** --- Methods --- */

    /**
     * Get the number of mismatches grouped by the mismatch status. This count is
     * computed over all the observations contained in this report.
     *
     * @return Map<SpotCheckMismatchStatus, Long>
     */
    public Map<SpotCheckMismatchStatus, Long> getMismatchStatusCounts() {
        if (observations != null) {
            Map<SpotCheckMismatchStatus, Long> counts = new HashMap<>();
            for (SpotCheckObservation obs : observations.values()) {
                Map<SpotCheckMismatchStatus, Long> obsCount = obs.getMismatchStatusCounts();
                for (SpotCheckMismatchStatus status : obsCount.keySet()) {
                    if (!counts.containsKey(status)) {
                        counts.put(status, obsCount.get(status));
                    }
                    else {
                        counts.put(status, counts.get(status) + obsCount.get(status));
                    }
                }
            }
            return counts;
        }
        throw new IllegalStateException("The observations on this report have not yet been set.");
    }

    /** --- Delegates --- */

    public LocalDateTime getReportDateTime() {
        return reportId.getReportDateTime();
    }

    public SpotCheckRefType getReferenceType() {
        return reportId.getReferenceType();
    }

    /** --- Basic Getters/Setters --- */

    public SpotCheckReportId getReportId() {
        return reportId;
    }

    public void setReportId(SpotCheckReportId reportId) {
        this.reportId = reportId;
    }

    public Map<ContentKey, SpotCheckObservation<ContentKey>> getObservations() {
        return observations;
    }

    public void setObservations(Map<ContentKey, SpotCheckObservation<ContentKey>> observations) {
        this.observations = observations;
    }
}