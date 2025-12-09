package gov.nysenate.openleg.spotchecks.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType.OBSERVE_DATA_MISSING;
import static gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType.REFERENCE_DATA_MISSING;

/**
 * A SpotCheckObservation is the result of performing a SpotCheck against some reference data. It contains
 * any mismatches that were detected between the reference content and the observed content.
 *
 * @param <ContentKey> Class that is used as a key for identifying the specific piece of
 *                     content that is being compared during the spot check.
 */
public class SpotCheckObservation<ContentKey> {
    /** The source used to compare our data against. */
    private SpotCheckReferenceId referenceId;

    /** A key that identifies the content being checked. */
    private final ContentKey key;

    /** The datetime this observation was made. */
    private LocalDateTime observedDateTime;

    /** Mapping of mismatches that exist between the reference content and our content. */
    private final Map<SpotCheckMismatchType, SpotCheckMismatch> mismatches = new HashMap<>();

    /* --- Constructors --- */

    public SpotCheckObservation(SpotCheckReferenceId referenceId, ContentKey key) {
        this.referenceId = referenceId;
        this.key = key;
        this.observedDateTime = LocalDateTime.now();
    }

    /* --- Methods --- */

    /**
     * Generates an observation with a reference data missing observation
     * @param referenceId {@link SpotCheckReferenceId}
     * @param key {@link ContentKey}
     * @return {@link SpotCheckObservation}
     */
    public static <ContentKey> SpotCheckObservation<ContentKey> getRefMissingObs(
            SpotCheckReferenceId referenceId, ContentKey key) {
        SpotCheckObservation<ContentKey> obs = new SpotCheckObservation<>(referenceId, key);
        obs.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING, key, ""));
        return obs;
    }

    /**
     * Generates an observation with an observed data missing observation
     * @param referenceId {@link SpotCheckReferenceId}
     * @param key {@link ContentKey}
     * @return {@link SpotCheckObservation}
     */
    public static <ContentKey> SpotCheckObservation<ContentKey> getObserveDataMissingObs(
            SpotCheckReferenceId referenceId, ContentKey key) {
        SpotCheckObservation<ContentKey> obs = new SpotCheckObservation<>(referenceId, key);
        obs.addMismatch(new SpotCheckMismatch(OBSERVE_DATA_MISSING, "", key));
        return obs;
    }

    public boolean hasMismatch(SpotCheckMismatchType type) {
        return mismatches.containsKey(type);
    }

    public void addMismatch(SpotCheckMismatch mismatch) {
        checkReportable(mismatch.getMismatchType());
        mismatches.put(mismatch.getMismatchType(), mismatch);
    }

    /**
     * Returns a mapping of mismatch type to status.
     *
     * @return Map<SpotCheckMismatchType, SpotCheckMismatchStatus>
     */
    public Map<SpotCheckMismatchType, MismatchState> getMismatchStatusTypes(boolean ignored) {
        return mismatches.values().stream()
                .filter(mismatch -> !mismatch.isIgnored() ^ ignored)
                .collect(Collectors.toMap(SpotCheckMismatch::getMismatchType, SpotCheckMismatch::getState));
    }

    /**
     * Returns the set of mismatch types that there are mismatches for.
     *
     * @return Set<SpotCheckMismatchType>
     */
    public Set<SpotCheckMismatchType> getMismatchTypes(boolean ignored) {
        return getMismatchStatusTypes(ignored).keySet();
    }

    /**
     * Get a set of mismatch keys for this observation.
     */
    public Set<SpotCheckMismatchKey<ContentKey>> getMismatchKeys() {
        return mismatches.values().stream()
                .map(mm -> new SpotCheckMismatchKey<>(key, mm.getMismatchType()))
                .collect(Collectors.toSet());
    }

    /**
     * Check to make sure the given mismatch type can be reported by this observation
     * @param type {@link SpotCheckMismatchType}
     * @throws IllegalArgumentException if it cannot.
     */
    public void checkReportable(SpotCheckMismatchType type) throws IllegalArgumentException {
        SpotCheckRefType referenceType = referenceId.getReferenceType();
        // The mismatch type must be registered as being checked by the observation's reference type.
        // Otherwise it cannot be resolved.
        if (!referenceType.checkedMismatchTypes().contains(type)) {
            throw new IllegalArgumentException(
                    type + " mismatches cannot be reported in " + referenceType + " observations.");
        }
    }

    /**
     * Merge another observation of the same content in the same report into this observation..
     * todo: add check for report id.
     *  - Didn't add initially because reportDateTime isn't properly set and would require major refactor.
     *
     * @param other {@link SpotCheckObservation}
     * @throws IllegalArgumentException if the given observation has a different content key or report id.
     */
    public void merge(SpotCheckObservation<ContentKey> other) {
        if (!Objects.equals(this.key, other.key)) {
            throw new IllegalArgumentException("Attempt to merge two observations with different keys: " +
                    this.key + " and " + other.key);
        }
        this.mismatches.putAll(other.mismatches);
    }

    /* --- Basic Getters/Setters --- */

    public SpotCheckReferenceId getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(SpotCheckReferenceId referenceId) {
        this.referenceId = referenceId;
    }

    public ContentKey getKey() {
        return key;
    }

    public LocalDateTime getObservedDateTime() {
        return observedDateTime;
    }

    public void setObservedDateTime(LocalDateTime observedDateTime) {
        this.observedDateTime = observedDateTime;
    }

    public Map<SpotCheckMismatchType, SpotCheckMismatch> getMismatches() {
        return mismatches;
    }
}
