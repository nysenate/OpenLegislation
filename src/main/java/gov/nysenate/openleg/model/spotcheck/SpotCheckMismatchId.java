package gov.nysenate.openleg.model.spotcheck;

/**
 * Identifies any spotcheck mismatches observed for a specific report type, content id, and mismatch type
 * Used to group recurring mismatches
 */
public class SpotCheckMismatchId<ContentId> {

    protected SpotCheckRefType refType;
    protected ContentId contentId;
    protected SpotCheckMismatchType mismatchType;

    public SpotCheckMismatchId(SpotCheckRefType refType, ContentId contentId, SpotCheckMismatchType mismatchType) {
        this.refType = refType;
        this.contentId = contentId;
        this.mismatchType = mismatchType;
    }

    /** --- Getters --- */

    public SpotCheckRefType getRefType() {
        return refType;
    }

    public ContentId getContentId() {
        return contentId;
    }

    public SpotCheckMismatchType getMismatchType() {
        return mismatchType;
    }
}
