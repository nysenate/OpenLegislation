package gov.nysenate.openleg.api.ratelimit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.auth.model.OpenLegRole;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.auth.user.ApiUserTestSupport;
import gov.nysenate.openleg.auth.user.SqlApiUserDao;
import gov.nysenate.openleg.config.WebInitializer;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FrameworkServlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Sends real HTTP requests through the filters as {@link WebInitializer} registers them, in an
 * embedded Tomcat, with limits of 1 request per second, a burst of 3 and 1 request in flight. The
 * limiter's clock only moves when a test advances it.
 */
public class ApiRateLimitIT {
    static final String PROFILE = "api-rate-limit-it";
    private static final String OK = BaseCtrl.BASE_API_PATH + "/rate-limit-it/ok";
    private static final String FAIL = BaseCtrl.BASE_API_PATH + "/rate-limit-it/fail";
    private static final String HOLD = BaseCtrl.BASE_API_PATH + "/rate-limit-it/hold";

    private static Path baseDir;
    private static Tomcat tomcat;
    private static int port;
    private static WebApplicationContext app;
    private static ManualTicker ticker;
    private static ApiRateLimitItConfig.ItCtrl itCtrl;

    private final HttpClient http = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final List<String> apiKeys = new ArrayList<>();

    @BeforeClass
    public static void startWebapp() throws Exception {
        baseDir = Files.createTempDirectory("api-rate-limit-it");
        tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.toString());
        tomcat.setPort(0);
        tomcat.getConnector().setProperty("address", "127.0.0.1");
        Context context = tomcat.addContext("", baseDir.toString());
        context.addParameter("spring.profiles.active", "test," + PROFILE);
        context.addParameter("api.auth.enable", "true");
        context.addParameter("api.auth.ip.whitelist", "127\\.0\\.0\\.1");
        context.addParameter("api.rate_limit.mode", "enforce");
        context.addParameter("api.rate_limit.requests_per_second", "1");
        context.addParameter("api.rate_limit.burst", "3");
        context.addParameter("api.rate_limit.max_concurrent", "1");
        context.addServletContainerInitializer(
                (classes, servletContext) -> new WebInitializer().onStartup(servletContext), null);
        tomcat.start();
        port = tomcat.getConnector().getLocalPort();
        app = (WebApplicationContext) context.getServletContext()
                .getAttribute(FrameworkServlet.SERVLET_CONTEXT_PREFIX + "legislation");
        assertNotNull("The webapp didn't start; see the log above", app);
        ticker = app.getBean(ManualTicker.class);
        itCtrl = app.getBean(ApiRateLimitItConfig.ItCtrl.class);
    }

    @AfterClass
    public static void stopWebapp() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
        if (baseDir != null) {
            FileSystemUtils.deleteRecursively(baseDir);
        }
    }

    @After
    public void deleteApiUsers() {
        JdbcTemplate jdbcTemplate = app.getBean(JdbcTemplate.class);
        apiKeys.forEach(apiKey -> ApiUserTestSupport.deleteUser(jdbcTemplate, apiKey));
    }

    @Test
    public void rejectsAKeyOverItsBurstBeforeTheController() throws Exception {
        String key = newApiKey();
        String otherKey = newApiKey();
        for (int i = 0; i < 3; i++) {
            assertEquals(200, get(OK + "?key=" + key).statusCode());
        }
        int calls = itCtrl.calls();

        HttpResponse<String> rejected = get(OK + "?key=" + key);

        assertEquals(429, rejected.statusCode());
        assertEquals(calls, itCtrl.calls());
        assertEquals("1", header(rejected, "Retry-After"));
        assertEquals("no-store", header(rejected, "Cache-Control"));
        assertEquals("*", header(rejected, "Access-Control-Allow-Origin"));
        assertEquals("Retry-After", header(rejected, "Access-Control-Expose-Headers"));
        assertTrue(header(rejected, "Content-Type").startsWith("application/json"));
        JsonNode body = new ObjectMapper().readTree(rejected.body());
        assertFalse(body.get("success").asBoolean());
        assertEquals("error", body.get("responseType").asText());
        assertEquals(703, body.get("errorCode").asInt());

        assertEquals(200, get(OK + "?key=" + otherKey).statusCode());
        ticker.advance(Duration.ofSeconds(1));
        assertEquals(200, get(OK + "?key=" + key).statusCode());
        assertEquals(429, get(OK + "?key=" + key).statusCode());
    }

    @Test
    public void countsEachRequestOnceIncludingForwardsAndHead() throws Exception {
        String key = newApiKey();
        // PathNormalizationFilter forwards a path with a trailing slash to the path without it.
        assertEquals(200, get(OK + "/?key=" + key).statusCode());
        assertEquals(200, send("HEAD", OK + "?key=" + key).statusCode());
        assertEquals(200, get(OK + "?key=" + key).statusCode());
        assertEquals(429, get(OK + "?key=" + key).statusCode());
    }

    @Test
    public void doesNotCountOptionsAdminOrKeylessRequests() throws Exception {
        String key = newApiKey();
        for (int i = 0; i < 3; i++) {
            assertNotEquals(429, send("OPTIONS", OK + "?key=" + key).statusCode());
            assertNotEquals(429, get(BaseCtrl.BASE_ADMIN_API_PATH + "/apiuser?key=" + key).statusCode());
            assertEquals(200, get(OK).statusCode());
        }
        for (int i = 0; i < 3; i++) {
            assertEquals(200, get(OK + "?key=" + key).statusCode());
        }
        assertEquals(429, get(OK + "?key=" + key).statusCode());
    }

    @Test
    public void invalidKeysStillGet401() throws Exception {
        HttpResponse<String> response = get(OK + "?key=NotAValidKey0000000000000000000000");
        assertEquals(401, response.statusCode());
        assertEquals(701, new ObjectMapper().readTree(response.body()).get("errorCode").asInt());
    }

    @Test
    public void queryKeyAndSessionRequestsFromAnyAddressShareOneBudget() throws Exception {
        String key = newApiKey();
        String session = logIn(key);
        assertEquals(200, get(OK, "Cookie", session, "X-Forwarded-For", "10.0.0.1").statusCode());
        assertEquals(200, get(OK + "?key=" + key, "X-Forwarded-For", "10.0.0.2").statusCode());
        assertEquals(200, get(OK, "Cookie", session, "X-Forwarded-For", "10.0.0.3").statusCode());
        assertEquals(429, get(OK + "?key=" + key, "X-Forwarded-For", "10.0.0.4").statusCode());
        assertEquals(429, get(OK, "Cookie", session).statusCode());
    }

    @Test
    public void grantingAndRevokingTheExemptionAppliesAtOnce() throws Exception {
        ApiUserService apiUserService = app.getBean(ApiUserService.class);
        String key = newApiKey();
        String session = logIn(key);
        for (int i = 0; i < 3; i++) {
            assertEquals(200, get(OK + "?key=" + key).statusCode());
        }
        assertEquals(429, get(OK + "?key=" + key).statusCode());
        assertEquals(429, get(OK, "Cookie", session).statusCode());

        apiUserService.grantRole(key, OpenLegRole.API_RATE_LIMIT_EXEMPT);
        for (int i = 0; i < 5; i++) {
            assertEquals(200, get(OK + "?key=" + key).statusCode());
            assertEquals(200, get(OK, "Cookie", session).statusCode());
        }

        apiUserService.revokeRole(key, OpenLegRole.API_RATE_LIMIT_EXEMPT);
        assertEquals(429, get(OK + "?key=" + key).statusCode());
        assertEquals(429, get(OK, "Cookie", session).statusCode());
    }

    @Test
    public void senateSiteKeysAreLimitedUnlessExempt() throws Exception {
        ApiUserService apiUserService = app.getBean(ApiUserService.class);
        String senateKey = newApiKey();
        apiUserService.grantRole(senateKey, OpenLegRole.SEN_SITE_API_USER);
        for (int i = 0; i < 3; i++) {
            assertEquals(200, get(OK + "?key=" + senateKey).statusCode());
        }
        assertEquals(429, get(OK + "?key=" + senateKey).statusCode());

        String exemptSenateKey = newApiKey();
        apiUserService.grantRole(exemptSenateKey, OpenLegRole.SEN_SITE_API_USER);
        apiUserService.grantRole(exemptSenateKey, OpenLegRole.API_RATE_LIMIT_EXEMPT);
        for (int i = 0; i < 5; i++) {
            assertEquals(200, get(OK + "?key=" + exemptSenateKey).statusCode());
        }
    }

    @Test
    public void freesTheSlotHoweverARequestEnds() throws Exception {
        String key = newApiKey();

        // An exception that escapes Spring, for Tomcat's error page to handle
        assertEquals(500, get(FAIL + "?key=" + key).statusCode());
        assertEquals(200, get(OK + "?key=" + key).statusCode());

        // A forwarded request
        assertEquals(200, get(OK + "/?key=" + key).statusCode());

        // A client that disconnects while its request is in flight
        ticker.advance(Duration.ofSeconds(3));
        CountDownLatch release = itCtrl.holdNext();
        try (Socket socket = new Socket("127.0.0.1", port)) {
            OutputStream out = socket.getOutputStream();
            out.write(("GET " + HOLD + "?key=" + key + " HTTP/1.1\r\nHost: 127.0.0.1\r\n\r\n")
                    .getBytes(StandardCharsets.US_ASCII));
            out.flush();
            assertTrue("The held request didn't reach the controller", itCtrl.awaitHeld());
            HttpResponse<String> whileHeld = get(OK + "?key=" + key);
            assertEquals(429, whileHeld.statusCode());
            assertTrue(whileHeld.body(), whileHeld.body().contains("in progress"));
            socket.setSoLinger(true, 0);
        }
        release.countDown();
        // The server finishes the abandoned request and frees its slot.
        assertEventuallyOk(OK + "?key=" + key);
    }

    // --- Helpers ---

    private String newApiKey() {
        String apiKey = ApiUserTestSupport.insertActiveUser(app.getBean(SqlApiUserDao.class)).getApiKey();
        apiKeys.add(apiKey);
        return apiKey;
    }

    /** Logs in to a session with the key, as the OpenLeg UI does, and returns its cookie. */
    private String logIn(String apiKey) throws IOException, InterruptedException {
        HttpResponse<String> response = http.send(HttpRequest.newBuilder(uri("/loginapikey"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"apiKey\": \"" + apiKey + "\"}"))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(response.body(), 200, response.statusCode());
        return response.headers().allValues("Set-Cookie").stream()
                .filter(cookie -> cookie.startsWith("JSESSIONID="))
                .map(cookie -> cookie.split(";", 2)[0])
                .findFirst()
                .orElseThrow(() -> new AssertionError("No session cookie: " + response.headers()));
    }

    private HttpResponse<String> get(String pathAndQuery, String... headers) throws IOException, InterruptedException {
        return send("GET", pathAndQuery, headers);
    }

    private HttpResponse<String> send(String method, String pathAndQuery, String... headers)
            throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(uri(pathAndQuery))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(30));
        if (headers.length > 0) {
            request.headers(headers);
        }
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private void assertEventuallyOk(String pathAndQuery) throws Exception {
        long deadline = System.nanoTime() + Duration.ofSeconds(15).toNanos();
        int status;
        do {
            status = get(pathAndQuery).statusCode();
            if (status == 200) {
                return;
            }
            Thread.sleep(100);
        } while (System.nanoTime() < deadline);
        fail("Still " + status + " after 15 seconds");
    }

    private static String header(HttpResponse<?> response, String name) {
        return response.headers().firstValue(name).orElse(null);
    }

    private static URI uri(String pathAndQuery) {
        return URI.create("http://127.0.0.1:" + port + pathAndQuery);
    }
}
