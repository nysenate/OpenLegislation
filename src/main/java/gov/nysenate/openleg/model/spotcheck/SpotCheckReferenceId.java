package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;

/**
 * A simple model that identifies reference data that is compared against when
 * performing spot checks.
 */
public class SpotCheckReferenceId
{
    /** Indicate the type of reference that is being used when performing QA. */
    protected SpotCheckRefType referenceType;

    /** The date (and time) from which the reference data is valid from. */
    protected LocalDateTime refActiveDateTime;

    /** --- Constructors --- */

    public SpotCheckReferenceId(SpotCheckRefType referenceType, LocalDateTime refActiveDateTime) {
        this.referenceType = referenceType;
        this.refActiveDateTime = refActiveDateTime;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "referenceType = " + referenceType +", refActiveDateTime = " + refActiveDateTime + '}';
    }

    /** --- Basic Getters --- */

    public SpotCheckRefType getReferenceType() {
        return referenceType;
    }

    public LocalDateTime getRefActiveDateTime() {
        return refActiveDateTime;
    }
}
