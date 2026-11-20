package gov.nysenate.openleg.api.ratelimit;

import gov.nysenate.openleg.api.BaseCtrl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** What {@link ApiRateLimitIT} adds to the webapp: a clock it controls and endpoints it can end at will. */
@Configuration
@Profile(ApiRateLimitIT.PROFILE)
public class ApiRateLimitItConfig {

    @Bean
    @Primary
    public ManualTicker apiRateLimitItTicker() {
        return new ManualTicker();
    }

    /** API endpoints that answer, fail, or hold a request in flight until the test releases it. */
    @RestController
    @Profile(ApiRateLimitIT.PROFILE)
    @RequestMapping(BaseCtrl.BASE_API_PATH + "/rate-limit-it")
    public static class ItCtrl {
        private final AtomicInteger calls = new AtomicInteger();
        private volatile CountDownLatch entered = new CountDownLatch(0);
        private volatile CountDownLatch release = new CountDownLatch(0);

        @GetMapping("/ok")
        public String ok() {
            calls.incrementAndGet();
            return "ok";
        }

        /** Throws past Spring, since this controller has no exception handler. */
        @GetMapping("/fail")
        public String fail() {
            calls.incrementAndGet();
            throw new IllegalStateException("Thrown by ApiRateLimitIT");
        }

        /** Waits for the test, then sends a body too large to fit in the socket buffers. */
        @GetMapping("/hold")
        public byte[] hold() throws InterruptedException {
            calls.incrementAndGet();
            entered.countDown();
            if (!release.await(30, TimeUnit.SECONDS)) {
                throw new IllegalStateException("ApiRateLimitIT didn't release the request");
            }
            return new byte[16 * 1024 * 1024];
        }

        public int calls() {
            return calls.get();
        }

        /** Makes the next /hold request wait. Count down the returned latch to release it. */
        public CountDownLatch holdNext() {
            entered = new CountDownLatch(1);
            release = new CountDownLatch(1);
            return release;
        }

        public boolean awaitHeld() throws InterruptedException {
            return entered.await(10, TimeUnit.SECONDS);
        }
    }
}
