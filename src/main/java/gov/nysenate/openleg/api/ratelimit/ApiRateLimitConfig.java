package gov.nysenate.openleg.api.ratelimit;

import com.google.common.base.Ticker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiRateLimitConfig {
    /** The rate limit's clock. Tests replace it with one they control. */
    @Bean
    public Ticker apiRateLimitTicker() {
        return Ticker.systemTicker();
    }
}
