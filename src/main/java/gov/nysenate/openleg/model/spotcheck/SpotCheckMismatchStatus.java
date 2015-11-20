package gov.nysenate.openleg.model.spotcheck;

/**
 * The various types of statuses that can be attributed to a spot check mismatch.
 */
public enum SpotCheckMismatchStatus
{
    NEW,        // A new mismatch that has never occurred before
    EXISTING,   // An ongoing problem that has yet to be resolved.
    REGRESSION, // The mismatch was resolved once before but has appeared again.
    RESOLVED,   // An existing mismatch has been resolved.
}
