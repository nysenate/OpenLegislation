package gov.nysenate.openleg.api.ratelimit;

import gov.nysenate.openleg.api.ratelimit.ApiRateLimitSettings.Mode;
import gov.nysenate.openleg.config.annotation.UnitTest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class ApiRateLimitSettingsTest {

    @Test
    public void missingPropertiesTakeTheDefaults() {
        ApiRateLimitSettings settings = load(Map.of());
        assertEquals(Mode.OFF, settings.getMode());
        assertEquals(new ApiRateLimits(10, 50, 4), settings.getLimits());
    }

    @Test
    public void readsEveryProperty() {
        ApiRateLimitSettings settings = load(Map.of(
                "api.rate_limit.mode", "enforce",
                "api.rate_limit.requests_per_second", "20",
                "api.rate_limit.burst", "100",
                "api.rate_limit.max_concurrent", "8"));
        assertEquals(Mode.ENFORCE, settings.getMode());
        assertEquals(new ApiRateLimits(20, 100, 8), settings.getLimits());
    }

    @Test
    public void readsEachMode() {
        assertEquals(Mode.OFF, load(Map.of("api.rate_limit.mode", "off")).getMode());
        assertEquals(Mode.OBSERVE, load(Map.of("api.rate_limit.mode", "observe")).getMode());
        assertEquals(Mode.ENFORCE, load(Map.of("api.rate_limit.mode", "enforce")).getMode());
    }

    @Test
    public void ignoresSurroundingSpaces() {
        // A properties file keeps the spaces after a value.
        ApiRateLimitSettings settings = load(Map.of(
                "api.rate_limit.mode", "observe ",
                "api.rate_limit.burst", "60 "));
        assertEquals(Mode.OBSERVE, settings.getMode());
        assertEquals(60, settings.getLimits().burst());
    }

    @Test
    public void anInvalidModeStopsStartupNamingTheProperty() {
        assertStartupFails("api.rate_limit.mode", "enforced");
        assertStartupFails("api.rate_limit.mode", "ENFORCE");
        assertStartupFails("api.rate_limit.mode", "");
    }

    @Test
    public void anInvalidLimitStopsStartupNamingTheProperty() {
        for (String property : new String[] {"api.rate_limit.requests_per_second",
                "api.rate_limit.burst", "api.rate_limit.max_concurrent"}) {
            assertStartupFails(property, "0");
            assertStartupFails(property, "-1");
            assertStartupFails(property, "2.5");
            assertStartupFails(property, "ten");
            assertStartupFails(property, "");
            assertStartupFails(property, "3000000000");
        }
    }

    private static void assertStartupFails(String property, String value) {
        try {
            load(Map.of(property, value));
            fail("Started with " + property + "=" + value);
        } catch (RuntimeException ex) {
            String message = ExceptionUtils.getRootCause(ex).getMessage();
            assertTrue(message, message.contains(property) && message.contains("'" + value + "'"));
        }
    }

    /** Starts a Spring context with the settings bean and {@code properties}, as the webapp does. */
    private static ApiRateLimitSettings load(Map<String, Object> properties) {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().getPropertySources()
                    .addFirst(new MapPropertySource("test", properties));
            context.register(ApiRateLimitSettings.class);
            context.refresh();
            return context.getBean(ApiRateLimitSettings.class);
        }
    }
}
