package gov.nysenate.openleg.model.spotcheck;

/**
 * An event that is posted when new SpotCheck references are available
 */
public class SpotCheckReferenceEvent
{
    /** A type indicating the source of the new references */
    protected SpotCheckRefType refType;

    /** If true, subscribed reporting services will run asynchronously */
    protected boolean asyncRun;

    public SpotCheckReferenceEvent(SpotCheckRefType refType, boolean asyncRun) {
        this.refType = refType;
        this.asyncRun = asyncRun;
    }

    public SpotCheckReferenceEvent(SpotCheckRefType refType) {
        this(refType, false);
    }

    public SpotCheckRefType getRefType() {
        return refType;
    }

    public boolean isAsyncRun() {
        return asyncRun;
    }
}
