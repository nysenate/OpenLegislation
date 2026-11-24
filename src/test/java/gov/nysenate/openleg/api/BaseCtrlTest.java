package gov.nysenate.openleg.api;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.common.util.OutputUtils;
import jakarta.servlet.ServletException;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.CtrlTestSupport.json;
import static gov.nysenate.openleg.api.CtrlTestSupport.mockMvc;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class BaseCtrlTest {
    private final BaseCtrl ctrl = new BaseCtrl() {};

    @RestController
    static class NotAcceptableCtrl extends BaseCtrl {
        @GetMapping("/not-acceptable")
        public Object notAcceptable() throws HttpMediaTypeNotAcceptableException {
            throw new HttpMediaTypeNotAcceptableException(List.of(MediaType.APPLICATION_PDF));
        }

        /** Declares no media type, so only writing the response finds that the client accepts no JSON. */
        @GetMapping("/json-body")
        public BaseResponse jsonBody() {
            return new SimpleResponse(true, "", "test");
        }

        @PostMapping("/json-request")
        public BaseResponse jsonRequest(@RequestBody Map<String, String> body) {
            return new SimpleResponse(true, "", "test");
        }

        @GetMapping("/invalid-param")
        public BaseResponse invalidParam() {
            throw new InvalidRequestParamEx("x", "param", "string", "valid");
        }
    }

    /** Only the PDF endpoints answer errors in JSON for requests that don't accept it. */
    @Test
    public void errorsOutsidePdfEndpointsStillHonorAccept() {
        var mvc = mockMvc(new NotAcceptableCtrl());
        ServletException ex = assertThrows(ServletException.class,
                () -> mvc.perform(get("/invalid-param").header("Accept", "text/html")));
        assertTrue(ex.getCause() instanceof InvalidRequestParamEx);
    }

    @Test
    public void unsupportedRequestBodyIsNotLabelledJson() throws Exception {
        MvcResult result = mockMvc(new NotAcceptableCtrl())
                .perform(post("/json-request").contentType(MediaType.TEXT_PLAIN).content("text"))
                .andReturn();
        assertEquals(406, result.getResponse().getStatus());
        assertNull(result.getResponse().getContentType());
    }

    @Test
    public void unwritableBodyStaysNotAcceptable() throws Exception {
        MvcResult result = mockMvc(new NotAcceptableCtrl())
                .perform(get("/json-body").header("Accept", "text/html"))
                .andReturn();
        assertEquals(406, result.getResponse().getStatus());
    }

    @Test
    public void notAcceptableHandlerResolvesThroughSpring() throws Exception {
        MvcResult result = mockMvc(new NotAcceptableCtrl())
                .perform(get("/not-acceptable").header("Accept", "application/json"))
                .andReturn();
        assertEquals(400, result.getResponse().getStatus());
        JsonNode body = json(result);
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
