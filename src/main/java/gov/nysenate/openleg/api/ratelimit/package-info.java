/**
 * Per-key limits on {@code /api/3} requests made with an API key.
 *
 * <p>Glossary:
 * <dl>
 *   <dt>API key</dt>
 *   <dd>The key a request is logged in with, sent as the {@code key} parameter or held by a
 *       {@code /loginapikey} session. Each key has its own budget.</dd>
 *   <dt>Budget</dt>
 *   <dd>A key's token bucket and slots.</dd>
 *   <dt>Token bucket</dt>
 *   <dd>Limits a key's rate. It starts full, holds at most {@code burst} tokens and refills
 *       continuously at {@code requests_per_second}. An admitted request takes one token.</dd>
 *   <dt>Slot</dt>
 *   <dd>Limits a key's concurrency. A key has {@code max_concurrent} slots, and an admitted request
 *       holds one while it is in flight: until the server has finished it, however it ends.</dd>
 *   <dt>Admission</dt>
 *   <dd>The decision for one request, made once and at once: admitted, or rejected by the rate
 *       or the concurrency limit. A rejected request takes no token and holds no slot.</dd>
 *   <dt>Exempt</dt>
 *   <dd>A key with the {@code API_RATE_LIMIT_EXEMPT} role, which bypasses both limits.</dd>
 *   <dt>Mode</dt>
 *   <dd>{@code off} limits and counts nothing; {@code observe} counts what {@code enforce} would
 *       reject but sends every response unchanged; {@code enforce} rejects with a 429.</dd>
 *   <dt>Fingerprint</dt>
 *   <dd>The first 12 hex characters of a key's SHA-256, which telemetry writes instead of the
 *       key.</dd>
 *   <dt>Interval</dt>
 *   <dd>The roughly one minute of decisions that telemetry counts before writing them.</dd>
 * </dl>
 */
package gov.nysenate.openleg.api.ratelimit;
