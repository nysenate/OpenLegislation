package gov.nysenate.openleg.api.ratelimit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.model.OpenLegRole;
import gov.nysenate.openleg.auth.service.ApiUserLoginAuthRealm;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.config.annotation.UnitTest;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Calls the filter as the servlet container does, after Shiro has bound the request's subject and
 * {@code ApiAuthFilter} has authenticated it.
 */
@Category(UnitTest.class)
public class ApiRateLimitFilterTest {
    private static final String KEY = "Abcdefghijklmnopqrstuvwxyz012345";
    private static final String OTHER_KEY = "Zyxwvutsrqponmlkjihgfedcba543210";
    private static final String BILL_PATH = "/api/3/bills/2023/S1";

    private final ApiUserService apiUserService = mock(ApiUserService.class);
    private final ApiUserLoginAuthRealm apiKeyRealm = new ApiUserLoginAuthRealm(apiUserService, new EventBus());
    private final DefaultSecurityManager securityManager = new DefaultSecurityManager();
    private final ManualTicker ticker = new ManualTicker();
    private final List<String> telemetryLines = new ArrayList<>();
    private final AtomicInteger chainCalls = new AtomicInteger();
    private final FilterChain okChain = (request, response) -> {
        chainCalls.incrementAndGet();
        ((HttpServletResponse) response).setStatus(200);
        response.getWriter().write("ok");
    };
    private ApiRateLimitTelemetry telemetry;

    @Before
    public void keysHaveNoRoles() {
        withRoles(KEY);
        withRoles(OTHER_KEY);
    }

    private ApiRateLimitFilter filter(String mode, int requestsPerSecond, int burst, int maxConcurrent) {
        ApiRateLimitSettings settings = settings(mode, requestsPerSecond, burst, maxConcurrent);
        return filter(settings, new ApiRateLimitTelemetry(settings.getMode(), telemetryLines::add));
    }

    private ApiRateLimitFilter filter(ApiRateLimitSettings settings, ApiRateLimitTelemetry telemetry) {
        this.telemetry = telemetry;
        return new ApiRateLimitFilter(settings, ticker, telemetry, apiKeyRealm, apiUserService);
    }

    private static ApiRateLimitSettings settings(String mode, int requestsPerSecond, int burst, int maxConcurrent) {
        return new ApiRateLimitSettings(mode, String.valueOf(requestsPerSecond),
                String.valueOf(burst), String.valueOf(maxConcurrent));
    }

    // --- Enforce ---

