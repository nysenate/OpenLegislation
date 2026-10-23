package gov.nysenate.openleg.api.admin;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.notifications.mail.MailConfigurationException;
import gov.nysenate.openleg.notifications.mail.apiuser.ApiUserBatchEmailServiceImpl;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailInProgressException;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailStatus;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.UnsentRecipient;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.api.CtrlTestSupport.json;
import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.FAILED;
import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.NOT_SENT;
import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.SKIPPED;
import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.UNCONFIRMED;
import static gov.nysenate.openleg.api.CtrlTestSupport.mockMvc;
import static gov.nysenate.openleg.api.CtrlTestSupport.ok;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** The JSON the admin batch email page reads from the send and status endpoints. */
@Category(UnitTest.class)
public class AdminApiEmailCtrlTest {
    private static final String REQUEST =
            "{\"subscriptions\":[\"BREAKING_CHANGES\"],\"subject\":\"Subject\",\"body\":\"Body\"}";
    private static final LocalDateTime STARTED = LocalDateTime.of(2026, 10, 23, 15, 0);

    private ApiUserBatchEmailServiceImpl service;
    private MockMvc mvc;

    @Before
    public void setUp() {
        service = mock(ApiUserBatchEmailServiceImpl.class);
        mvc = mockMvc(new AdminApiEmailCtrl(service));
    }

    @After
    public void tearDown() {
        ThreadContext.unbindSubject();
    }

    @Test
    public void startedBatchRespondsAtOnceWithItsStatus() throws Exception {
        when(service.startMessage(any())).thenReturn(new BatchEmailStatus("Subject", 2000,
                new BatchEmailResult(0, List.of()), STARTED, null));

        JsonNode body = send("/api/3/admin/email/batchEmail", 200);

        assertTrue(body.get("success").asBoolean());
        assertEquals("batch-email-status", body.get("responseType").asText());
        JsonNode result = body.get("result");
        assertTrue(result.get("running").asBoolean());
        assertEquals("Subject", result.get("subject").asText());
        assertEquals(2000, result.get("recipients").asInt());
        assertEquals(0, result.get("sent").asInt());
        assertEquals(0, result.get("unsent").size());
        assertEquals("2026-10-23T15:00:00", result.get("started").asText());
        assertTrue(result.get("finished").isNull());
    }

    @Test
    public void statusListsEachUnsentRecipient() throws Exception {
        when(service.getStatus()).thenReturn(new BatchEmailStatus("Subject", 6, new BatchEmailResult(2, List.of(
                new UnsentRecipient("bad address@example.com", SKIPPED, "Not a valid email address"),
                new UnsentRecipient("b@example.com", FAILED, "451 Rejected"),
                new UnsentRecipient("c@example.com", UNCONFIRMED, "Read timed out"),
                new UnsentRecipient(null, NOT_SENT, "Connection refused"))), STARTED, STARTED.plusMinutes(9)));

        JsonNode body = ok(mvc, "/api/3/admin/email/batchEmail");

        assertTrue(body.get("success").asBoolean());
        JsonNode result = body.get("result");
        assertEquals(false, result.get("running").asBoolean());
        assertEquals("2026-10-23T15:09:00", result.get("finished").asText());
        assertEquals(2, result.get("sent").asInt());
        assertEquals("bad address@example.com", result.at("/unsent/0/email").asText());
        assertEquals("SKIPPED", result.at("/unsent/0/status").asText());
        assertEquals("Not a valid email address", result.at("/unsent/0/reason").asText());
        assertEquals("FAILED", result.at("/unsent/1/status").asText());
        assertEquals("UNCONFIRMED", result.at("/unsent/2/status").asText());
        assertTrue(result.at("/unsent/3/email").isNull());
        assertEquals("NOT_SENT", result.at("/unsent/3/status").asText());
        assertEquals("Connection refused", result.at("/unsent/3/reason").asText());
    }

    @Test
    public void statusBeforeAnyBatchHasNoResult() throws Exception {
        JsonNode body = ok(mvc, "/api/3/admin/email/batchEmail");

        assertTrue(body.get("success").asBoolean());
        assertTrue(body.path("result").isMissingNode() || body.get("result").isNull());
    }

    @Test
    public void batchWhileAnotherIsBeingSentIsAConflict() throws Exception {
        when(service.startMessage(any())).thenThrow(new BatchEmailInProgressException("Earlier"));

        JsonNode body = send("/api/3/admin/email/batchEmail", 409);

        assertEquals(false, body.get("success").asBoolean());
        assertEquals(ErrorCode.BATCH_EMAIL_IN_PROGRESS.getCode(), body.get("errorCode").asInt());
        assertEquals("Batch email \"Earlier\" is still being sent. Wait for it to finish before sending another.",
                body.get("message").asText());
    }

    @Test
    public void fullySentTestEmailHasAnEmptyListForThePage() throws Exception {
        Subject subject = mock(Subject.class);
        when(subject.getPrincipal()).thenReturn("admin@example.com");
        ThreadContext.bind(subject);
        when(service.sendTestMessage(eq("admin@example.com"), any()))
                .thenReturn(new BatchEmailResult(1, List.of()));

        JsonNode body = send("/api/3/admin/email/testModeEmail", 200);

        assertTrue(body.get("success").asBoolean());
        assertEquals("Sent 1 email", body.get("message").asText());
        assertTrue(body.at("/result/unsent").isArray());
        assertEquals(0, body.at("/result/unsent").size());
    }

    @Test
    public void mailSettingProblemIsAnErrorWithTheReason() throws Exception {
        when(service.startMessage(any())).thenThrow(new MailConfigurationException(List.of("mail.smtp.host is blank")));

        JsonNode body = send("/api/3/admin/email/batchEmail", 503);

        assertEquals(false, body.get("success").asBoolean());
        assertEquals("Mail is not configured: mail.smtp.host is blank.", body.get("message").asText());
    }

    private JsonNode send(String path, int status) throws Exception {
        MvcResult result = mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(REQUEST)).andReturn();
        assertEquals(result.getResponse().getContentAsString(), status, result.getResponse().getStatus());
        return json(result);
    }
}
