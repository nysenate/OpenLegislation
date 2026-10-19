package gov.nysenate.openleg.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.openleg.common.util.OutputUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/** Shared setup and expectations for the collection full retirement controller tests. */
public final class CollectionFullTestSupport {
    /** Raw query values that Spring binds to a true {@code boolean} request parameter. */
    public static final List<String> BOUND_TRUE_VALUES = List.of("true", "True", "TRUE", "1", "yes", "on", "%54rue");
    /** Raw query values that Spring binds to a false {@code boolean} request parameter. */
    public static final List<String> BOUND_FALSE_VALUES = List.of("false", "0", "");
    /** Raw query values that {@link BaseCtrl#getBooleanParam} parses as true. */
    public static final List<String> PARSED_TRUE_VALUES =
            List.of("true", "True", "TRUE", "1", "yes", "on", "y", "t", "%54rue");
    /** Raw query values that {@link BaseCtrl#getBooleanParam} parses as false, including unrecognised ones. */
    public static final List<String> PARSED_FALSE_VALUES = List.of("false", "0", "", "no", "f", "maybe");

    private static final String DOMAIN_URL = "http://example.test/legislation";
    private static final CollectionFullPolicy ENFORCE_POLICY = new CollectionFullPolicy("enforce", DOMAIN_URL);

    private CollectionFullTestSupport() {}

    public static MockMvc mockMvc(Function<CollectionFullPolicy, BaseCtrl> controller, String mode) {
        return standaloneSetup(controller.apply(new CollectionFullPolicy(mode, DOMAIN_URL)))
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

    /** The documented 400 body for a rejected option. */
    public static JsonNode expectedRejection(CollectionFullPolicy.Option option, String receivedValue) {
        ObjectNode body = OutputUtils.failOnUnknownMapper.createObjectNode()
                .put("success", false)
                .put("message", ENFORCE_POLICY.getMessage(option))
                .put("responseType", "error")
                .put("errorCode", 1)
                .put("errorDataType", "invalid-parameter");
        ObjectNode errorData = body.putObject("errorData");
        errorData.putObject("parameterConstraint")
                .put("name", option.getParameterName())
                .put("type", "boolean")
                .put("constraint", ENFORCE_POLICY.getConstraint(option));
        errorData.put("receivedValue", receivedValue);
        return body;
    }

    /** The decoded value the server receives for a raw query value. */
    public static String decoded(String rawValue) {
        return rawValue.equals("%54rue") ? "True" : rawValue;
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

    /** Performs a GET of the path with raw (still encoded) query parameters. */
    public static MvcResult perform(MockMvc mvc, String path, String... params) throws Exception {
        return mvc.perform(get(URI.create(path + query(params)))).andReturn();
    }

    public static JsonNode json(MvcResult result) throws Exception {
        return new ObjectMapper().readTree(result.getResponse().getContentAsString());
    }
}
