package gov.nysenate.openleg.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.common.util.OutputUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/** Shared setup and request helpers for standalone controller unit tests. */
public final class CtrlTestSupport {
    private CtrlTestSupport() {}

    /** Accept headers (null for none) that a PDF endpoint serves its PDF for. */
    public static final List<String> PDF_ACCEPTS = Arrays.asList(null, "application/pdf", "*/*", "application/json",
            "application/pdf, application/json;q=0.5",
            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

    /**
     * A MockMvc for the controllers, with the production JSON mapper, a converter for PDF bytes, and
     * the production exception resolvers: {@link PdfErrorResolver} ahead of Spring's defaults.
     */
    public static MockMvc mockMvc(Object... controllers) {
        List<HttpMessageConverter<?>> converters = List.of(new ByteArrayHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter(OutputUtils.failOnUnknownMapper));
        var exceptionHandlerResolver = new ExceptionHandlerExceptionResolver();
        exceptionHandlerResolver.setMessageConverters(converters);
        return standaloneSetup(controllers)
                .setMessageConverters(converters.toArray(HttpMessageConverter[]::new))
                .setHandlerExceptionResolvers(new PdfErrorResolver(), exceptionHandlerResolver,
                        new ResponseStatusExceptionResolver(), new DefaultHandlerExceptionResolver())
                .build();
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

    /** Asserts that the path returns 200 with a PDF for every Accept header in {@link #PDF_ACCEPTS}. */
    public static void assertServesPdf(MockMvc mvc, String path) throws Exception {
        for (String accept : PDF_ACCEPTS) {
            String message = path + " Accept: " + accept;
            MvcResult result = performAccepting(mvc, path, accept);
            assertEquals(message, 200, result.getResponse().getStatus());
            assertContentType(message, MediaType.APPLICATION_PDF, result);
            assertTrue(message, result.getResponse().getContentAsString().startsWith("%PDF"));
        }
    }

    /**
     * Asserts that the path returns the status and JSON error body with Accept: application/pdf
     * that it returns without an Accept header.
     */
    public static void assertJsonErrorWhenPdfAccepted(MockMvc mvc, String path, int status) throws Exception {
        MvcResult noAccept = performAccepting(mvc, path, null);
        MvcResult pdf = performAccepting(mvc, path, MediaType.APPLICATION_PDF_VALUE);
        assertEquals(path, status, noAccept.getResponse().getStatus());
        assertEquals(path + " Accept: application/pdf", status, pdf.getResponse().getStatus());
        assertContentType(path, MediaType.APPLICATION_JSON, pdf);
        assertEquals(path, json(noAccept), json(pdf));
    }

    /** Asserts that the path returns 406 for the Accept header. */
    public static void assertNotAcceptable(MockMvc mvc, String path, String accept) throws Exception {
        assertEquals(path + " Accept: " + accept, 406, performAccepting(mvc, path, accept).getResponse().getStatus());
    }

    private static void assertContentType(String message, MediaType expected, MvcResult result) {
        String contentType = result.getResponse().getContentType();
        assertTrue(message + " Content-Type: " + contentType,
                contentType != null && expected.equalsTypeAndSubtype(MediaType.parseMediaType(contentType)));
    }

    public static JsonNode json(MvcResult result) throws Exception {
        return json(result.getResponse().getContentAsString());
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
