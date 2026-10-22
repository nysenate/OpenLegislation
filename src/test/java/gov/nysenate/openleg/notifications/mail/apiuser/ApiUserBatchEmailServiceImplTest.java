package gov.nysenate.openleg.notifications.mail.apiuser;

import gov.nysenate.openleg.auth.admin.AdminUserService;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.config.annotation.UnitTest;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ApiUserBatchEmailServiceImplTest {
    private static final String KEY = "abc123";
    private static final String PREFERENCES_URL = "https://legislation.example.org/subscriptions?key=" + KEY;
    private static final ApiUserMessage MESSAGE =
            new ApiUserMessage(ApiUserSubscriptionType.BREAKING_CHANGES, "Subject", "<p>Body</p>");

    private Session session;
    private ApiUserBatchEmailServiceImpl service;

    @Before
    public void setUp() {
        session = Session.getInstance(new Properties());
        MailUtils mailUtils = mock(MailUtils.class);
        when(mailUtils.getSmtpSession()).thenReturn(session);
        OpenLegEnvironment env = mock(OpenLegEnvironment.class);
        when(env.getUrl()).thenReturn("https://legislation.example.org");
        service = new ApiUserBatchEmailServiceImpl(mailUtils, env,
                mock(ApiUserService.class), mock(AdminUserService.class));
    }

    @Test
    public void userMessageHasOnePreferencesLinkPerPart() throws Exception {
        ApiUser user = new ApiUser("user@example.com");
        user.setApiKey(KEY);
        Multipart parts = send(service.getMimeMessage(MESSAGE, user.getEmail(), user));

        String text = (String) parts.getBodyPart(0).getContent();
        String html = (String) parts.getBodyPart(1).getContent();
        for (String part : new String[] {text, html}) {
            assertEquals(part, 1, occurrences(part, "/subscriptions"));
            assertEquals(part, 1, occurrences(part, PREFERENCES_URL));
            assertTrue(part, part.contains("manage your subscriptions or unsubscribe"));
            assertFalse(part, part.contains("unsub="));
        }
        assertTrue(text, text.endsWith(PREFERENCES_URL));
        assertTrue(html, html.contains("href=\"" + PREFERENCES_URL + "\""));
    }

    @Test
    public void adminAndTestCopiesHaveNoSubscriptionLinks() throws Exception {
        Multipart parts = send(service.getMimeMessage(MESSAGE, "admin@example.com", null));
        for (int i = 0; i < parts.getCount(); i++) {
            String part = (String) parts.getBodyPart(i).getContent();
            assertFalse(part, part.contains("/subscriptions"));
            assertFalse(part, part.contains("unsub"));
        }
    }

    /** Serializes and re-parses the message, as a recipient would receive it. */
    private Multipart send(MimeMessage message) throws Exception {
        message.saveChanges();
        var out = new ByteArrayOutputStream();
        message.writeTo(out);
        var received = new MimeMessage(session, new ByteArrayInputStream(out.toByteArray()));
        Multipart parts = (Multipart) received.getContent();
        assertEquals(2, parts.getCount());
        assertTrue(parts.getBodyPart(0).isMimeType("text/plain"));
        assertTrue(parts.getBodyPart(1).isMimeType("text/html"));
        return parts;
    }

    private static int occurrences(String text, String target) {
        return text.split(java.util.regex.Pattern.quote(target), -1).length - 1;
    }
}
