package gov.nysenate.openleg.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.openleg.common.util.OutputUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.function.Function;

import static gov.nysenate.openleg.api.CtrlTestSupport.*;
import static org.junit.Assert.assertEquals;

/**
 * Shared setup and expectations for the collection full retirement controller tests.
 * General controller test helpers are in {@link CtrlTestSupport}.
 */
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
    private static final CollectionFullPolicy ENFORCE_POLICY = policy("enforce");

    private CollectionFullTestSupport() {}

    public static CollectionFullPolicy policy(String mode) {
        return new CollectionFullPolicy(mode, DOMAIN_URL);
    }

    public static MockMvc mockMvc(Function<CollectionFullPolicy, BaseCtrl> controller, String mode) {
        return CtrlTestSupport.mockMvc(controller.apply(policy(mode)));
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

    /** Asserts that the request returns the documented 400 for a rejected option. */
    public static void assertRejected(MockMvc mvc, String path, CollectionFullPolicy.Option option,
                                      String receivedValue, String... params) throws Exception {
        MvcResult result = perform(mvc, path, params);
        assertEquals(path + query(params), 400, result.getResponse().getStatus());
        assertEquals(path + query(params), expectedRejection(option, receivedValue), json(result));
    }

    /** Asserts that off and enforce return the same 200 body for the request, and returns it. */
    public static JsonNode assertSameInBothModes(MockMvc offMvc, MockMvc enforceMvc, String path, String... params)
            throws Exception {
        JsonNode off = ok(offMvc, path, params);
        assertEquals(path + query(params), off, ok(enforceMvc, path, params));
        return off;
    }

    /** The decoded value the server receives for a raw query value. */
    public static String decoded(String rawValue) {
        return rawValue.equals("%54rue") ? "True" : rawValue;
    }
}
