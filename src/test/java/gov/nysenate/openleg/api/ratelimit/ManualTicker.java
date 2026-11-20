package gov.nysenate.openleg.api.ratelimit;

import com.google.common.base.Ticker;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/** A frozen clock for rate limit tests: time moves only when a test advances it. */
public class ManualTicker extends Ticker {
    private final AtomicLong nanos = new AtomicLong();

    @Override
    public long read() {
        return nanos.get();
    }

    public void advance(Duration duration) {
        nanos.addAndGet(duration.toNanos());
    }
}
