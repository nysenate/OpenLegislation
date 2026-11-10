package gov.nysenate.openleg.api.ratelimit;

import com.google.common.primitives.Ints;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The API rate limit settings, read once at startup from the {@code api.rate_limit.*} properties.
 * A missing property takes its default, and an invalid one stops startup.
 */
@Component
public class ApiRateLimitSettings {
    private static final String PREFIX = "api.rate_limit.";

    /** What the rate limit does with API key requests. It only changes on restart. */
    public enum Mode {
        /** Nothing: no request is counted and no response changes. */
        OFF,
        /** Counts requests as enforce would, but sends every response unchanged. */
        OBSERVE,
        /** Rejects requests over a limit with a 429. */
        ENFORCE
    }

    private final Mode mode;
    private final ApiRateLimits limits;

    public ApiRateLimitSettings(@Value("${" + PREFIX + "mode:off}") String mode,
                                @Value("${" + PREFIX + "requests_per_second:10}") String requestsPerSecond,
                                @Value("${" + PREFIX + "burst:50}") String burst,
                                @Value("${" + PREFIX + "max_concurrent:4}") String maxConcurrent) {
        this.mode = switch (mode.trim()) {
            case "off" -> Mode.OFF;
            case "observe" -> Mode.OBSERVE;
            case "enforce" -> Mode.ENFORCE;
            default -> throw new IllegalArgumentException(
                    "Invalid " + PREFIX + "mode '" + mode + "': must be off, observe or enforce");
        };
        this.limits = new ApiRateLimits(positiveInt("requests_per_second", requestsPerSecond),
                positiveInt("burst", burst), positiveInt("max_concurrent", maxConcurrent));
    }

    public Mode getMode() {
        return mode;
    }

    public ApiRateLimits getLimits() {
        return limits;
    }

    private static int positiveInt(String name, String value) {
        Integer parsed = Ints.tryParse(value.trim());
        if (parsed == null || parsed <= 0) {
            throw new IllegalArgumentException(
                    "Invalid " + PREFIX + name + " '" + value + "': must be a positive integer");
        }
        return parsed;
    }
}
