package gov.nysenate.openleg.api;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static gov.nysenate.openleg.api.CollectionFullPolicy.Mode.ENFORCE;
import static gov.nysenate.openleg.api.CollectionFullPolicy.Mode.OFF;
import static gov.nysenate.openleg.api.CollectionFullPolicy.Option.BILL_FULL;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class CollectionFullPolicyTest {
    private static final String DOMAIN_URL = "http://example.test/legislation";

    @Test
    public void missingPropertyMeansOff() {
        try (var context = context(Map.of())) {
            assertEquals(OFF, context.getBean(CollectionFullPolicy.class).getMode());
        }
    }

    @Test
    public void propertySelectsMode() {
        try (var context = context(Map.of(CollectionFullPolicy.MODE_PROPERTY, "enforce"))) {
            assertEquals(ENFORCE, context.getBean(CollectionFullPolicy.class).getMode());
        }
        assertEquals(OFF, new CollectionFullPolicy("off", DOMAIN_URL).getMode());
        assertEquals(OFF, new CollectionFullPolicy(" off ", DOMAIN_URL).getMode());
        assertEquals(ENFORCE, new CollectionFullPolicy("enforce", DOMAIN_URL).getMode());
    }

    @Test
    public void otherModesAreRejected() {
        for (String mode : new String[]{"observe", "", "OFF", "Enforce", "bogus"}) {
            var ex = assertThrows(mode, IllegalArgumentException.class, () -> new CollectionFullPolicy(mode, DOMAIN_URL));
            assertTrue(ex.getMessage(), ex.getMessage().contains(CollectionFullPolicy.MODE_PROPERTY));
            assertTrue(ex.getMessage(), ex.getMessage().contains("off or enforce"));
        }
    }

    @Test
    public void invalidModeFailsContextStartup() {
        var ex = assertThrows(RuntimeException.class,
                () -> context(Map.of(CollectionFullPolicy.MODE_PROPERTY, "bogus")).close());
        assertTrue(rootMessage(ex), rootMessage(ex).contains(CollectionFullPolicy.MODE_PROPERTY));
    }

    @Test
    public void offAcceptsEveryValue() {
        CollectionFullPolicy policy = new CollectionFullPolicy("off", DOMAIN_URL);
        policy.check(BILL_FULL, true, request("full", "true"));
        policy.check(BILL_FULL, false, request("full", "false"));
    }

    @Test
    public void enforceAcceptsFalse() {
        CollectionFullPolicy policy = new CollectionFullPolicy("enforce", DOMAIN_URL);
        policy.check(BILL_FULL, false, request("full", "false"));
        policy.check(BILL_FULL, false, request());
    }

    @Test
    public void enforceRejectsTrueWithRawValueAndGuidance() {
        CollectionFullPolicy policy = new CollectionFullPolicy("enforce", DOMAIN_URL);
        var ex = assertThrows(RetiredRequestParamEx.class,
                () -> policy.check(BILL_FULL, true, request("full", "True")));
        assertEquals("full", ex.getParameterName());
        assertEquals("boolean", ex.getParameterType());
        assertEquals("True", ex.getParameterValue());
        assertEquals(policy.getConstraint(BILL_FULL), ex.getParameterConstraint());
        assertEquals(policy.getMessage(BILL_FULL), ex.getResponseMessage());
        String guide = DOMAIN_URL + "/static/docs/html/bills.html#bill-collection-full-migration";
        assertTrue(ex.getResponseMessage(), ex.getResponseMessage().endsWith("Migration guide: " + guide));
        assertTrue(ex.getParameterConstraint(), ex.getParameterConstraint().endsWith("Migration guide: " + guide));
    }

    @Test
    public void enforceJoinsRepeatedValues() {
        CollectionFullPolicy policy = new CollectionFullPolicy("enforce", DOMAIN_URL);
        var ex = assertThrows(RetiredRequestParamEx.class,
                () -> policy.check(BILL_FULL, true, request("full", "true", "full", "false")));
        assertEquals("true,false", ex.getParameterValue());
    }

    private static AnnotationConfigApplicationContext context(Map<String, Object> properties) {
        var context = new AnnotationConfigApplicationContext();
        var sources = context.getEnvironment().getPropertySources();
        sources.addFirst(new MapPropertySource("defaults", Map.of("domain.url", DOMAIN_URL)));
        sources.addFirst(new MapPropertySource("test", properties));
        context.registerBean(PropertySourcesPlaceholderConfigurer.class);
        context.register(CollectionFullPolicy.class);
        context.refresh();
        return context;
    }

    private static String rootMessage(Throwable ex) {
        while (ex.getCause() != null) {
            ex = ex.getCause();
        }
        return ex.getMessage();
    }

    private static WebRequest request(String... params) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        for (int i = 0; i < params.length; i += 2) {
            request.addParameter(params[i], params[i + 1]);
        }
        return new ServletWebRequest(request);
    }
}
