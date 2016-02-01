package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.LinkedListMultimap;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A SpotCheckObservation is the result of performing a SpotCheck against some reference data. It contains
 * any mismatches that were detected between the reference content and the observed content.
 *
 * @param <ContentKey> Class that is used as a key for identifying the specific piece of
 *                     content that is being compared during the spot check.
 */
public class SpotCheckObservation<ContentKey>
{
    /** The source used to compare our data against. */
    protected SpotCheckReferenceId referenceId;

    /** A key that identifies the content being checked. */
    protected ContentKey key;

    /** The datetime this observation was made. */
    protected LocalDateTime observedDateTime;

    /** The date time when the report that generated this observation was run */
    protected LocalDateTime reportDateTime;

    /** Mapping of mismatches that exist between the reference content and our content. */
    protected Map<SpotCheckMismatchType, SpotCheckMismatch> mismatches = new HashMap<>();

    /** Mapping of prior mismatches keyed by the mismatch type. This is only populated if the observation
     * is made within the content of previously saved reports and the mismatch is one that has appeared before. */
    protected LinkedListMultimap<SpotCheckMismatchType, SpotCheckPriorMismatch> priorMismatches = LinkedListMultimap.create();

    /** --- Constructors --- */

    public SpotCheckObservation() {}

    public SpotCheckObservation(SpotCheckReferenceId referenceId, ContentKey key) {
        this.referenceId = referenceId;
        this.key = key;
        this.observedDateTime = LocalDateTime.now();
    }

    /** --- Methods --- */

    public boolean hasMismatches() {
        return !mismatches.isEmpty();
    }

    public boolean hasMismatch(SpotCheckMismatchType type) {
        return mismatches.containsKey(type);
    }

    public void addMismatch(SpotCheckMismatch mismatch) {
        if (mismatch != null) {
            mismatches.put(mismatch.getMismatchType(), mismatch);
        }
    }

    public void addPriorMismatch(SpotCheckPriorMismatch priorMismatch) {
        if (priorMismatch != null) {
            priorMismatches.put(priorMismatch.getMismatchType(), priorMismatch);
        }
    }

    /**
     * Returns the number of mismatches grouped by mismatch status. So for example:
     * {NEW=4, EXISTING=2} would be returned if there were four new mismatches and two
     * existing mismatches.
     *
     * @param ignored boolean - if true, will return counts for ignored mismatches, which are left out if false
     * @return Map<SpotCheckMismatchStatus, Long>
     */
    public Map<SpotCheckMismatchStatus, Long> getMismatchStatusCounts(boolean ignored) {
        if (mismatches != null) {
            return mismatches.values().stream()
                    .filter(mismatch -> !mismatch.isIgnored() ^ ignored)
                    .collect(Collectors.groupingBy(SpotCheckMismatch::getStatus, Collectors.counting()));
        }
        else {
            throw new IllegalStateException("Collection of mismatches is null");
        }
    }

    /**
     * Returns a mapping of mismatch type to status.
     *
     * @return Map<SpotCheckMismatchType, SpotCheckMismatchStatus>
     */
    public Map<SpotCheckMismatchType, SpotCheckMismatchStatus> getMismatchStatusTypes(boolean ignored) {
        if (mismatches != null) {
            return mismatches.values().stream()
                    .filter(mismatch -> !mismatch.isIgnored() ^ ignored)
                    .collect(Collectors.toMap(SpotCheckMismatch::getMismatchType, SpotCheckMismatch::getStatus));
        }
        else {
            throw new IllegalStateException("Collection of mismatches is null");
        }
    }

    /**
     * Returns the set of mismatch types that there are mismatches for.
     *
     * @return Set<SpotCheckMismatchType>
     */
    public Set<SpotCheckMismatchType> getMismatchTypes(boolean ignored) {
        if (mismatches != null) {
            return mismatches.values().stream()
                    .filter(mismatch -> !mismatch.isIgnored() ^ ignored)
                    .map(SpotCheckMismatch::getMismatchType)
                    .collect(Collectors.toSet());
        }
        else {
            throw new IllegalStateException("Collection of mismatches is null");
        }
    }

    /** --- Basic Getters/Setters --- */

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }

    public void setReportDateTime(LocalDateTime reportDateTime) {
        this.reportDateTime = reportDateTime;
    }

    public SpotCheckReferenceId getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(SpotCheckReferenceId referenceId) {
        this.referenceId = referenceId;
    }

    public ContentKey getKey() {
        return key;
    }

    public void setKey(ContentKey key) {
        this.key = key;
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

    public void setMismatches(Map<SpotCheckMismatchType, SpotCheckMismatch> mismatches) {
        this.mismatches = mismatches;
    }

    public LinkedListMultimap<SpotCheckMismatchType, SpotCheckPriorMismatch> getPriorMismatches() {
        return priorMismatches;
    }
}