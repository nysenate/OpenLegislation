package gov.nysenate.openleg.spotchecks.model;

import com.google.common.base.Objects;

/**
 * Identifies any spotcheck mismatches observed for a specific report type, content id, and mismatch type
 * Used to group recurring mismatches
 */
public class SpotCheckMismatchKey<ContentId> {

    protected ContentId contentId;
    protected SpotCheckMismatchType mismatchType;

    public SpotCheckMismatchKey(ContentId contentId, SpotCheckMismatchType mismatchType) {
        this.contentId = contentId;
        this.mismatchType = mismatchType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpotCheckMismatchKey)) return false;
        SpotCheckMismatchKey<?> that = (SpotCheckMismatchKey<?>) o;
        return Objects.equal(contentId, that.contentId) &&
                mismatchType == that.mismatchType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(contentId, mismatchType);
    }

    /** --- Getters --- */

    public ContentId getContentId() {
        return contentId;
    }

    public SpotCheckMismatchType getMismatchType() {
        return mismatchType;
    }
}