    @Test
    public void admitsTheBurstThenRejectsWithA429() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 50, 4);
        for (int i = 0; i < 50; i++) {
            assertEquals(200, get(filter, KEY).getStatus());
        }
        MockHttpServletResponse rejected = get(filter, KEY);

        assertEquals(50, chainCalls.get());
        assertEquals(429, rejected.getStatus());
        assertEquals("1", rejected.getHeader("Retry-After"));
        assertEquals("no-store", rejected.getHeader("Cache-Control"));
        assertTrue(rejected.getContentType(), rejected.getContentType().startsWith("application/json"));
        JsonNode body = new ObjectMapper().readTree(rejected.getContentAsString());
        assertFalse(body.get("success").asBoolean());
        assertEquals("error", body.get("responseType").asText());
        assertEquals(703, body.get("errorCode").asInt());
        assertTrue(body.get("message").asText(), body.get("message").asText().contains("10 requests per second"));
    }

    @Test
    public void refillsTenRequestsASecondAndKeepsEachKeysBudgetApart() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 50, 4);
        sendAll(filter, KEY, 50);
        assertEquals(429, get(filter, KEY).getStatus());

        ticker.advance(Duration.ofSeconds(1));
        for (int i = 0; i < 10; i++) {
            assertEquals(200, get(filter, KEY).getStatus());
        }
        assertEquals(429, get(filter, KEY).getStatus());
        assertEquals(200, get(filter, OTHER_KEY).getStatus());
    }

    @Test
    public void rejectsAFifthConcurrentRequestWithoutTakingAToken() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 5, 4);
        try (InFlight inFlight = new InFlight(filter, KEY, 4)) {
            MockHttpServletResponse fifth = get(filter, KEY);
            assertEquals(429, fifth.getStatus());
            assertTrue(fifth.getContentAsString(), fifth.getContentAsString().contains("4 requests"));
            assertEquals(200, get(filter, OTHER_KEY).getStatus());

            inFlight.finishOne();
            // The fifth token is still there, since the rejection didn't take it.
            assertEquals(200, get(filter, KEY).getStatus());
        }
        assertEquals(429, get(filter, KEY).getStatus());
    }

    @Test
    public void rejectionsReportHeldSlotsAcrossTelemetryIntervals() throws Exception {
        for (String mode : List.of("enforce", "observe")) {
            telemetryLines.clear();
            ApiRateLimitFilter filter = filter(mode, 10, 4, 4);
            try (InFlight held = new InFlight(filter, KEY, 4)) {
                telemetry.writeInterval();
                telemetryLines.clear();

                assertEquals(mode.equals("enforce") ? 429 : 200, get(filter, KEY).getStatus());
                telemetry.writeInterval();
                assertTrue(telemetryLines.toString(), telemetryLines.get(1).contains(
                        "admitted=0 rejected_rate=0 rejected_concurrency=1 peak_concurrency=4"));
                telemetryLines.clear();

                held.finishOne();
                // All tokens were spent before the interval reset, and three slots remain held.
                assertEquals(mode.equals("enforce") ? 429 : 200, get(filter, KEY).getStatus());
                telemetry.writeInterval();
                assertTrue(telemetryLines.toString(), telemetryLines.get(1).contains(
                        "admitted=0 rejected_rate=1 rejected_concurrency=0 peak_concurrency=3"));
            }
        }
    }

    @Test
    public void aRequestThatThrowsFreesItsSlot() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 50, 1);
        FilterChain failingChain = (request, response) -> {
            throw new IOException("Broken pipe");
        };
        try {
            send(filter, subjectFor(KEY), request("GET", BILL_PATH), failingChain);
            fail("The chain's exception should propagate");
        } catch (IOException expected) {
            // The servlet container handles it.
        }
        assertEquals(200, get(filter, KEY).getStatus());
    }

    @Test
    public void aSlotIsFreedWhenRecordingTheAdmissionThrows() throws Exception {
        ApiRateLimitSettings settings = settings("enforce", 10, 50, 1);
        ApiRateLimitTelemetry failingOnce = new ApiRateLimitTelemetry(settings.getMode(), telemetryLines::add) {
            private boolean failed;

            @Override
            public synchronized void recordAdmitted(String apiKey, boolean senateSite, int inFlight) {
                if (!failed) {
                    failed = true;
                    throw new IllegalStateException("Thrown by the test");
                }
                super.recordAdmitted(apiKey, senateSite, inFlight);
            }
        };
        ApiRateLimitFilter filter = filter(settings, failingOnce);
        try {
            get(filter, KEY);
            fail("The telemetry's exception should propagate");
        } catch (IllegalStateException expected) {
            // The servlet container handles it.
        }
        assertEquals(200, get(filter, KEY).getStatus());
    }

    @Test
    public void aSessionWhoseKeyIsNoLongerValidIsNotLimited() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 1, 1);
        when(apiUserService.getUserByKey(KEY)).thenReturn(Optional.empty());
        ApiUser unactivated = apiUser(OTHER_KEY);
        unactivated.setAuthenticated(false);
        when(apiUserService.getUserByKey(OTHER_KEY)).thenReturn(Optional.of(unactivated));
        for (int i = 0; i < 3; i++) {
            assertEquals(200, get(filter, KEY).getStatus());
            assertEquals(200, get(filter, OTHER_KEY).getStatus());
        }
        telemetry.writeInterval();
        assertEquals(List.of(), telemetryLines);
    }

    @Test
    public void exemptKeysBypassBothLimits() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 1, 1, 1);
        withRoles(KEY, OpenLegRole.API_RATE_LIMIT_EXEMPT);
        withRoles(OTHER_KEY, OpenLegRole.API_RATE_LIMIT_EXEMPT, OpenLegRole.SEN_SITE_API_USER);
        for (String key : List.of(KEY, OTHER_KEY)) {
            try (InFlight ignored = new InFlight(filter, key, 3)) {
                for (int i = 0; i < 3; i++) {
                    assertEquals(200, get(filter, key).getStatus());
                }
            }
        }
        telemetry.writeInterval();
        assertEquals(List.of("interval mode=enforce admitted=0 rejected_rate=0 rejected_concurrency=0 exempt=12"),
                telemetryLines);
    }

    @Test
    public void senateSiteKeysAreLimited() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 1, 4);
        withRoles(KEY, OpenLegRole.SEN_SITE_API_USER);
        assertEquals(200, get(filter, KEY).getStatus());
        assertEquals(429, get(filter, KEY).getStatus());
        telemetry.writeInterval();
        assertTrue(telemetryLines.toString(), telemetryLines.get(1).contains("senate_site=true"));
    }

    @Test
    public void readsTheKeysRolesForEachRequest() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 1, 4);
        assertEquals(200, get(filter, KEY).getStatus());
        assertEquals(429, get(filter, KEY).getStatus());
        withRoles(KEY, OpenLegRole.API_RATE_LIMIT_EXEMPT);
        assertEquals(200, get(filter, KEY).getStatus());
        withRoles(KEY);
        assertEquals(429, get(filter, KEY).getStatus());
    }

    @Test
    public void headersCreateNoExemption() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 1, 4);
        assertEquals(200, get(filter, KEY).getStatus());
        MockHttpServletRequest forged = request("GET", BILL_PATH);
        forged.addHeader("X-Forwarded-For", "127.0.0.1");
        forged.addHeader("X-Real-IP", "127.0.0.1");
        forged.addHeader("Referer", "https://www.nysenate.gov/");
        forged.addHeader("X-Rate-Limit-Exempt", "true");
        assertEquals(429, send(filter, subjectFor(KEY), forged, okChain).getStatus());
    }

    @Test
    public void doesNotCountOptionsAdminOrKeylessRequests() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 1, 1);
        Subject keyless = new Subject.Builder(securityManager).buildSubject();
        Subject adminSession = new Subject.Builder(securityManager)
                .principals(new SimplePrincipalCollection("admin@example.com", "AdminLoginAuthRealm_0"))
                .authenticated(true).buildSubject();
        for (int i = 0; i < 3; i++) {
            assertEquals(200, send(filter, subjectFor(KEY), request("OPTIONS", BILL_PATH), okChain).getStatus());
            assertEquals(200, send(filter, subjectFor(KEY), request("GET", "/api/3/admin/apiuser"), okChain).getStatus());
            assertEquals(200, send(filter, subjectFor(KEY), request("GET", "/api/3/admin"), okChain).getStatus());
            assertEquals(200, send(filter, keyless, request("GET", BILL_PATH), okChain).getStatus());
            assertEquals(200, send(filter, adminSession, request("GET", BILL_PATH), okChain).getStatus());
        }
        // The key's one token is still there.
        assertEquals(200, get(filter, KEY).getStatus());
        assertEquals(429, get(filter, KEY).getStatus());
    }

    @Test
    public void countsHeadRequests() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 1, 4);
        assertEquals(200, send(filter, subjectFor(KEY), request("HEAD", BILL_PATH), okChain).getStatus());
        assertEquals(429, get(filter, KEY).getStatus());
    }

    @Test
    public void countsAForwardedRequestOnce() throws Exception {
        ApiRateLimitFilter filter = filter("enforce", 10, 1, 1);
        Subject subject = subjectFor(KEY);
        MockHttpServletRequest request = request("GET", BILL_PATH + "/");
        FilterChain forwardingChain = (req, res) -> {
            ((MockHttpServletRequest) req).setDispatcherType(DispatcherType.FORWARD);
            ((MockHttpServletRequest) req).setRequestURI(BILL_PATH);
            ((MockHttpServletRequest) req).setServletPath(BILL_PATH);
            filter.doFilter(req, res, okChain);
        };
        assertEquals(200, send(filter, subject, request, forwardingChain).getStatus());
        assertEquals(1, chainCalls.get());
        assertEquals(429, get(filter, KEY).getStatus());
    }

    // --- Observe ---

    @Test
    public void observeSendsEveryResponseUnchangedAndCountsWouldBeRejections() throws Exception {
        ApiRateLimitFilter filter = filter("observe", 10, 1, 4);
        for (int i = 0; i < 3; i++) {
            MockHttpServletResponse response = get(filter, KEY);
            assertEquals(200, response.getStatus());
            assertEquals("ok", response.getContentAsString());
            assertNull(response.getHeader("Retry-After"));
        }
        assertEquals(3, chainCalls.get());
        telemetry.writeInterval();
        assertEquals("interval mode=observe admitted=1 rejected_rate=2 rejected_concurrency=0 exempt=0",
                telemetryLines.get(0));
    }

    @Test
    public void observeWouldBeRejectionsHoldNoSlot() throws Exception {
        ApiRateLimitFilter filter = filter("observe", 10, 50, 1);
        try (InFlight admitted = new InFlight(filter, KEY, 1);
             InFlight wouldBeRejected = new InFlight(filter, KEY, 1)) {
            admitted.finishOne();
            // Only the would-be rejection is in flight, and it holds no slot.
            assertEquals(200, get(filter, KEY).getStatus());
        }
        telemetry.writeInterval();
        assertEquals("interval mode=observe admitted=2 rejected_rate=0 rejected_concurrency=1 exempt=0",
                telemetryLines.get(0));
    }

    // --- Off ---

    @Test
    public void offChangesNoResponseAndCountsNothing() throws Exception {
        ApiRateLimitFilter filter = filter("off", 1, 1, 1);
        try (InFlight ignored = new InFlight(filter, KEY, 2)) {
            for (int i = 0; i < 3; i++) {
                MockHttpServletResponse response = get(filter, KEY);
                assertEquals(200, response.getStatus());
                assertEquals("ok", response.getContentAsString());
            }
        }
        telemetry.writeInterval();
        assertEquals(List.of(), telemetryLines);
    }

    // --- Helpers ---

    private void withRoles(String apiKey, OpenLegRole... roles) {
        ApiUser user = apiUser(apiKey);
        for (OpenLegRole role : roles) {
            user.addRole(role);
        }
        when(apiUserService.getUserByKey(apiKey)).thenReturn(Optional.of(user));
    }

    /** A user whose key is valid, as activating a registration leaves it. */
    private static ApiUser apiUser(String apiKey) {
        ApiUser user = new ApiUser(apiKey + "@example.com");
        user.setApiKey(apiKey);
        user.setAuthenticated(true);
        return user;
    }

    /** A subject logged in with {@code apiKey}, as ApiAuthFilter or /loginapikey leaves it. */
    private Subject subjectFor(String apiKey) {
        return new Subject.Builder(securityManager)
                .principals(new SimplePrincipalCollection(apiKey, apiKeyRealm.getName()))
                .authenticated(true).buildSubject();
    }

    /** A request as Tomcat passes it on, to a dispatcher servlet mapped to "/". */
    private static MockHttpServletRequest request(String method, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setServletPath(path);
        request.setDispatcherType(DispatcherType.REQUEST);
        return request;
    }

    private MockHttpServletResponse get(ApiRateLimitFilter filter, String apiKey) throws Exception {
        return send(filter, subjectFor(apiKey), request("GET", BILL_PATH), okChain);
    }

    private void sendAll(ApiRateLimitFilter filter, String apiKey, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            get(filter, apiKey);
        }
    }

    private static MockHttpServletResponse send(ApiRateLimitFilter filter, Subject subject,
                                                MockHttpServletRequest request, FilterChain chain)
            throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            subject.execute(() -> {
                filter.doFilter(request, response, chain);
                return null;
            });
        } catch (org.apache.shiro.subject.ExecutionException ex) {
            throw (Exception) ex.getCause();
        }
        return response;
    }

    /** Requests from one key that stay in the chain, in flight, until finished. */
    private class InFlight implements AutoCloseable {
        private final ExecutorService executor;
        private final List<CountDownLatch> finishes = new ArrayList<>();
        private final List<Future<MockHttpServletResponse>> responses = new ArrayList<>();

        InFlight(ApiRateLimitFilter filter, String apiKey, int count) throws Exception {
            executor = Executors.newFixedThreadPool(count);
            for (int i = 0; i < count; i++) {
                CountDownLatch entered = new CountDownLatch(1);
                CountDownLatch finish = new CountDownLatch(1);
                FilterChain heldChain = (request, response) -> {
                    entered.countDown();
                    await(finish);
                    okChain.doFilter(request, response);
                };
                finishes.add(finish);
                responses.add(executor.submit(
                        () -> send(filter, subjectFor(apiKey), request("GET", BILL_PATH), heldChain)));
                if (!entered.await(10, TimeUnit.SECONDS)) {
                    throw new AssertionError("Request " + i + " didn't reach the chain");
                }
            }
        }

        /** Lets the oldest unfinished request finish, and waits until it has. */
        void finishOne() throws Exception {
            for (int i = 0; i < finishes.size(); i++) {
                if (finishes.get(i).getCount() > 0) {
                    finishes.get(i).countDown();
                    assertEquals(200, responses.get(i).get(10, TimeUnit.SECONDS).getStatus());
                    return;
                }
            }
        }

        @Override
        public void close() throws Exception {
            finishes.forEach(CountDownLatch::countDown);
            for (Future<MockHttpServletResponse> response : responses) {
                assertEquals(200, response.get(10, TimeUnit.SECONDS).getStatus());
            }
            executor.shutdownNow();
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Timed out holding a request in flight");
            }
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
