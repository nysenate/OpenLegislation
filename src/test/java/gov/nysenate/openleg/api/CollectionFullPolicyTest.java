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
import static gov.nysenate.openleg.api.CollectionFullPolicy.Option.*;
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
        String guide = DOMAIN_URL + "/static/docs/html/migration.html#bill-collection-full-migration";
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

    @Test
    public void nonBillOptionsCarryDocumentedTextsAndGuides() {
        CollectionFullPolicy policy = new CollectionFullPolicy("enforce", DOMAIN_URL);
        assertDocumented(policy, HEARING_FULL, "full",
                "/static/docs/html/migration.html#hearing-collection-full-migration",
                "Hearing search and listing no longer return full hearings. Omit full and use summary=true for " +
                "hearing details without the text, or summary=false for IDs only, then request each hearing from " +
                "/api/3/hearings/{id}.",
                "Must be false or omitted on hearing search and listing.");
        assertDocumented(policy, TRANSCRIPT_FULL, "full",
                "/static/docs/html/migration.html#transcript-collection-full-migration",
                "Transcript search and listing no longer return full transcripts. Omit full and use summary=true " +
                "for transcript details without the text, or summary=false for IDs only, then request each " +
                "transcript from /api/3/transcripts/{dateTime}/{sessionType}.",
                "Must be false or omitted on transcript search and listing.");
        assertDocumented(policy, CALENDAR_FULL, "full",
                "/static/docs/html/migration.html#calendar-collection-full-migration",
                "Calendar listings no longer return full calendars. Omit full for calendar summaries, then request " +
                "each calendar from /api/3/calendars/{year}/{calendarNumber}.",
                "Must be false or omitted on calendar listings.");
        assertDocumented(policy, ACTIVE_LIST_FULL, "full",
                "/static/docs/html/migration.html#calendar-collection-full-migration",
                "Active list listings no longer return full active lists. Omit full for active list summaries, " +
                "then request each active list from /api/3/calendars/{year}/{calendarNumber}/{sequenceNumber}.",
                "Must be false or omitted on active list listings.");
        assertDocumented(policy, SUPPLEMENTAL_FULL, "full",
                "/static/docs/html/migration.html#calendar-collection-full-migration",
                "Supplemental calendar listings no longer return full supplementals. Omit full for supplemental " +
                "summaries, then request each one from /api/3/calendars/{year}/{calendarNumber}/{version}.",
                "Must be false or omitted on supplemental calendar listings.");
        assertDocumented(policy, CALENDAR_SEARCH_DETAIL, "detail",
                "/static/docs/html/migration.html#calendar-collection-full-migration",
                "Calendar search no longer returns full calendars. Omit detail for calendar summaries, then request " +
                "each calendar from /api/3/calendars/{year}/{calendarNumber}.",
                "Must be false or omitted on calendar search.");
        assertDocumented(policy, AGENDA_SEARCH_FULL, "full",
                "/static/docs/html/migration.html#agenda-search-full-migration",
                "Agenda search no longer returns full committee agendas. Omit full for committee agenda summaries, " +
                "then request each one from /api/3/agendas/{year}/{agendaNo}/{committeeName}.",
                "Must be false or omitted on agenda search.");
    }

    private static void assertDocumented(CollectionFullPolicy policy, CollectionFullPolicy.Option option,
                                         String parameterName, String guidePath, String message, String constraint) {
        String guide = " Migration guide: " + DOMAIN_URL + guidePath;
        assertEquals(option.name(), parameterName, option.getParameterName());
        assertEquals(option.name(), message + guide, policy.getMessage(option));
        assertEquals(option.name(), constraint + guide, policy.getConstraint(option));
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
