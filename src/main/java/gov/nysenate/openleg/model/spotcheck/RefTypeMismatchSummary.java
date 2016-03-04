package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;

/**
 * A summary of the mismatch/status counts for a particular spotcheck ref type
 */
public class RefTypeMismatchSummary extends SpotCheckSummary {

    protected SpotCheckRefType refType;
    protected LocalDateTime observedAfter;

    public RefTypeMismatchSummary(SpotCheckRefType refType, LocalDateTime observedAfter) {
        this.refType = refType;
        this.observedAfter = observedAfter;
    }

    public SpotCheckRefType getRefType() {
        return refType;
    }

    public LocalDateTime getObservedAfter() {
        return observedAfter;
    }
}
