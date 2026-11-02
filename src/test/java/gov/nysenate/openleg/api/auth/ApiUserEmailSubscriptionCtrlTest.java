package gov.nysenate.openleg.api.auth;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.common.util.OutputUtils;
import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import static gov.nysenate.openleg.api.CtrlTestSupport.json;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/** Changing an API user's address, as the subscription preferences page does. */
@Category(UnitTest.class)
public class ApiUserEmailSubscriptionCtrlTest {
    private static final String KEY = "abc123";

    private ApiUserService apiUserService;
    private MockMvc mvc;

    @Before
    public void setUp() {
        apiUserService = mock(ApiUserService.class);
        // No one has the address yet.
        when(apiUserService.getUser(anyString())).thenThrow(new EmptyResultDataAccessException(1));
        // As in production, a plain-text body is read as a string and responses are written as JSON.
        mvc = standaloneSetup(new ApiUserEmailSubscriptionCtrl(apiUserService))
                .setMessageConverters(new StringHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(OutputUtils.failOnUnknownMapper))
                .build();
    }

    @Test
    public void addressTheServiceRefusesGetsTheInvalidEmailError() throws Exception {
        doThrow(new IllegalArgumentException("Invalid email address")).when(apiUserService)
                .updateEmail(KEY, "still bad@example.com");

        JsonNode response = updateEmail("still bad@example.com");

        assertFalse(response.get("success").asBoolean());
        assertEquals(ErrorCode.INVALID_EMAIL.getCode(), response.get("errorCode").asInt());
        assertEquals(ErrorCode.INVALID_EMAIL.getMessage(), response.get("message").asText());
    }

    @Test
    public void validNewAddressIsStored() throws Exception {
        JsonNode response = updateEmail("a_b@x.com");

        assertTrue(response.toString(), response.get("success").asBoolean());
        verify(apiUserService).updateEmail(KEY, "a_b@x.com");
    }

    @Test
    public void addressInUseIsNotStored() throws Exception {
        doReturn(new ApiUser("taken@example.com")).when(apiUserService).getUser("taken@example.com");

        JsonNode response = updateEmail("taken@example.com");

        assertFalse(response.get("success").asBoolean());
        assertEquals(ErrorCode.EMAIL_IN_USE.getCode(), response.get("errorCode").asInt());
        verify(apiUserService, never()).updateEmail(any(), any());
    }

    /** Posts the new address as the page does, as the plain-text body. */
    private JsonNode updateEmail(String email) throws Exception {
        return json(mvc.perform(post("/api/3/email/subscription/updateEmail").param("key", KEY)
                .contentType(MediaType.TEXT_PLAIN).characterEncoding("UTF-8").content(email)).andReturn());
    }
}
