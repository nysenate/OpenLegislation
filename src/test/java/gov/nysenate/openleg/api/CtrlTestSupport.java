package gov.nysenate.openleg.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.common.util.OutputUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/** Shared setup and request helpers for standalone controller unit tests. */
public final class CtrlTestSupport {
    private CtrlTestSupport() {}

    /** A MockMvc for the controllers, with the production JSON mapper. */
    public static MockMvc mockMvc(Object... controllers) {
        return standaloneSetup(controllers)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(OutputUtils.failOnUnknownMapper))
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
