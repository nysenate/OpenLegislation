package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    protected Map<ContentKey, SpotCheckObservation<ContentKey>> observations = new HashMap<>();

    /** All observations/mismatches that have been resolved since prior report of same type. */

    /** --- Constructors --- */

    public SpotCheckReport() {}

    public SpotCheckReport(SpotCheckReportId reportId) {
        this.reportId = reportId;
    }

    /** --- Methods --- */

    /**
     * Get the number of mismatches across all observations grouped by the mismatch status.
     *
     * @return Map<SpotCheckMismatchStatus, Integer>
     */
    public Map<SpotCheckMismatchStatus, Integer> getMismatchStatusCounts() {
        if (observations != null) {
            Map<SpotCheckMismatchStatus, Integer> counts = new HashMap<>();
            for (SpotCheckObservation<ContentKey> obs : observations.values()) {
                Map<SpotCheckMismatchStatus, Long> obsCount = obs.getMismatchStatusCounts();
                for (SpotCheckMismatchStatus status : obsCount.keySet()) {
                    if (!counts.containsKey(status)) {
                        counts.put(status, obsCount.get(status).intValue());
                    }
                    else {
                        counts.put(status, counts.get(status) + obsCount.get(status).intValue());
                    }
                }
            }
            return counts;
        }
        throw new IllegalStateException("The observations on this report have not yet been set.");
    }

    /**
     * Get the number of mismatches across all observations grouped by the mismatch type.
     *
     * @return Map<SpotCheckMismatchType, Integer>
     */
    public Map<SpotCheckMismatchType, Integer> getMismatchTypeCounts() {
        if (observations != null) {
            Map<SpotCheckMismatchType, Integer> counts = new HashMap<>();
            for (SpotCheckObservation<ContentKey> obs : observations.values()) {
                obs.getMismatchTypes().forEach(t -> {
                    if (!counts.containsKey(t)) {
                        counts.put(t, 0);
                    }
                    counts.put(t, counts.get(t) + 1);
                });
            }
            return counts;
        }
        throw new IllegalStateException("The observations on this report have not yet been set.");
    }

    /**
     * Add the observation to the map.
     */
    public void addObservation(SpotCheckObservation<ContentKey> observation) {
        if (observation == null) {
            throw new IllegalArgumentException("Supplied observation cannot be null");
        }
        this.observations.put(observation.getKey(), observation);

    }

    /**
     * Add the collection of observations to the map.
     */
    public void addObservations(Collection<SpotCheckObservation<ContentKey>> observations) {
        if (observations == null) {
            throw new IllegalArgumentException("Supplied observation collection cannot be null");
        }
        observations.forEach(this::addObservation);
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