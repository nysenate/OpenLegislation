package gov.nysenate.openleg.api;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static gov.nysenate.openleg.api.CtrlTestSupport.assertErrorForEveryAccept;
import static gov.nysenate.openleg.api.CtrlTestSupport.json;
import static gov.nysenate.openleg.api.CtrlTestSupport.mockMvc;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

/** Errors Spring MVC raises when no endpoint can take the request: under /api/3 they are error responses. */
public class ApiErrorResolverTest {
    private static final String API = BaseCtrl.BASE_API_PATH;

    @RestController
    static class ItemCtrl extends BaseCtrl {
        @GetMapping({API + "/items/{year:\\d{4}}", "/items/{year:\\d{4}}"})
        public BaseResponse item() {
            return new SimpleResponse(true, "", "test");
        }

        @PostMapping(value = API + "/items", consumes = MediaType.APPLICATION_JSON_VALUE)
        public BaseResponse addItem(@RequestBody Map<String, String> item) {
            return new SimpleResponse(true, "", "test");
        }
    }

    private final MockMvc mvc = mockMvc(new ItemCtrl());

    @Test
    public void unknownPathUnderApiIsNoSuchEndpoint() throws Exception {
        for (String path : new String[]{API, API + "/nonexistent", API + "/items/20x5"}) {
            JsonNode body = json(assertErrorForEveryAccept(mvc, () -> get(path), 404, 4));
            assertEquals("No API endpoint matches this path", body.get("message").asText());
        }
    }

    @Test
    public void unknownPathOutsideApiKeepsSpringHandling() throws Exception {
        MockHttpServletResponse response = mvc.perform(get("/nonexistent").header("Accept", "application/json"))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());
        assertNull(response.getContentType());
    }

    @Test
    public void unsupportedMethodUnderApiIsMethodNotAllowed() throws Exception {
        for (HttpMethod method : new HttpMethod[]{HttpMethod.POST, HttpMethod.PUT}) {
            MockHttpServletResponse response =
                    assertErrorForEveryAccept(mvc, () -> request(method, API + "/items/2025"), 405, 5);
            JsonNode body = json(response);
            assertEquals("This endpoint doesn't support the request's HTTP method", body.get("message").asText());
            assertFalse(body.has("errorData"));
            assertEquals("GET", response.getHeader("Allow"));
        }
    }

    @Test
    public void unsupportedRequestBodyUnderApiIsUnsupportedMediaType() throws Exception {
        MockHttpServletResponse response = assertErrorForEveryAccept(mvc,
                () -> post(API + "/items").contentType(MediaType.TEXT_PLAIN).content("text"), 415, 6);
        JsonNode body = json(response);
        assertEquals("The request body's Content-Type isn't supported", body.get("message").asText());
        assertFalse(body.has("errorData"));
        assertEquals("application/json", response.getHeader("Accept"));
    }

    @Test
    public void optionsStillAnswered() throws Exception {
        assertEquals(200, mvc.perform(request(HttpMethod.OPTIONS, API + "/items/2025")).andReturn()
                .getResponse().getStatus());
    }
}
