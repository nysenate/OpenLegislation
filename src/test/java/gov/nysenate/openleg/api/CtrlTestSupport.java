package gov.nysenate.openleg.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.config.ApplicationConfig;
import gov.nysenate.openleg.config.WebApplicationConfig;
import jakarta.servlet.ServletContext;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Shared setup and request helpers for standalone controller unit tests. */
public final class CtrlTestSupport {
    private CtrlTestSupport() {}

    /** Accept headers (null for none) to send each request with. */
    public static final List<String> ACCEPTS = Arrays.asList(null, "*/*", "application/json", "text/html",
            "application/pdf", "application/pdf, application/json;q=0.5",
            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

    /**
     * A MockMvc for the controllers, with a converter for PDF bytes and, from {@link WebApplicationConfig}, the
     * JSON converter, the content negotiation and the {@link ApiErrorResolver}.
     */
    public static MockMvc mockMvc(Object... controllers) {
        var config = new WebApplicationConfig(new ApplicationConfig());
        List<HttpMessageConverter<?>> converters = List.of(new ByteArrayHttpMessageConverter(),
                config.jackson2Converter());
        var contentNegotiation = contentNegotiation(config);
        // The dispatcher finds the resolver among the beans, as it does in the application.
        var builder = new StandaloneMockMvcBuilder(controllers) {
            @Override
            protected Map<String, Object> extendMvcSingletons(ServletContext servletContext) {
                return Map.of("apiErrorResolver", config.apiErrorResolver());
            }
        };
        return builder
                .setMessageConverters(converters.toArray(HttpMessageConverter[]::new))
                .setContentNegotiationManager(contentNegotiation)
                // The standalone builder doesn't pass its manager to the handler mapping.
                .setCustomHandlerMapping(() -> {
                    var mapping = new RequestMappingHandlerMapping();
                    mapping.setContentNegotiationManager(contentNegotiation);
                    return mapping;
                })
                .build();
    }

    /** The content negotiation manager the configuration sets up for the application. */
    private static ContentNegotiationManager contentNegotiation(WebApplicationConfig config) {
        var configurer = new ContentNegotiationConfigurer() {
            ContentNegotiationManager build() {
                return buildContentNegotiationManager();
            }
        };
        config.configureContentNegotiation(configurer);
        return configurer.build();
    }

    /** Binds a Shiro subject that holds every role and permission to the current thread. */
    public static void bindAllRolesSubject() {
        Subject subject = mock(Subject.class);
        when(subject.hasRole(anyString())).thenReturn(true);
        when(subject.isPermitted(anyString())).thenReturn(true);
        when(subject.isPermitted(any(org.apache.shiro.authz.Permission.class))).thenReturn(true);
        ThreadContext.bind(subject);
    }

    public static void unbindSubject() {
        ThreadContext.unbindSubject();
    }

    /** Asserts that the request returns 200, and returns its body. */
    public static JsonNode ok(MockMvc mvc, String path, String... params) throws Exception {
        MvcResult result = perform(mvc, path, params);
        assertEquals(path + query(params), 200, result.getResponse().getStatus());
        return json(result);
    }

    /** Asserts a paginated response's total and offsets. */
    public static void assertPage(JsonNode body, int total, int offsetStart, int offsetEnd) {
        assertPage(null, body, total, offsetStart, offsetEnd);
    }

    /** Asserts a paginated response's total and offsets, with a message identifying the request. */
    public static void assertPage(String message, JsonNode body, int total, int offsetStart, int offsetEnd) {
        assertEquals(message, total, body.get("total").asInt());
        assertEquals(message, offsetStart, body.get("offsetStart").asInt());
        assertEquals(message, offsetEnd, body.get("offsetEnd").asInt());
    }

    /** Performs a GET of the path with raw (still encoded) query parameters. */
    public static MvcResult perform(MockMvc mvc, String path, String... params) throws Exception {
        return mvc.perform(get(URI.create(path + query(params)))).andReturn();
    }

    /** Performs a GET of the path with the Accept header, or none if it is null. */
    public static MvcResult performAccepting(MockMvc mvc, String path, String accept) throws Exception {
        var request = get(URI.create(path));
        if (accept != null) {
            request.header("Accept", accept);
        }
        return mvc.perform(request).andReturn();
    }

    /** Asserts that the path returns 200 with a PDF for every Accept header in {@link #ACCEPTS}. */
    public static void assertServesPdf(MockMvc mvc, String path) throws Exception {
        for (String accept : ACCEPTS) {
            String message = path + " Accept: " + accept;
            MvcResult result = performAccepting(mvc, path, accept);
            assertEquals(message, 200, result.getResponse().getStatus());
            assertContentType(message, MediaType.APPLICATION_PDF, result);
            assertTrue(message, result.getResponse().getContentAsString().startsWith("%PDF"));
        }
    }

    /**
     * Asserts that the path returns the status and JSON body for every Accept header in {@link #ACCEPTS}
     * that it returns without one, and returns that body.
     */
    public static JsonNode assertJsonForEveryAccept(MockMvc mvc, String path, int status) throws Exception {
        JsonNode body = null;
        for (MvcResult result : assertJsonStatusForEveryAccept(mvc, () -> get(URI.create(path)), status)) {
            JsonNode json = json(result);
            if (body == null) {
                body = json;
            }
            assertEquals(message(result), body, json);
        }
        return body;
    }

    /**
     * Asserts that the request returns the status and an error response with the error code for every Accept
     * header in {@link #ACCEPTS}, and returns the last response.
     */
    public static MockHttpServletResponse assertErrorForEveryAccept(MockMvc mvc,
            Supplier<MockHttpServletRequestBuilder> request, int status, int errorCode) throws Exception {
        MockHttpServletResponse response = null;
        for (MvcResult result : assertJsonStatusForEveryAccept(mvc, request, status)) {
            assertEquals(message(result), errorCode, json(result).get("errorCode").asInt());
            response = result.getResponse();
        }
        return response;
    }

    /**
     * Performs the request with each Accept header in {@link #ACCEPTS}, asserts that each returns the status
     * with a JSON Content-Type, and returns the results in that order.
     */
    private static List<MvcResult> assertJsonStatusForEveryAccept(MockMvc mvc,
            Supplier<MockHttpServletRequestBuilder> request, int status) throws Exception {
        List<MvcResult> results = new ArrayList<>();
        for (String accept : ACCEPTS) {
            MockHttpServletRequestBuilder builder = request.get();
            if (accept != null) {
                builder.header("Accept", accept);
            }
            MvcResult result = mvc.perform(builder).andReturn();
            assertEquals(message(result), status, result.getResponse().getStatus());
            assertContentType(message(result), MediaType.APPLICATION_JSON, result);
            results.add(result);
        }
        return results;
    }

    /** The request's method, path, query and Accept header, to identify it in assertion messages. */
    private static String message(MvcResult result) {
        var request = result.getRequest();
        String query = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        return request.getMethod() + " " + request.getRequestURI() + query
                + " Accept: " + request.getHeader("Accept");
    }

    private static void assertContentType(String message, MediaType expected, MvcResult result) {
        String contentType = result.getResponse().getContentType();
        assertTrue(message + " Content-Type: " + contentType,
                contentType != null && expected.equalsTypeAndSubtype(MediaType.parseMediaType(contentType)));
    }

    public static JsonNode json(MvcResult result) throws Exception {
        return json(result.getResponse());
    }

    public static JsonNode json(MockHttpServletResponse response) throws Exception {
        return json(response.getContentAsString());
    }

    public static JsonNode json(String content) throws Exception {
        return new ObjectMapper().readTree(content);
    }

    public static String query(String... params) {
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < params.length; i += 2) {
            query.append(query.isEmpty() ? "?" : "&").append(params[i]).append('=').append(params[i + 1]);
        }
        return query.toString();
    }

    public static String[] concat(String[] first, String... second) {
        String[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
