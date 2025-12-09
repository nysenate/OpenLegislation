package gov.nysenate.openleg.spotchecks.model;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import gov.nysenate.openleg.spotchecks.base.SpotCheckReportService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A SpotCheckReport is basically a collection of observations that have 1 or more mismatches associated
 * within them. The ContentKey is templated to allow for reports on specific content types.
 *
 * @see SpotCheckReportService
 * @param <ContentKey>
 */
public class SpotCheckReport<ContentKey> {
    /** Auto increment id */
    private int id;

    /** Identifier for this report. */
    private SpotCheckReportId reportId;

    /** Map of all observations associated with this report. */
    private final Map<ContentKey, SpotCheckObservation<ContentKey>> observationMap = new HashMap<>();

    /** miscellaneous notes pertaining to this report */
    private String notes;

    /** --- Constructors --- */

    public SpotCheckReport() {}

    public SpotCheckReport(SpotCheckReportId reportId) {
        this.reportId = reportId;
    }

    /**
     * Get a count of open mismatches
     * @param ignored boolean - returns the count of ignored mismatches if true, which are not included if false
     * @return long
     */
    public long getOpenMismatchCount(boolean ignored) {
        return observationMap.values().stream()
                .map(obs -> obs.getMismatches().values().stream()
                        .filter(mismatch -> !mismatch.isIgnored() ^ ignored)
                        .filter(mismatch -> mismatch.getState() == MismatchState.OPEN)
                        .count()
                )
                .reduce(0L, Long::sum);
    }

    /**
     * Gets a count of mismatch statuses grouped by type across all observations.
     * @param ignored boolean - get status/type counts of ignored mismatches if true, which are not included if false
     * @return Map<SpotCheckMismatchStatus, Map<SpotCheckMismatchType, Long>>
     */
    public Map<MismatchState, Map<SpotCheckMismatchType, Long>> getMismatchStatusTypeCounts(boolean ignored) {
        Table<MismatchState, SpotCheckMismatchType, Long> countTable = HashBasedTable.create();
        observationMap.values().stream()
                .flatMap(obs -> obs.getMismatchStatusTypes(ignored).entrySet().stream())
                .forEach(entry -> {
                    long currentValue = Optional.ofNullable(countTable.get(entry.getValue(), entry.getKey()))
                            .orElse(0L);
                    countTable.put(entry.getValue(), entry.getKey(), currentValue + 1);
                });
        return countTable.rowMap();
    }

    /**
     * Add the observation to the map.
     */
    public void addObservation(SpotCheckObservation<ContentKey> observation) {
        if (observation == null) {
            throw new IllegalArgumentException("Supplied observation cannot be null");
        }
        observation.setReferenceId(reportId.getReferenceId());
        observation.setObservedDateTime(LocalDateTime.now());
        // If there is already an observation for this key, attempt to merge the new one in.
        if (observationMap.containsKey(observation.getKey())) {
            SpotCheckObservation<ContentKey> priorObs = observationMap.get(observation.getKey());
            priorObs.merge(observation);
        } else {
            this.observationMap.put(observation.getKey(), observation);
        }
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
        return Optional.of(this.observationMap).map(Map::size).orElse(0);
    }

    /**
     * Get ContentKey's that were checked by this report.
     */
    public Set<ContentKey> getCheckedKeys() {
        return this.getObservationMap().values().stream().map(SpotCheckObservation::getKey).collect(Collectors.toSet());
    }

    /**
     * Add an observation to the report indicating that content is missing from the reference data
     * @param missingKey ContentKey - id of the missing data
     */
    public void addRefMissingObs(ContentKey missingKey) {
        this.addObservation(SpotCheckObservation.getRefMissingObs(reportId.getReferenceId(), missingKey));
    }

    /**
     * Add an observation to the report indicating that content is missing from Openleg data
     * @param missingKey ContentKey - id of the missing data
     */
    public void addObservedDataMissingObs(ContentKey missingKey) {
        this.addObservation(SpotCheckObservation.getObserveDataMissingObs(reportId.getReferenceId(), missingKey));
    }

    /**
     * Add an empty observation to the report, indicating that there are no errors.
     * @param contentKey ContentKey
     */
    public void addEmptyObservation(ContentKey contentKey) {
        this.addObservation(new SpotCheckObservation<>(reportId.getReferenceId(), contentKey));
    }

    public Collection<SpotCheckObservation<ContentKey>> getObservations() {
        return observationMap.values();
    }

    public Set<SpotCheckMismatchKey<ContentKey>> getMismatchKeys() {
        return observationMap.values().stream()
                .map(SpotCheckObservation::getMismatchKeys)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SpotCheckReportId getReportId() {
        return reportId;
    }

    public void setReportId(SpotCheckReportId reportId) {
        this.reportId = reportId;
    }

    public Map<ContentKey, SpotCheckObservation<ContentKey>> getObservationMap() {
        return observationMap;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
