package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.util.StringDiffer;

import java.util.LinkedList;
import java.util.Objects;

/**
 * Encapsulates basic information about a mismatch between the reference and target content.
 */
public class SpotCheckMismatch
{
    /** The type of mismatch that occurred. */
    protected SpotCheckMismatchType mismatchType;

    /** The status of the mismatch (new, existing, etc.) */
    protected SpotCheckMismatchStatus status = SpotCheckMismatchStatus.NEW;

    /** String representation of the reference data. (e.g. lbdc daybreak content) */
    protected String referenceData;

    /** String representation of the observed data (typically openleg processed content) */
    protected String observedData;

    /** Any details about this mismatch. (Optional) */
    protected String notes;

    /**
     * --- Constructor ---
     */

    public SpotCheckMismatch(SpotCheckMismatchType mismatchType, Object referenceData, Object observedData) {
        this(mismatchType, String.valueOf(referenceData), String.valueOf(observedData));
    }

    public SpotCheckMismatch(SpotCheckMismatchType mismatchType, String referenceData, String observedData) {
        this(mismatchType, referenceData, observedData, "");
    }

    public SpotCheckMismatch(SpotCheckMismatchType mismatchType, String referenceData, String observedData, String notes) {
        this.mismatchType = mismatchType;
        this.referenceData = referenceData == null ? "" : referenceData;
        this.observedData = observedData == null ? "" : observedData;
        this.notes = notes;
    }

    /** --- Methods --- */

    /**
     * Computes the difference between the reference and target data.
     *
     * @param simple boolean - Set to true to make the results of the diff less granular.
     * @return LinkedList<StringDiffer.Diff>
     */
    public LinkedList<StringDiffer.Diff> getDiff(boolean simple) {
        StringDiffer stringDiffer = new StringDiffer();
        LinkedList<StringDiffer.Diff> diffs = stringDiffer.diff_main(referenceData, observedData);
        if (simple) {
            stringDiffer.diff_cleanupSemantic(diffs);
        }
        return diffs;
    }

    /** --- Implemented Methods --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final SpotCheckMismatch other = (SpotCheckMismatch) obj;
        return Objects.equals(this.mismatchType, other.mismatchType) &&
               Objects.equals(this.status, other.status) &&
               Objects.equals(this.referenceData, other.referenceData) &&
               Objects.equals(this.observedData, other.observedData) &&
               Objects.equals(this.notes, other.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mismatchType, status, referenceData, observedData, notes);
    }

    /** --- Basic Getters --- */

    public SpotCheckMismatchType getMismatchType() {
        return mismatchType;
    }

    public SpotCheckMismatchStatus getStatus() {
        return status;
    }

    public void setStatus(SpotCheckMismatchStatus status) {
        this.status = status;
    }

    public String getReferenceData() {
        return referenceData;
    }

    public String getObservedData() {
        return observedData;
    }

    public String getNotes() {
        return notes;
    }
}