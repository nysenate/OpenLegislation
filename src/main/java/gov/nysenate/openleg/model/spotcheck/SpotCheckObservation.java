package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    /** Mapping of mismatches that exist between the reference content and our content. */
    protected Map<SpotCheckMismatchType, SpotCheckMismatch> mismatches = new HashMap<>();

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

    public void addMismatch(SpotCheckMismatch mismatch) {
        if (mismatch != null) {
            mismatches.put(mismatch.getMismatchType(), mismatch);
        }
    }

    /** --- Basic Getters/Setters --- */

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
}