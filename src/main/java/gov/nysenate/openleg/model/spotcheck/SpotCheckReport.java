package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A SpotCheckReport is basically a collection of observations that have 1 or more mismatches associated
 * within them. The ContentKey is templated to allow for reports on specific content types.
 *
 * @see SpotCheckReportService
 * @param <ContentKey>
 */
public class SpotCheckReport<ContentKey>
{
    /** Identifier for this report. */
    protected SpotCheckReportId reportId;

    /** All observations associated with this report. */
    protected Map<ContentKey, SpotCheckObservation<ContentKey>> observations = new HashMap<>();

    /** miscellaneous notes pertaining to this report */
    protected String notes;

    /** --- Constructors --- */

    public SpotCheckReport() {}

    public SpotCheckReport(SpotCheckReportId reportId) {
        this.reportId = reportId;
    }

    public SpotCheckReport(SpotCheckReportId reportId, String notes) {
        this(reportId);
        this.notes = notes;
    }

    /** --- Methods --- */

    public long getOpenMismatchCount() {
        return observations.values().stream()
                .map(obs -> obs.getMismatches().values().stream()
                        .filter(mismatch -> !SpotCheckMismatchStatus.RESOLVED.equals(mismatch.getStatus()))
                        .count()
                )
                .reduce(0L, (a, b) -> a + b);
    }

    /**
     * Get the number of mismatches across all observations grouped by the mismatch status.
     *
     * @return Map<SpotCheckMismatchStatus, Long>
     */
    public Map<SpotCheckMismatchStatus, Long> getMismatchStatusCounts() {
        if (observations != null) {
            Map<SpotCheckMismatchStatus, Long> counts = new HashMap<>();
            for (SpotCheckMismatchStatus status : SpotCheckMismatchStatus.values()) {
                counts.put(status, 0L);
            }
            observations.values().stream()
                .flatMap(e -> e.getMismatchStatusCounts().entrySet().stream())
                .forEach(e -> counts.merge(e.getKey(), e.getValue(), Long::sum));
            return counts;
        }
        throw new IllegalStateException("The observations on this report have not yet been set.");
    }

    /**
     * Gets a count of mismatch types grouped by statuses across all observations.
     *
     * @return Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>>
     */
    public Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> getMismatchTypeStatusCounts() {
        if (observations != null) {
            Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> counts = new HashMap<>();
            observations.values().stream()
                .flatMap(e -> e.getMismatchStatusTypes().entrySet().stream())
                .forEach(e -> {
                    if (!counts.containsKey(e.getKey())) {
                        counts.put(e.getKey(), new HashMap<>());
                    }
                    counts.get(e.getKey()).merge(e.getValue(), 1L, (a,b) -> a + 1L);
                });
            return counts;
        }
        throw new IllegalStateException("The observations on this report have not yet been set.");
    }

    /**
     * Gets a count of mismatch statuses grouped by type across all observations.
     *
     * @return Map<SpotCheckMismatchStatus, Map<SpotCheckMismatchType, Long>>
     */
    public Map<SpotCheckMismatchStatus, Map<SpotCheckMismatchType, Long>> getMismatchStatusTypeCounts() {
        if (observations != null) {
            Map<SpotCheckMismatchStatus, Map<SpotCheckMismatchType, Long>> counts = new HashMap<>();
            observations.values().stream()
                    .flatMap(e -> e.getMismatchStatusTypes().entrySet().stream())
                    .forEach(e -> {
                        if (!counts.containsKey(e.getValue())) {
                            counts.put(e.getValue(), new HashMap<>());
                        }
                        long currentCount = 0;
                        if (counts.get(e.getValue()).containsKey(e.getKey())) {
                            currentCount = counts.get(e.getValue()).get(e.getKey());
                        }
                        counts.get(e.getValue()).merge(e.getKey(), 1L, (a, b) -> a + 1L);
                    });
            return counts;
        }
        throw new IllegalStateException("The observations on this report have not yet been set.");
    }

    /**
     * Get the number of mismatches across all observations grouped by the mismatch type.
     *
     * @return Map<SpotCheckMismatchType, Long>
     */
    public Map<SpotCheckMismatchType, Long> getMismatchTypeCounts() {
        if (observations != null) {
            Map<SpotCheckMismatchType, Long> counts = new HashMap<>();
            observations.values().stream()
                .flatMap(e -> e.getMismatchTypes().stream())
                .forEach(e -> counts.merge(e, 1L, (a,b) -> a + 1L));
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

    public LocalDateTime getReferenceDateTime() {
        return reportId.getReferenceDateTime();
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}