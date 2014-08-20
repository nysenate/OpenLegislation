package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;

/**
 * A simple interface that identifies reference data that is compared against when
 * performing spot checks.
 */
public interface SpotCheckReference
{
    /**
     * Indicate the type of reference that is being used when performing QA.
     */
    public SpotCheckRefType getRefType();

    /**
     * A unique id for the reference data.
     */
    public String getRefId();

    /**
     * The date (and time) in which the reference data is valid from.
     */
    public LocalDateTime getRefActiveDate();
}
