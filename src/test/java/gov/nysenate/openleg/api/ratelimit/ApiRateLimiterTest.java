package gov.nysenate.openleg.api.ratelimit;

import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Admission;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Admitted;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Limit;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Slot;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Rejected;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ApiRateLimiterTest {
    private static final String KEY = "key-a";
    private static final String OTHER_KEY = "key-b";

    private final ManualTicker ticker = new ManualTicker();

    private ApiRateLimiter limiter(int requestsPerSecond, int burst, int maxConcurrent) {
        return new ApiRateLimiter(new ApiRateLimits(requestsPerSecond, burst, maxConcurrent), ticker);
    }

    // --- Rate ---

    @Test
    public void admitsTheFirstBurstThenRejectsByRate() {
        ApiRateLimiter limiter = limiter(10, 50, 4);
        admitAndFinish(limiter, KEY, 50);
        assertRejected(Limit.RATE, limiter.tryAdmit(KEY));
    }

    @Test
    public void refillsRequestsPerSecond() {
        ApiRateLimiter limiter = limiter(10, 50, 4);
        admitAndFinish(limiter, KEY, 50);
        ticker.advance(Duration.ofSeconds(1));
        admitAndFinish(limiter, KEY, 10);
        assertRejected(Limit.RATE, limiter.tryAdmit(KEY));
    }

    @Test
    public void refillsContinuously() {
        ApiRateLimiter limiter = limiter(10, 50, 4);
        admitAndFinish(limiter, KEY, 50);
        ticker.advance(Duration.ofMillis(99));
        assertRejected(Limit.RATE, limiter.tryAdmit(KEY));
        ticker.advance(Duration.ofMillis(1));
        admitAndFinish(limiter, KEY, 1);
        assertRejected(Limit.RATE, limiter.tryAdmit(KEY));
    }

    @Test
    public void neverHoldsMoreThanTheBurst() {
        ApiRateLimiter limiter = limiter(10, 50, 4);
        admitAndFinish(limiter, KEY, 1);
        ticker.advance(Duration.ofDays(400));
        admitAndFinish(limiter, KEY, 50);
        assertRejected(Limit.RATE, limiter.tryAdmit(KEY));
    }

    @Test
    public void neverHoldsMoreThanTheBurstAfterAShortIdle() {
        ApiRateLimiter limiter = limiter(10, 50, 4);
        admitAndFinish(limiter, KEY, 1);
        // Enough to refill the one token taken, far short of refilling the whole burst.
        ticker.advance(Duration.ofSeconds(1));
        admitAndFinish(limiter, KEY, 50);
        assertRejected(Limit.RATE, limiter.tryAdmit(KEY));
    }

    @Test
    public void keysHaveTheirOwnBudgets() {
        ApiRateLimiter limiter = limiter(10, 50, 4);
        admitAndFinish(limiter, KEY, 50);
        assertRejected(Limit.RATE, limiter.tryAdmit(KEY));
        admitAndFinish(limiter, OTHER_KEY, 50);
    }

    @Test
    public void aRateRejectionHoldsNoSlot() {
        ApiRateLimiter limiter = limiter(10, 1, 2);
        Slot held = admitted(limiter.tryAdmit(KEY));
        assertEquals(new Rejected(Limit.RATE, 1), limiter.tryAdmit(KEY));
        ticker.advance(Duration.ofMillis(100));
        // Only the held request is in flight, so a refilled token admits a second one.
        Slot second = admitted(limiter.tryAdmit(KEY));
        held.close();
        second.close();
    }

    // --- Concurrency ---

    @Test
    public void rejectsByConcurrencyWhileTheSlotsAreFull() {
        ApiRateLimiter limiter = limiter(10, 50, 4);
        List<Slot> held = hold(limiter, KEY, 4);
        assertRejected(Limit.CONCURRENCY, limiter.tryAdmit(KEY));
        admitAndFinish(limiter, OTHER_KEY, 1);
        held.forEach(Slot::close);
    }

    @Test
    public void countsTheRequestsInFlight() {
        ApiRateLimiter limiter = limiter(10, 50, 4);
        List<Integer> inFlight = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (limiter.tryAdmit(KEY) instanceof Admitted admitted) {
                inFlight.add(admitted.inFlight());
            }
        }
        assertEquals(List.of(1, 2, 3, 4), inFlight);
    }

    @Test
    public void aConcurrencyRejectionTakesNoToken() {
        ApiRateLimiter limiter = limiter(10, 5, 4);
        List<Slot> held = hold(limiter, KEY, 4);
        assertRejected(Limit.CONCURRENCY, limiter.tryAdmit(KEY));
        held.get(0).close();
        // The fifth token is still there for the next request.
        admitAndFinish(limiter, KEY, 1);
        assertRejected(Limit.RATE, limiter.tryAdmit(KEY));
        held.forEach(Slot::close);
    }

    @Test
    public void finishingARequestAdmitsTheNext() {
        ApiRateLimiter limiter = limiter(10, 50, 4);
        List<Slot> held = hold(limiter, KEY, 4);
        held.get(2).close();
        Slot next = admitted(limiter.tryAdmit(KEY));
        assertRejected(Limit.CONCURRENCY, limiter.tryAdmit(KEY));
        next.close();
        held.forEach(Slot::close);
    }

    @Test
    public void finishingTwiceFreesOneSlot() {
        ApiRateLimiter limiter = limiter(10, 50, 4);
        List<Slot> held = hold(limiter, KEY, 4);
        held.get(0).close();
        held.get(0).close();
        Slot next = admitted(limiter.tryAdmit(KEY));
        assertRejected(Limit.CONCURRENCY, limiter.tryAdmit(KEY));
        next.close();
        held.forEach(Slot::close);
    }

    @Test
    public void slotsHeldForAnyTimeStayHeld() {
        ApiRateLimiter limiter = limiter(10, 50, 1);
        Slot held = admitted(limiter.tryAdmit(KEY));
        ticker.advance(Duration.ofDays(1));
        assertRejected(Limit.CONCURRENCY, limiter.tryAdmit(KEY));
        held.close();
        admitAndFinish(limiter, KEY, 1);
    }

    // --- Simultaneous requests ---

    @Test
    public void simultaneousRequestsNeverExceedTheBurst() throws Exception {
        ApiRateLimiter limiter = limiter(10, 50, 1000);
        List<Admission> admissions = simultaneously(150, () -> {
            Admission admission = limiter.tryAdmit(KEY);
            if (admission instanceof Admitted admitted) {
                admitted.slot().close();
            }
            return admission;
        });
        assertEquals(50, admissions.stream().filter(Admitted.class::isInstance).count());
        assertTrue(admissions.stream().allMatch(a -> a instanceof Admitted ||
                a instanceof Rejected rejected && rejected.limit() == Limit.RATE));
    }

    @Test
    public void simultaneousRequestsNeverExceedMaxConcurrent() throws Exception {
        ApiRateLimiter limiter = limiter(10, 100_000, 4);
        AtomicInteger inFlight = new AtomicInteger();
        AtomicInteger peak = new AtomicInteger();
        List<Admission> admissions = simultaneously(150, () -> {
            Admission admission = limiter.tryAdmit(KEY);
            if (admission instanceof Admitted admitted) {
                peak.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
                Thread.yield();
                inFlight.decrementAndGet();
                admitted.slot().close();
            }
            return admission;
        });
        assertTrue("peak " + peak.get(), peak.get() <= 4);
        assertTrue(admissions.stream().anyMatch(Admitted.class::isInstance));
        // Every slot was released: four requests can be in flight again.
        List<Slot> held = hold(limiter, KEY, 4);
        assertRejected(Limit.CONCURRENCY, limiter.tryAdmit(KEY));
        held.forEach(Slot::close);
    }

    @Test
    public void simultaneousRequestsHoldingTheirSlotsAdmitExactlyMaxConcurrent() throws Exception {
        ApiRateLimiter limiter = limiter(10, 100_000, 4);
        CountDownLatch allTried = new CountDownLatch(150);
        List<Admission> admissions = simultaneously(150, () -> {
            Admission admission = limiter.tryAdmit(KEY);
            allTried.countDown();
            allTried.await();
            if (admission instanceof Admitted admitted) {
                admitted.slot().close();
            }
            return admission;
        });
        assertEquals(4, admissions.stream().filter(Admitted.class::isInstance).count());
        assertEquals(146, admissions.stream().filter(new Rejected(Limit.CONCURRENCY, 4)::equals).count());
        List<Slot> held = hold(limiter, KEY, 4);
        assertRejected(Limit.CONCURRENCY, limiter.tryAdmit(KEY));
        held.forEach(Slot::close);
    }

    // --- Helpers ---

    private static Slot admitted(Admission admission) {
        if (admission instanceof Admitted admitted) {
            return admitted.slot();
        }
        throw new AssertionError("Expected an admission, got " + admission);
    }

    private static void assertRejected(Limit limit, Admission admission) {
        assertTrue("Expected a rejection, got " + admission, admission instanceof Rejected);
        assertEquals(limit, ((Rejected) admission).limit());
    }

    /** Admits {@code count} requests one after another, each finishing before the next. */
    private static void admitAndFinish(ApiRateLimiter limiter, String key, int count) {
        for (int i = 0; i < count; i++) {
            admitted(limiter.tryAdmit(key)).close();
        }
    }

    /** Admits {@code count} requests that stay in flight until the test closes their slots. */
    private static List<Slot> hold(ApiRateLimiter limiter, String key, int count) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            slots.add(admitted(limiter.tryAdmit(key)));
        }
        return slots;
    }

    /** Runs {@code request} on {@code threads} threads released at the same moment. */
    private static List<Admission> simultaneously(int threads, Callable<Admission> request) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            CountDownLatch ready = new CountDownLatch(threads);
            CountDownLatch go = new CountDownLatch(1);
            List<Future<Admission>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    go.await();
                    return request.call();
                }));
            }
            ready.await();
            go.countDown();
            List<Admission> admissions = new ArrayList<>();
            for (Future<Admission> future : futures) {
                admissions.add(future.get(30, TimeUnit.SECONDS));
            }
            return admissions;
        } finally {
            executor.shutdownNow();
        }
    }
}
