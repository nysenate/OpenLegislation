package gov.nysenate.openleg.spotchecks.model;

import java.util.*;

/**
 * Encapsulates basic information about a mismatch between the reference and target content.
 */
public class SpotCheckMismatch {
    /** An integer id that uniquely identifies this mismatch */
    protected int mismatchId;

    /** The type of mismatch that occurred. */
    protected SpotCheckMismatchType mismatchType;

    /** The status of the mismatch (new, existing, etc.) */
    protected MismatchState state = MismatchState.OPEN;

    /** String representation of the reference data. (e.g. lbdc daybreak content) */
    protected String referenceData;

    /** String representation of the observed data (typically openleg processed content) */
    protected String observedData;

    /** Any details about this mismatch. (Optional) */
    protected String notes;

    /** The ignore status of this mismatch. (Optional) */
    protected SpotCheckMismatchIgnore ignoreStatus;

    /** A list of related issue tracker ids */
    protected LinkedHashSet<String> issueIds = new LinkedHashSet<>();

    /** --- Constructor --- */

    public SpotCheckMismatch(SpotCheckMismatchType mismatchType, Object observedData, Object referenceData) {
        this(mismatchType, String.valueOf(observedData), String.valueOf(referenceData));
    }

    public SpotCheckMismatch(SpotCheckMismatchType mismatchType, String observedData, String referenceData) {
        this(mismatchType, observedData, referenceData, "");
    }

    public SpotCheckMismatch(SpotCheckMismatchType mismatchType, String observedData, String referenceData, String notes) {
        this.mismatchType = mismatchType;
        this.referenceData = referenceData == null ? "" : referenceData;
        this.observedData = observedData == null ? "" : observedData;
        this.notes = notes;
    }

    /** --- Functional Getters / Setters --- */

    public boolean isIgnored() {
        return ignoreStatus != null;
    }

    public SpotCheckMismatchTracked getTracked() {
        return SpotCheckMismatchTracked.getFromBoolean(!issueIds.isEmpty());
    }

    public void addIssueId(String issueId) {
        issueIds.add(issueId);
    }

    public List<String> getIssueIds() {
        return new ArrayList<>(issueIds);
    }

    public void setIssueIds(Collection<String> issueIds) {
        this.issueIds = new LinkedHashSet<>(issueIds);
    }

    /** --- Implemented Methods --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final SpotCheckMismatch other = (SpotCheckMismatch) obj;
        return Objects.equals(this.mismatchType, other.mismatchType) &&
               Objects.equals(this.state, other.state) &&
               Objects.equals(this.referenceData, other.referenceData) &&
               Objects.equals(this.observedData, other.observedData) &&
               Objects.equals(this.notes, other.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mismatchType, state, referenceData, observedData, notes);
    }

    /** --- Basic Getters / Setters --- */

    public SpotCheckMismatchType getMismatchType() {
        return mismatchType;
    }

    public MismatchState getState() {
        return state;
    }

    public void setState(MismatchState state) {
        this.state = state;
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

    public SpotCheckMismatchIgnore getIgnoreStatus() {
        return ignoreStatus;
    }

    public void setIgnoreStatus(SpotCheckMismatchIgnore ignoreStatus) {
        this.ignoreStatus = ignoreStatus;
    }

    public int getMismatchId() {
        return mismatchId;
    }

    public void setMismatchId(int mismatchId) {
        this.mismatchId = mismatchId;
    }
}