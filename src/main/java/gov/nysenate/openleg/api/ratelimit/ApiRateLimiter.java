package gov.nysenate.openleg.api.ratelimit;

import com.google.common.base.Ticker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Decides, per API key, whether a request may start now. A token bucket limits the rate and a
 * count of slots limits the requests in flight. The decision is made at once, never by waiting,
 * and a rejected request takes neither a token nor a slot.
 *
 * <p>Budgets live in memory and are never evicted. They grow only with the number of distinct
 * keys that made a request, and callers pass only keys that were valid when the request was
 * authenticated, so there are never more budgets than API users, all of whom the API user cache
 * already holds. Since a budget is never replaced, a key can't get a fresh one while its requests
 * are in flight.
 */
public class ApiRateLimiter {
    /** One token in the units buckets count, which refill by {@code requestsPerSecond} a nanosecond. */
    private static final long TOKEN = 1_000_000_000L;

    public enum Limit {
        RATE, CONCURRENCY
    }

    /** The decision for one request. */
    public sealed interface Admission permits Admitted, Rejected {}

    /** The request may start. {@code inFlight} counts it. Close its slot once it has finished. */
    public record Admitted(Slot slot, int inFlight) implements Admission {}

    /** The request is over {@code limit}. It took no token and holds no slot; {@code inFlight} counts existing slots. */
    public record Rejected(Limit limit, int inFlight) implements Admission {}

    /** The slot an admitted request holds while in flight. Closing it frees it; closing it again does nothing. */
    public final class Slot implements AutoCloseable {
        private final Budget budget;
        private boolean closed;

        private Slot(Budget budget) {
            this.budget = budget;
        }

        @Override
        public void close() {
            synchronized (budget) {
                if (!closed) {
                    closed = true;
                    budget.inFlight--;
                }
            }
        }
    }

    /** One key's token bucket and slots, guarded by the budget's own lock, so keys don't wait on each other. */
    private static final class Budget {
        private long tokens;
        private long refilledAt;
        private int inFlight;

        private Budget(long tokens, long now) {
            this.tokens = tokens;
            this.refilledAt = now;
        }
    }

    private final ApiRateLimits limits;
    private final Ticker ticker;
    private final long capacity;
    private final Map<String, Budget> budgets = new ConcurrentHashMap<>();

    public ApiRateLimiter(ApiRateLimits limits, Ticker ticker) {
        this.limits = limits;
        this.ticker = ticker;
        this.capacity = limits.burst() * TOKEN;
    }

    public Admission tryAdmit(String apiKey) {
        Budget budget = budgets.computeIfAbsent(apiKey, key -> new Budget(capacity, ticker.read()));
        synchronized (budget) {
            refill(budget, ticker.read());
            if (budget.inFlight >= limits.maxConcurrent()) {
                return new Rejected(Limit.CONCURRENCY, budget.inFlight);
            }
            if (budget.tokens < TOKEN) {
                return new Rejected(Limit.RATE, budget.inFlight);
            }
            budget.tokens -= TOKEN;
            budget.inFlight++;
            return new Admitted(new Slot(budget), budget.inFlight);
        }
    }

    private void refill(Budget budget, long now) {
        long elapsed = now - budget.refilledAt;
        long missing = capacity - budget.tokens;
        // Fill up without multiplying when that could overflow, after a long idle time.
        budget.tokens = elapsed >= Math.ceilDiv(missing, limits.requestsPerSecond())
                ? capacity
                : budget.tokens + elapsed * limits.requestsPerSecond();
        budget.refilledAt = now;
    }
}
