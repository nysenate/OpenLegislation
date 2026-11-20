package gov.nysenate.openleg.api.ratelimit;

import com.google.common.hash.Hashing;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimitSettings.Mode;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Limit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Counts the rate limit's decisions and about once a minute writes them to the
 * {@value #LOGGER_NAME} logger: a line of totals, then a line for each key that had a request
 * rejected (in observe mode, one that enforce would have rejected). A key is written only as its
 * fingerprint, the first 12 hex characters of its SHA-256. An interval without requests writes
 * nothing.
 */
@Component
public class ApiRateLimitTelemetry {
    public static final String LOGGER_NAME = "gov.nysenate.openleg.api.ratelimit.telemetry";
    private static final Logger logger = LoggerFactory.getLogger(LOGGER_NAME);

    /** One key's counts for the current interval. */
    private static final class KeyCounts {
        private boolean senateSite;
        private int admitted;
        private final EnumMap<Limit, Integer> rejected = new EnumMap<>(Limit.class);
        private int peakConcurrency;

        private int rejected(Limit limit) {
            return rejected.getOrDefault(limit, 0);
        }

        private int rejections() {
            return rejected(Limit.RATE) + rejected(Limit.CONCURRENCY);
        }
    }

    private final String mode;
    private final Consumer<String> log;
    private Map<String, KeyCounts> keyCounts = new HashMap<>();
    private int exempt;

    @Autowired
    public ApiRateLimitTelemetry(ApiRateLimitSettings settings) {
        this(settings.getMode(), logger::info);
    }

    ApiRateLimitTelemetry(Mode mode, Consumer<String> log) {
        this.mode = mode.name().toLowerCase(Locale.ROOT);
        this.log = log;
    }

    public synchronized void recordAdmitted(String apiKey, boolean senateSite, int inFlight) {
        KeyCounts counts = countsFor(apiKey, senateSite);
        counts.admitted++;
        counts.peakConcurrency = Math.max(counts.peakConcurrency, inFlight);
    }

    public synchronized void recordRejected(String apiKey, boolean senateSite, Limit limit, int inFlight) {
        KeyCounts counts = countsFor(apiKey, senateSite);
        counts.rejected.merge(limit, 1, Integer::sum);
        // Slots admitted before this interval can still be held when a request is rejected.
        counts.peakConcurrency = Math.max(counts.peakConcurrency, inFlight);
    }

    public synchronized void recordExempt() {
        exempt++;
    }

    /** Writes the current interval's counts, if there are any, and starts the next interval. */
    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void writeInterval() {
        Map<String, KeyCounts> intervalCounts;
        int intervalExempt;
        // Take the interval's counts and start the next one, so requests never wait on the writing.
        synchronized (this) {
            if (keyCounts.isEmpty() && exempt == 0) {
                return;
            }
            intervalCounts = keyCounts;
            intervalExempt = exempt;
            keyCounts = new HashMap<>();
            exempt = 0;
        }
        Map<String, KeyCounts> rejectedKeys = new TreeMap<>();
        intervalCounts.forEach((apiKey, counts) -> {
            if (counts.rejections() > 0) {
                rejectedKeys.put(fingerprint(apiKey), counts);
            }
        });
        log.accept("interval mode=%s admitted=%d rejected_rate=%d rejected_concurrency=%d exempt=%d".formatted(
                mode, intervalCounts.values().stream().mapToInt(counts -> counts.admitted).sum(),
                intervalCounts.values().stream().mapToInt(counts -> counts.rejected(Limit.RATE)).sum(),
                intervalCounts.values().stream().mapToInt(counts -> counts.rejected(Limit.CONCURRENCY)).sum(),
                intervalExempt));
        rejectedKeys.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> -entry.getValue().rejections()))
                .forEach(entry -> log.accept(("key=%s mode=%s senate_site=%s admitted=%d rejected_rate=%d "
                        + "rejected_concurrency=%d peak_concurrency=%d").formatted(
                        entry.getKey(), mode, entry.getValue().senateSite, entry.getValue().admitted,
                        entry.getValue().rejected(Limit.RATE), entry.getValue().rejected(Limit.CONCURRENCY),
                        entry.getValue().peakConcurrency)));
    }

    private KeyCounts countsFor(String apiKey, boolean senateSite) {
        KeyCounts counts = keyCounts.computeIfAbsent(apiKey, key -> new KeyCounts());
        counts.senateSite = senateSite;
        return counts;
    }

    private static String fingerprint(String apiKey) {
        return Hashing.sha256().hashString(apiKey, StandardCharsets.UTF_8).toString().substring(0, 12);
    }
}
