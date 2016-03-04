package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.*;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public SpotCheckReportSummary getSummary() {
        SpotCheckReportSummary summary = new SpotCheckReportSummary(reportId, notes);
        summary.addCountsFromObservations(observations.values());
        return summary;
    }

    /**
     * Get a count of open mismatches
     * @param ignored boolean - returns the count of ignored mismatches if true, which are not included if false
     * @return long
     */
    public long getOpenMismatchCount(boolean ignored) {
        return observations.values().stream()
                .map(obs -> obs.getMismatches().values().stream()
                        .filter(mismatch -> !mismatch.isIgnored() ^ ignored)
                        .filter(mismatch -> mismatch.getStatus() != SpotCheckMismatchStatus.RESOLVED)
                        .count()
                )
                .reduce(0L, (a, b) -> a + b);
    }

    /**
     * Get the number of mismatches across all observations grouped by the mismatch status.
     * @param ignored boolean - get the status count of ignored mismatches if true, which are not included if false
     * @return Map<SpotCheckMismatchStatus, Long>
     */
    public Map<SpotCheckMismatchStatus, Long> getMismatchStatusCounts(boolean ignored) {
        if (observations != null) {
            Map<SpotCheckMismatchStatus, Long> counts = new HashMap<>();
            for (SpotCheckMismatchStatus status : SpotCheckMismatchStatus.values()) {
                counts.put(status, 0L);
            }
            observations.values().stream()
                .flatMap(e -> e.getMismatchStatusCounts(ignored).entrySet().stream())
                .forEach(e -> counts.merge(e.getKey(), e.getValue(), Long::sum));
            return counts;
        }
        throw new IllegalStateException("The observations on this report have not yet been set.");
    }

    /**
     * Gets a count of mismatch types grouped by statuses across all observations.
     * @param ignored boolean - get type/status counts of ignored mismatches if true, which are not included if false
     * @return Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>>
     */
    public Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> getMismatchTypeStatusCounts(boolean ignored) {
        if (observations != null) {
            Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus, Long>> counts = new HashMap<>();
            observations.values().stream()
                .flatMap(e -> e.getMismatchStatusTypes(ignored).entrySet().stream())
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
     * @param ignored boolean - get status/type counts of ignored mismatches if true, which are not included if false
     * @return Map<SpotCheckMismatchStatus, Map<SpotCheckMismatchType, Long>>
     */
    public Map<SpotCheckMismatchStatus, Map<SpotCheckMismatchType, Long>> getMismatchStatusTypeCounts(boolean ignored) {
        if (observations != null) {
            Table<SpotCheckMismatchStatus, SpotCheckMismatchType, Long> countTable = HashBasedTable.create();
            observations.values().stream()
                    .flatMap(obs -> obs.getMismatchStatusTypes(ignored).entrySet().stream())
                    .forEach(entry -> {
                        long currentValue = Optional.ofNullable(countTable.get(entry.getValue(), entry.getKey()))
                                .orElse(0l);
                        countTable.put(entry.getValue(), entry.getKey(), currentValue + 1);
                    });
            return countTable.rowMap();
        }
        throw new IllegalStateException("The observations on this report have not yet been set.");
    }

    /**
     * Get the number of mismatches across all observations grouped by the mismatch type.
     * @param ignored boolean - get type counts of ignored mismatches if true, which are not included if false
     * @return Map<SpotCheckMismatchType, Long>
     */
    public Map<SpotCheckMismatchType, Long> getMismatchTypeCounts(boolean ignored) {
        if (observations != null) {
            Map<SpotCheckMismatchType, Long> counts = new HashMap<>();
            observations.values().stream()
                .flatMap(e -> e.getMismatchTypes(ignored).stream())
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

    /**
     * Get the number of content items observed
     */
    public int getObservedCount() {
        return Optional.ofNullable(this.observations).map(Map::size).orElse(0);
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