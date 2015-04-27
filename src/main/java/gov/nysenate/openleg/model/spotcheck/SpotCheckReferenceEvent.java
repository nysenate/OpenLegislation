package gov.nysenate.openleg.model.spotcheck;

/**
 * An event that is posted when new SpotCheck references are available
 */
public class SpotCheckReferenceEvent
{
    /** A type indicating the source of the new references */
    protected SpotCheckRefType refType;

    public SpotCheckReferenceEvent(SpotCheckRefType refType) {
        this.refType = refType;
    }

    public SpotCheckRefType getRefType() {
        return refType;
    }
}
