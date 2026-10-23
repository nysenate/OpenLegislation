package gov.nysenate.openleg.notifications.mail.apiuser;

import gov.nysenate.openleg.auth.admin.AdminUserService;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.notifications.mail.MailConfigurationException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ApiUserBatchEmailServiceImplTest {
    private static final String FROM = "openleg@example.org";
    private static final String KEY = "abc123";
    private static final String PREFERENCES_URL = "https://legislation.example.org/subscriptions?key=" + KEY;
    private static final String SIGNATURE = "https://github.com/nysenate/OpenLegislation";
    private static final ApiUserMessage MESSAGE =
            new ApiUserMessage(ApiUserSubscriptionType.BREAKING_CHANGES, "Subject", "Body");

    private Session session;
    private MailUtils mailUtils;
    private ApiUserService apiUserService;
    private AdminUserService adminUserService;
    private ApiUserBatchEmailServiceImpl service;
    private ApiUser user;

    @Before
    public void setUp() {
        session = Session.getInstance(new Properties());
        mailUtils = mock(MailUtils.class);
        when(mailUtils.getSmtpSession()).thenReturn(session);
        OpenLegEnvironment env = mock(OpenLegEnvironment.class);
        when(env.getUrl()).thenReturn("https://legislation.example.org");
        when(env.getEmailFromAddress()).thenReturn(FROM);
        apiUserService = mock(ApiUserService.class);
        adminUserService = mock(AdminUserService.class);
        service = new ApiUserBatchEmailServiceImpl(mailUtils, env, apiUserService, adminUserService);
        user = new ApiUser("user@example.com");
        user.setApiKey(KEY);
    }

    @Test
    public void everyCopyIsFromTheConfiguredSender() throws Exception {
        for (ApiUser recipient : new ApiUser[] {user, null}) {
            MimeMessage received = receive(service.getMimeMessage(MESSAGE, "user@example.com", recipient));
            assertArrayEquals(new InternetAddress[] {new InternetAddress(FROM)}, received.getFrom());
        }
    }

    @Test
    public void invalidMailSettingsStopEverySendBeforeAnyMessageIsBuilt() {
        when(mailUtils.getSmtpSettingProblems()).thenReturn(List.of("mail.smtp.from is blank"));
        MailConfigurationException batch =
                assertThrows(MailConfigurationException.class, () -> service.sendMessage(MESSAGE));
        assertEquals("Mail is not configured: mail.smtp.from is blank.", batch.getMessage());
        assertThrows(MailConfigurationException.class, () -> service.sendTestMessage("admin@example.com", MESSAGE));
        // No recipients were looked up, so no message was built or sent.
        verifyNoInteractions(apiUserService, adminUserService);
    }

    @Test
    public void nonAsciiTextAndAmpersandsArriveIntactInBothParts() throws Exception {
        String text = "“Quoted” café & co.";
        var message = new ApiUserMessage(ApiUserSubscriptionType.BREAKING_CHANGES, text, text);
        MimeMessage received = receive(service.getMimeMessage(message, "user@example.com", user));

        assertEquals(text, received.getSubject());
        Multipart parts = parts(received);
        for (int i = 0; i < parts.getCount(); i++) {
            assertTrue(parts.getBodyPart(i).getContentType(),
                    parts.getBodyPart(i).getContentType().contains("charset=UTF-8"));
        }
        String plain = content(parts, 0);
        assertTrue(plain, plain.startsWith(text + "\n\n"));
        assertFalse(plain, plain.contains("&amp;"));
        String html = content(parts, 1);
        assertTrue(html, html.contains("“Quoted” café &amp; co."));
    }

    @Test
    public void paragraphsAndLineBreaksAreKeptAndHtmlIsShownLiterally() throws Exception {
        String body = "First line\r\nsecond line\r\n\r\n\r\nNext <b>x</b>\n  \nLast";
        var message = new ApiUserMessage(ApiUserSubscriptionType.BREAKING_CHANGES, "Subject", body);
        Multipart parts = parts(receive(service.getMimeMessage(message, "user@example.com", null)));

        String plain = content(parts, 0);
        assertTrue(plain, plain.startsWith("First line\nsecond line\n\n\nNext <b>x</b>\n  \nLast\n\n"));
        String html = content(parts, 1);
        assertTrue(html, html.contains(">First line<br>\nsecond line</p>"));
        assertTrue(html, html.contains(">Next &lt;b&gt;x&lt;/b&gt;</p>"));
        assertTrue(html, html.contains(">Last</p>"));
        assertFalse(html, html.contains("<b>"));
        assertEquals(html, 3, occurrences(html, "<p style=\"margin:0 0 1em 0;\">"));
    }

    @Test
    public void urlsBecomeLinksWithoutTrailingPunctuation() {
        String html = service.renderHtml(
                "See https://example.org/a. Or (https://example.org/b), and https://example.org/c?x=1&y=2", null);
        assertTrue(html, html.contains("See <a href=\"https://example.org/a\">https://example.org/a</a>. "));
        assertTrue(html, html.contains("(<a href=\"https://example.org/b\">https://example.org/b</a>), "));
        assertTrue(html, html.contains(
                "<a href=\"https://example.org/c?x=1&amp;y=2\">https://example.org/c?x=1&amp;y=2</a>"));
    }

    @Test
    public void closingParenthesisIsKeptWhenTheUrlOpenedIt() {
        String html = service.renderHtml("See https://en.wikipedia.org/wiki/Bill_(law). Or "
                + "(https://en.wikipedia.org/wiki/Bill_(law)).", null);
        String link = "<a href=\"https://en.wikipedia.org/wiki/Bill_(law)\">https://en.wikipedia.org/wiki/Bill_(law)</a>";
        assertTrue(html, html.contains("See " + link + ". "));
        assertTrue(html, html.contains("(" + link + ")."));
    }

    @Test
    public void userCopyEndsWithSignatureThenOnePreferencesLink() throws Exception {
        Multipart parts = parts(receive(service.getMimeMessage(MESSAGE, user.getEmail(), user)));
        String text = content(parts, 0);
        String html = content(parts, 1);
        for (String part : new String[] {text, html}) {
            assertEquals(part, 1, occurrences(part, "/subscriptions"));
            assertEquals(part, 1, occurrences(part, PREFERENCES_URL));
            assertTrue(part, part.contains("manage your subscriptions or unsubscribe"));
            assertTrue(part, part.indexOf(SIGNATURE) < part.indexOf(PREFERENCES_URL));
            assertFalse(part, part.contains("unsub="));
        }
        assertTrue(text, text.strip().endsWith("visit: " + PREFERENCES_URL));
        assertTrue(html, html.contains("href=\"" + PREFERENCES_URL + "\""));
        assertTrue(html, html.indexOf("</table>") < html.indexOf(PREFERENCES_URL));
        assertTrue(html, html.substring(html.indexOf(PREFERENCES_URL))
                .matches("(?s)[^<]*\">visit your subscription preferences</a>.</p>\\s*</body></html>\\s*"));
    }

    @Test
    public void adminAndTestCopiesHaveTheSignatureButNoSubscriptionLinks() throws Exception {
        Multipart parts = parts(receive(service.getMimeMessage(MESSAGE, "admin@example.com", null)));
        for (int i = 0; i < parts.getCount(); i++) {
            String part = content(parts, i);
            assertTrue(part, part.contains(SIGNATURE));
            assertTrue(part, part.contains("Open Legislation"));
            assertFalse(part, part.contains("/subscriptions"));
            assertFalse(part, part.contains("unsub"));
        }
    }

    @Test
    public void previewIsTheSubscriberHtmlWithAPlaceholderKey() {
        String preview = service.renderPreviewHtml("Hello & welcome");
        assertEquals(service.renderHtml("Hello & welcome",
                "https://legislation.example.org/subscriptions?key=YOUR_API_KEY"), preview);
        assertTrue(preview, preview.contains("Hello &amp; welcome"));
    }

    /** Serializes and re-parses the message, as a recipient would receive it. */
    private MimeMessage receive(MimeMessage message) throws Exception {
        message.saveChanges();
        var out = new ByteArrayOutputStream();
        message.writeTo(out);
        return new MimeMessage(session, new ByteArrayInputStream(out.toByteArray()));
    }

    private static Multipart parts(MimeMessage received) throws Exception {
        Multipart parts = (Multipart) received.getContent();
        assertEquals(2, parts.getCount());
        assertTrue(parts.getBodyPart(0).isMimeType("text/plain"));
        assertTrue(parts.getBodyPart(1).isMimeType("text/html"));
        return parts;
    }

    private static String content(Multipart parts, int index) throws Exception {
        // Line breaks travel as CRLF.
        return ((String) parts.getBodyPart(index).getContent()).replace("\r\n", "\n");
    }

    private static int occurrences(String text, String target) {
        return text.split(java.util.regex.Pattern.quote(target), -1).length - 1;
    }
}
