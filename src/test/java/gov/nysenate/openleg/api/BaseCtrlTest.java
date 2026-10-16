package gov.nysenate.openleg.api;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.common.util.OutputUtils;
import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Category(UnitTest.class)
public class BaseCtrlTest {
    private final BaseCtrl ctrl = new BaseCtrl() {};

    @RestController
    static class NotAcceptableCtrl extends BaseCtrl {
        @GetMapping("/not-acceptable")
        public Object notAcceptable() throws HttpMediaTypeNotAcceptableException {
            throw new HttpMediaTypeNotAcceptableException(List.of(MediaType.APPLICATION_PDF));
        }
    }

    @Test
    public void notAcceptableHandlerResolvesThroughSpring() throws Exception {
        MvcResult result = standaloneSetup(new NotAcceptableCtrl())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(OutputUtils.failOnUnknownMapper))
                .build()
                .perform(get("/not-acceptable").header("Accept", "application/json"))
                .andReturn();
        assertEquals(400, result.getResponse().getStatus());
        JsonNode body = OutputUtils.failOnUnknownMapper.readTree(result.getResponse().getContentAsString());
        assertEquals("Accept", body.at("/errorData/parameterConstraint/name").asText());
        assertEquals("application/json", body.at("/errorData/receivedValue").asText());
    }

    @Test
    public void notAcceptableReportsAcceptHeaderAndSupportedTypes() throws Exception {
        JsonNode body = notAcceptable(new HttpMediaTypeNotAcceptableException(List.of(MediaType.APPLICATION_PDF)),
                "text/csv");
        assertEquals(1, body.get("errorCode").asInt());
        assertEquals("invalid-parameter", body.get("errorDataType").asText());
        JsonNode errorData = body.get("errorData");
        assertEquals("Accept", errorData.at("/parameterConstraint/name").asText());
        assertEquals("media type", errorData.at("/parameterConstraint/type").asText());
        assertEquals("Must accept one of: application/pdf", errorData.at("/parameterConstraint/constraint").asText());
        assertEquals("text/csv", errorData.get("receivedValue").asText());
        assertFalse(body.toString().contains("Exception"));
    }

    @Test
    public void notAcceptableWithoutSupportedTypesOrAcceptHeader() throws Exception {
        JsonNode body = notAcceptable(new HttpMediaTypeNotAcceptableException("Could not find acceptable representation"),
                null);
        assertEquals("Must accept a supported media type", body.at("/errorData/parameterConstraint/constraint").asText());
        assertTrue(body.at("/errorData/receivedValue").isNull());
        assertFalse(body.toString().contains("Exception"));
    }

    @Test
    public void invalidRequestParamKeepsGenericMessageAndSerializesDetails() {
        JsonNode body = invalidRequestParam(new InvalidRequestParamEx("5000", "limit", "int", "Must be > 0 and <= 1000"));
        assertEquals(ErrorCode.INVALID_ARGUMENTS.getMessage(), body.get("message").asText());
        assertEquals(1, body.get("errorCode").asInt());
        assertEquals("invalid-parameter", body.get("errorDataType").asText());
        JsonNode errorData = body.get("errorData");
        assertEquals("limit", errorData.at("/parameterConstraint/name").asText());
        assertEquals("int", errorData.at("/parameterConstraint/type").asText());
        assertEquals("Must be > 0 and <= 1000", errorData.at("/parameterConstraint/constraint").asText());
        assertEquals("5000", errorData.get("receivedValue").asText());
    }

    @Test
    public void invalidRequestParamResponseMessageReplacesGenericMessage() {
        JsonNode body = invalidRequestParam(
                new InvalidRequestParamEx("true", "full", "boolean", "Must be false", "Omit full"));
        assertEquals("Omit full", body.get("message").asText());
        assertEquals(1, body.get("errorCode").asInt());
        assertEquals("full", body.at("/errorData/parameterConstraint/name").asText());
        assertEquals("Must be false", body.at("/errorData/parameterConstraint/constraint").asText());
        assertEquals("true", body.at("/errorData/receivedValue").asText());
    }

    @Test
    public void retiredRequestParamCarriesGuidanceAsResponseMessage() {
        JsonNode body = invalidRequestParam(
                new RetiredRequestParamEx("True", "full", "boolean", "Must be false", "Omit full"));
        assertEquals("Omit full", body.get("message").asText());
        assertEquals("True", body.at("/errorData/receivedValue").asText());
    }

    private JsonNode invalidRequestParam(InvalidRequestParamEx ex) {
        return OutputUtils.failOnUnknownMapper.valueToTree(ctrl.handleInvalidRequestParameterException(ex));
    }

    private JsonNode notAcceptable(HttpMediaTypeNotAcceptableException ex, String accept) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (accept != null) {
            request.addHeader("Accept", accept);
        }
        Object response = ctrl.handleHttpMediaTypeNotAcceptableExceptionException(ex, new ServletWebRequest(request));
        return OutputUtils.failOnUnknownMapper.valueToTree(response);
    }
}
