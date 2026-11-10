package gov.nysenate.openleg.api.ratelimit;

/**
 * The limits each API key gets. All are positive.
 *
 * @param requestsPerSecond how fast a key's token bucket refills
 * @param burst the most tokens a key's bucket holds; a full bucket admits this many requests at once
 * @param maxConcurrent the most requests a key may have in flight
 */
public record ApiRateLimits(int requestsPerSecond, int burst, int maxConcurrent) {}
