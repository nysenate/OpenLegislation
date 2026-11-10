package gov.nysenate.openleg.api.ratelimit;

import gov.nysenate.openleg.api.ratelimit.ApiRateLimitSettings.Mode;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Limit;
import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class ApiRateLimitTelemetryTest {
    private static final String KEY = "Abcdefghijklmnopqrstuvwxyz012345";
    private static final String SENATE_KEY = "Zyxwvutsrqponmlkjihgfedcba543210";
    private static final String QUIET_KEY = "Q0000000000000000000000000000000";

    private final List<String> lines = new ArrayList<>();

    @Test
    public void writesTheTotalsAndEachKeyWithARejection() {
        ApiRateLimitTelemetry telemetry = new ApiRateLimitTelemetry(Mode.ENFORCE, lines::add);
        telemetry.recordAdmitted(KEY, false, 1);
        telemetry.recordAdmitted(KEY, false, 3);
        telemetry.recordAdmitted(KEY, false, 2);
        telemetry.recordRejected(KEY, false, Limit.RATE, 0);
        telemetry.recordRejected(KEY, false, Limit.RATE, 0);
        telemetry.recordRejected(KEY, false, Limit.CONCURRENCY, 3);
        telemetry.recordAdmitted(SENATE_KEY, true, 1);
        telemetry.recordRejected(SENATE_KEY, true, Limit.CONCURRENCY, 1);
        telemetry.recordAdmitted(QUIET_KEY, false, 1);
        telemetry.recordExempt();
        telemetry.recordExempt();

        telemetry.writeInterval();

        assertEquals(List.of(
                "interval mode=enforce admitted=5 rejected_rate=2 rejected_concurrency=2 exempt=2",
                "key=" + fingerprint(KEY) + " mode=enforce senate_site=false admitted=3 rejected_rate=2 "
                        + "rejected_concurrency=1 peak_concurrency=3",
                "key=" + fingerprint(SENATE_KEY) + " mode=enforce senate_site=true admitted=1 rejected_rate=0 "
                        + "rejected_concurrency=1 peak_concurrency=1"),
                lines);
    }

    @Test
    public void neverWritesAKey() {
        ApiRateLimitTelemetry telemetry = new ApiRateLimitTelemetry(Mode.ENFORCE, lines::add);
        telemetry.recordAdmitted(KEY, false, 1);
        telemetry.recordRejected(KEY, false, Limit.RATE, 0);
        telemetry.writeInterval();
        assertFalse(lines.isEmpty());
        lines.forEach(line -> assertFalse(line, line.contains(KEY)));
    }

    @Test
    public void namesTheMode() {
        ApiRateLimitTelemetry telemetry = new ApiRateLimitTelemetry(Mode.OBSERVE, lines::add);
        telemetry.recordRejected(KEY, false, Limit.RATE, 0);
        telemetry.writeInterval();
        assertEquals(List.of(
                "interval mode=observe admitted=0 rejected_rate=1 rejected_concurrency=0 exempt=0",
                "key=" + fingerprint(KEY) + " mode=observe senate_site=false admitted=0 rejected_rate=1 "
                        + "rejected_concurrency=0 peak_concurrency=0"),
                lines);
    }

    @Test
    public void anIntervalWithoutRejectionsWritesOnlyTheTotals() {
        ApiRateLimitTelemetry telemetry = new ApiRateLimitTelemetry(Mode.ENFORCE, lines::add);
        telemetry.recordAdmitted(KEY, false, 1);
        telemetry.recordExempt();
        telemetry.writeInterval();
        assertEquals(List.of("interval mode=enforce admitted=1 rejected_rate=0 rejected_concurrency=0 exempt=1"),
                lines);
    }

    @Test
    public void eachIntervalStartsEmptyAndAnIdleOneWritesNothing() {
        ApiRateLimitTelemetry telemetry = new ApiRateLimitTelemetry(Mode.ENFORCE, lines::add);
        telemetry.recordAdmitted(KEY, false, 4);
        telemetry.recordRejected(KEY, false, Limit.CONCURRENCY, 3);
        telemetry.writeInterval();
        lines.clear();

        telemetry.writeInterval();
        assertEquals(List.of(), lines);

        telemetry.recordAdmitted(KEY, false, 1);
        telemetry.recordRejected(KEY, false, Limit.RATE, 0);
        telemetry.writeInterval();
        assertEquals(List.of(
                "interval mode=enforce admitted=1 rejected_rate=1 rejected_concurrency=0 exempt=0",
                "key=" + fingerprint(KEY) + " mode=enforce senate_site=false admitted=1 rejected_rate=1 "
                        + "rejected_concurrency=0 peak_concurrency=1"),
                lines);
    }

    /** The first 12 hex characters of the key's SHA-256. */
    private static String fingerprint(String apiKey) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(apiKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash).substring(0, 12);
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }
}
