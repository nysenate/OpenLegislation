package gov.nysenate.openleg.notifications.mail.apiuser;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.auth.admin.AdminUser;
import gov.nysenate.openleg.auth.admin.AdminUserService;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.notifications.mail.FakeTransport;
import gov.nysenate.openleg.notifications.mail.MailConfigurationException;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.UnsentRecipient;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import org.mockito.ArgumentCaptor;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.FAILED;
import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.NOT_SENT;
import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.SKIPPED;
import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.UNCONFIRMED;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
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
    private OpenLegEnvironment env;
    private ApiUser user;
    private EventBus eventBus;

    @Before
    public void setUp() throws Exception {
        session = Session.getInstance(new Properties());
        FakeTransport.install(session);
        FakeTransport.reset();
        mailUtils = mock(MailUtils.class);
        when(mailUtils.getSmtpSession()).thenReturn(session);
        env = mock(OpenLegEnvironment.class);
        when(env.getUrl()).thenReturn("https://legislation.example.org");
        when(env.getEmailFromAddress()).thenReturn(FROM);
        apiUserService = mock(ApiUserService.class);
        adminUserService = mock(AdminUserService.class);
        eventBus = mock(EventBus.class);
        service = new ApiUserBatchEmailServiceImpl(mailUtils, env, apiUserService, adminUserService, eventBus);
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

    @Test
    public void unusableAddressesAreSkippedAndEveryoneElseGetsACopy() {
        subscribers("a@example.com", "bad address@example.com", null, "c@example.com");
        admins("admin@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "c@example.com", "admin@example.com"), FakeTransport.delivered);
        assertEquals(3, result.sent());
        assertEquals(List.of(new UnsentRecipient(null, SKIPPED, "Not a valid email address"),
                new UnsentRecipient("bad address@example.com", SKIPPED, "Not a valid email address")),
                result.unsent());
    }

    @Test
    public void rejectedRecipientsAreReportedAndTheRestAreSentOnce() {
        subscribers("e@example.com", "d@example.com", "c@example.com", "b@example.com", "a@example.com");
        admins("admin@example.com");
        FakeTransport.rejected.addAll(List.of("b@example.com", "d@example.com"));

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "c@example.com", "e@example.com", "admin@example.com"),
                FakeTransport.delivered);
        assertEquals(4, result.sent());
        String reason = "Invalid Addresses: 451 4.3.0 Mailbox temporarily unavailable";
        assertEquals(List.of(new UnsentRecipient("b@example.com", FAILED, reason),
                new UnsentRecipient("d@example.com", FAILED, reason)), result.unsent());
        assertEquals("Each copy has its own connection", 6, FakeTransport.connections);
    }

    @Test
    public void unacknowledgedCopyIsNotReportedAsRejected() {
        subscribers("a@example.com", "b@example.com");
        FakeTransport.unacknowledged.add("a@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        // The server may have delivered it, so the admin must not be told it was rejected.
        assertEquals(List.of(new UnsentRecipient("a@example.com", UNCONFIRMED,
                "Exception reading response: Read timed out")), result.unsent());
        assertEquals(List.of("a@example.com", "b@example.com"), FakeTransport.delivered);
        assertEquals(1, result.sent());
    }

    @Test
    public void unreachableServerReportsEveryRecipientAsNotSent() {
        subscribers("a@example.com", "b@example.com", "c@example.com");
        admins("admin@example.com");
        FakeTransport.connectionsAllowed = 0;

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(0, result.sent());
        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com", "admin@example.com"),
                emails(result.unsent(NOT_SENT)));
        String refused = "Couldn't connect to host: Connection refused";
        assertEquals(List.of(refused, refused, refused,
                "Stopped after 3 copies in a row failed at the mail server: " + refused), reasons(result.unsent()));
        var notification = ArgumentCaptor.forClass(Notification.class);
        verify(eventBus).post(notification.capture());
        assertEquals(NotificationType.REQUEST_EXCEPTION, notification.getValue().getNotificationType());
        assertEquals("Batch email \"Subject\" not sent to 4 of 4 recipients", notification.getValue().getSummary());
    }

    @Test
    public void refusedRecipientsDoNotRaiseANotification() {
        subscribers("a@example.com", "b@example.com");
        FakeTransport.rejected.add("a@example.com");

        service.sendMessage(MESSAGE);

        verify(eventBus, never()).post(any());
    }

    @Test
    public void rejectedCredentialsStopTheBatch() {
        subscribers("a@example.com");
        admins("admin@example.com");
        FakeTransport.rejectCredentials = true;

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(0, result.sent());
        assertEquals(List.of("a@example.com", "admin@example.com"), emails(result.unsent(NOT_SENT)));
        assertEquals("535 Authentication failed", result.unsent().get(0).reason());
    }

    @Test
    public void serverLimitOnMessagesPerConnectionDoesNotRefuseTheRest() {
        subscribers("a@example.com", "b@example.com", "c@example.com");
        FakeTransport.messagesPerConnection = 1;

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com"), FakeTransport.delivered);
        assertEquals(3, result.sent());
        assertEquals(List.of(), result.unsent());
    }

    @Test
    public void serverThatStopsReplyingStopsTheBatch() {
        subscribers("a@example.com", "b@example.com", "c@example.com", "d@example.com");
        admins("admin@example.com");
        FakeTransport.unacknowledged.addAll(List.of("a@example.com", "b@example.com", "c@example.com",
                "d@example.com", "admin@example.com"));

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com"), emails(result.unsent(UNCONFIRMED)));
        assertEquals(List.of("d@example.com", "admin@example.com"), emails(result.unsent(NOT_SENT)));
        assertEquals("Stopped after 3 copies in a row failed at the mail server: "
                + "Exception reading response: Read timed out", result.unsent(NOT_SENT).get(0).reason());
        assertEquals(3, FakeTransport.connections);
    }

    @Test
    public void replyBetweenUnansweredCopiesKeepsTheBatchGoing() {
        subscribers("a@example.com", "b@example.com", "c@example.com", "d@example.com", "e@example.com");
        FakeTransport.unacknowledged.addAll(List.of("a@example.com", "b@example.com", "d@example.com",
                "e@example.com"));
        FakeTransport.rejected.add("c@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "b@example.com", "d@example.com", "e@example.com"),
                emails(result.unsent(UNCONFIRMED)));
        assertEquals(List.of("c@example.com"), emails(result.unsent(FAILED)));
        assertEquals(List.of(), result.unsent(NOT_SENT));
    }

    @Test
    public void serverLostMidBatchLeavesTheRestNotSent() {
        subscribers("a@example.com", "b@example.com", "c@example.com", "d@example.com", "e@example.com");
        admins("admin@example.com");
        FakeTransport.connectionsAllowed = 2;

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "b@example.com"), FakeTransport.delivered);
        assertEquals(2, result.sent());
        assertEquals(List.of("c@example.com", "d@example.com", "e@example.com", "admin@example.com"),
                emails(result.unsent(NOT_SENT)));
        assertEquals("Stopped after 3 copies in a row failed at the mail server: "
                + "Couldn't connect to host: Connection refused", result.unsent().get(3).reason());
    }

    @Test(timeout = 5_000)
    public void causeChainThatLoopsIsDescribedOnce() {
        // MessagingException.getCause() returns the next exception, which may be the exception itself.
        var looped = new jakarta.mail.MessagingException("Exception reading response");
        looped.setNextException(looped);
        FakeTransport.thrown.put("admin@example.com", looped);

        BatchEmailResult result = service.sendTestMessage("admin@example.com", MESSAGE);

        assertEquals(List.of(new UnsentRecipient("admin@example.com", FAILED, "Exception reading response")),
                result.unsent());
    }

    @Test
    public void connectionClosedAfterTheWholeMessageIsUnconfirmed() {
        subscribers("a@example.com", "b@example.com");
        FakeTransport.closedAfterData.add("a@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        // The server may have queued it before closing, so resending could duplicate it.
        assertEquals(List.of(new UnsentRecipient("a@example.com", UNCONFIRMED, "[EOF]")), result.unsent());
        assertEquals(1, result.sent());
    }

    @Test
    public void noReplyBeforeTheMessageWasSentIsNotDelivered() {
        subscribers("a@example.com", "b@example.com");
        FakeTransport.thrown.put("a@example.com", new jakarta.mail.MessagingException("Exception reading response",
                new java.net.SocketTimeoutException("Read timed out")));

        BatchEmailResult result = service.sendMessage(MESSAGE);

        // Such as a timeout waiting for the RCPT TO reply: the server can't have the message.
        assertEquals(List.of(new UnsentRecipient("a@example.com", FAILED,
                "Exception reading response: Read timed out")), result.unsent());
        assertEquals(1, result.sent());
    }

    @Test
    public void serverRefusingEverySenderStopsTheBatchAndRaisesANotification() {
        subscribers("a@example.com", "b@example.com", "c@example.com", "d@example.com");
        admins("admin@example.com");
        FakeTransport.refuseSender = true;

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com"), emails(result.unsent(FAILED)));
        assertEquals("530 5.7.0 Authentication required", result.unsent().get(0).reason());
        assertEquals(List.of("d@example.com", "admin@example.com"), emails(result.unsent(NOT_SENT)));
        assertEquals(3, FakeTransport.connections);
        verify(eventBus).post(any(Notification.class));
    }

    @Test
    public void oneFailedConnectionMidBatchDoesNotStopTheRest() {
        subscribers("a@example.com", "b@example.com", "c@example.com");
        FakeTransport.failedConnectionAttempts.add(2);

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "c@example.com"), FakeTransport.delivered);
        assertEquals(List.of(new UnsentRecipient("b@example.com", NOT_SENT,
                "Couldn't connect to host: Connection refused")), result.unsent());
    }

    @Test
    public void firstFailedConnectionDoesNotStopTheBatch() {
        subscribers("a@example.com", "b@example.com", "c@example.com");
        FakeTransport.failedConnectionAttempts.add(1);

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("b@example.com", "c@example.com"), FakeTransport.delivered);
        assertEquals(List.of(new UnsentRecipient("a@example.com", NOT_SENT,
                "Couldn't connect to host: Connection refused")), result.unsent());
    }

    @Test
    public void testEmailIsSentToALocalAdminAddress() {
        BatchEmailResult result = service.sendTestMessage("admin@localhost", MESSAGE);

        assertEquals(1, result.sent());
        assertEquals(List.of("admin@localhost"), FakeTransport.delivered);
        assertNull("A test email isn't a batch", service.getStatus());
    }

    @Test
    public void batchStartedInTheBackgroundIsSentAloneAndReportsItsProgress() {
        List<Runnable> tasks = new ArrayList<>();
        var background = new ApiUserBatchEmailServiceImpl(mailUtils, env, apiUserService, adminUserService, eventBus,
                tasks::add);
        subscribers("a@example.com", "b@example.com");
        FakeTransport.rejected.add("b@example.com");

        BatchEmailStatus started = background.startMessage(MESSAGE);

        assertTrue(started.running());
        assertEquals("Subject", started.subject());
        assertEquals(2, started.recipients());
        assertEquals(new BatchEmailResult(0, List.of()), started.result());
        assertThrows(BatchEmailInProgressException.class, () -> background.startMessage(MESSAGE));
        assertThrows(BatchEmailInProgressException.class, () -> background.sendMessage(MESSAGE));
        assertEquals(List.of(), FakeTransport.delivered);

        tasks.forEach(Runnable::run);

        BatchEmailStatus finished = background.getStatus();
        assertFalse(finished.running());
        assertEquals(1, finished.result().sent());
        assertEquals(List.of("b@example.com"), emails(finished.result().unsent(FAILED)));
        background.startMessage(MESSAGE);
        assertEquals("Another batch can start once it has finished", 2, tasks.size());
    }

    @Test
    public void interruptedBatchReportsTheRestAsNotSent() {
        subscribers("a@example.com", "b@example.com");

        BatchEmailResult result;
        // As when the server shuts down.
        Thread.currentThread().interrupt();
        try {
            result = service.sendMessage(MESSAGE);
        } finally {
            Thread.interrupted();
        }

        assertEquals(List.of(), FakeTransport.delivered);
        assertEquals(List.of("a@example.com", "b@example.com"), emails(result.unsent(NOT_SENT)));
        assertEquals("Stopped because the server is shutting down", result.unsent().get(0).reason());
        assertFalse(service.getStatus().running());
    }

    @Test
    public void testMessageFailureGivesTheReason() {
        FakeTransport.rejected.add("admin@example.com");

        BatchEmailResult result = service.sendTestMessage("admin@example.com", MESSAGE);

        assertEquals(0, result.sent());
        assertEquals(List.of(new UnsentRecipient("admin@example.com", FAILED,
                "Invalid Addresses: 451 4.3.0 Mailbox temporarily unavailable")), result.unsent());
        assertEquals(1, service.sendTestMessage("other@example.com", MESSAGE).sent());
    }

    @Test(timeout = 10_000)
    public void realSmtpServerThatRefusesConnectionsFinishesPromptly() throws Exception {
        int port;
        try (var socket = new java.net.ServerSocket(0, 1, java.net.InetAddress.getLoopbackAddress())) {
            port = socket.getLocalPort();
        }
        var properties = new Properties();
        properties.put("mail.smtp.host", "127.0.0.1");
        properties.put("mail.smtp.port", String.valueOf(port));
        // Bounded even if another process takes the freed port and never answers.
        properties.put("mail.smtp.connectiontimeout", "2000");
        properties.put("mail.smtp.timeout", "2000");
        when(mailUtils.getSmtpSession()).thenReturn(Session.getInstance(properties));
        var unreachable = new ApiUserBatchEmailServiceImpl(mailUtils, env, apiUserService, adminUserService, eventBus);
        subscribers("a@example.com", "b@example.com");

        long start = System.nanoTime();
        BatchEmailResult result = unreachable.sendMessage(MESSAGE);

        assertTrue(java.time.Duration.ofNanos(System.nanoTime() - start).toSeconds() < 5);
        assertEquals(0, result.sent());
        assertEquals(List.of("a@example.com", "b@example.com"), emails(result.unsent(NOT_SENT)));
        String reason = result.unsent().get(0).reason();
        assertTrue(reason, reason.startsWith("Couldn't connect to host, port: 127.0.0.1, " + port));
    }

    private void subscribers(String... emails) {
        List<ApiUser> users = new ArrayList<>();
        for (String email : emails) {
            ApiUser subscriber = new ApiUser(email);
            subscriber.setApiKey(KEY + users.size());
            users.add(subscriber);
        }
        when(apiUserService.getUsersWithSubscription(ApiUserSubscriptionType.BREAKING_CHANGES)).thenReturn(users);
    }

    private void admins(String... usernames) {
        List<AdminUser> admins = new ArrayList<>();
        for (String username : usernames) {
            admins.add(new AdminUser(username, "password", true, false));
        }
        when(adminUserService.getAdminUsers()).thenReturn(admins);
    }

    private static List<String> emails(List<UnsentRecipient> recipients) {
        return recipients.stream().map(UnsentRecipient::email).toList();
    }

    private static List<String> reasons(List<UnsentRecipient> recipients) {
        return recipients.stream().map(UnsentRecipient::reason).toList();
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
