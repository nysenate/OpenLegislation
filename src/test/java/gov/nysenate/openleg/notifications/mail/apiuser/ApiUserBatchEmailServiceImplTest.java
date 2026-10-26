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
import gov.nysenate.openleg.notifications.mail.MailConfigurationException;
import gov.nysenate.openleg.notifications.mail.MimeSendMailService;
import gov.nysenate.openleg.notifications.mail.SendResult;
import gov.nysenate.openleg.notifications.mail.SendResult.Outcome;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.UnsentRecipient;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import org.eclipse.angus.mail.smtp.SMTPAddressFailedException;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.mockito.ArgumentCaptor;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.SendFailedException;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.FAILED;
import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.NOT_SENT;
import static gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status.SKIPPED;
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
    private ScriptedSender sender;

    @Before
    public void setUp() {
        session = Session.getInstance(new Properties());
        mailUtils = mock(MailUtils.class);
        when(mailUtils.getSmtpSession()).thenReturn(session);
        env = mock(OpenLegEnvironment.class);
        when(env.getUrl()).thenReturn("https://legislation.example.org");
        when(env.getEmailFromAddress()).thenReturn(FROM);
        apiUserService = mock(ApiUserService.class);
        adminUserService = mock(AdminUserService.class);
        eventBus = mock(EventBus.class);
        sender = new ScriptedSender(mailUtils, env);
        service = new ApiUserBatchEmailServiceImpl(mailUtils, env, sender, apiUserService, adminUserService,
                eventBus);
        user = new ApiUser("user@example.com");
        user.setApiKey(KEY);
    }

    @Test
    public void everyCopyIsFromTheConfiguredSender() throws Exception {
        for (ApiUser recipient : new ApiUser[] {user, null}) {
            MimeMessage received =
                    receive(service.getMimeMessage(MESSAGE, new InternetAddress("user@example.com"), recipient));
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
        MimeMessage received = receive(service.getMimeMessage(message, new InternetAddress("user@example.com"), user));

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
        Multipart parts =
                parts(receive(service.getMimeMessage(message, new InternetAddress("user@example.com"), null)));

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
        Multipart parts =
                parts(receive(service.getMimeMessage(MESSAGE, new InternetAddress(user.getEmail()), user)));
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
        Multipart parts =
                parts(receive(service.getMimeMessage(MESSAGE, new InternetAddress("admin@example.com"), null)));
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

        assertEquals(List.of("a@example.com", "c@example.com", "admin@example.com"), sender.offered);
        assertEquals(3, result.sent());
        assertEquals(List.of(new UnsentRecipient(null, SKIPPED, "Not a valid email address"),
                new UnsentRecipient("bad address@example.com", SKIPPED, "Not a valid email address")),
                result.unsent());
    }

    @Test
    public void rejectedRecipientsAreReportedAndTheRestAreSentOnce() throws Exception {
        subscribers("e@example.com", "d@example.com", "c@example.com", "b@example.com", "a@example.com");
        admins("admin@example.com");
        refuse("b@example.com", "d@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com", "d@example.com", "e@example.com",
                "admin@example.com"), sender.offered);
        assertEquals(4, result.sent());
        String reason = "Invalid Addresses: 451 4.3.0 Mailbox temporarily unavailable";
        assertEquals(List.of(new UnsentRecipient("b@example.com", FAILED, reason),
                new UnsentRecipient("d@example.com", FAILED, reason)), result.unsent());
    }

    @Test
    public void copyTheServerNeverConfirmedIsReportedAsFailedWithTheReason() {
        subscribers("a@example.com", "b@example.com");
        timeOut("a@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        // The reason, unlike a refusal's, tells the admin it may still have been delivered.
        assertEquals(List.of(new UnsentRecipient("a@example.com", FAILED,
                "Exception reading response: Read timed out")), result.unsent());
        assertEquals(List.of("a@example.com", "b@example.com"), sender.offered);
        assertEquals(1, result.sent());
    }

    @Test
    public void unreachableServerReportsEveryRecipientAsNotSent() {
        subscribers("a@example.com", "b@example.com", "c@example.com");
        admins("admin@example.com");
        refuseConnections("a@example.com", "b@example.com", "c@example.com", "admin@example.com");

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
    public void refusedRecipientsDoNotRaiseANotification() throws Exception {
        subscribers("a@example.com", "b@example.com");
        refuse("a@example.com");

        service.sendMessage(MESSAGE);

        verify(eventBus, never()).post(any());
    }

    @Test
    public void rejectedCredentialsStopTheBatch() {
        subscribers("a@example.com");
        admins("admin@example.com");
        sender.answer(Outcome.LOGIN_REJECTED, new AuthenticationFailedException("535 Authentication failed"),
                "a@example.com", "admin@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(0, result.sent());
        assertEquals(List.of("a@example.com", "admin@example.com"), emails(result.unsent(NOT_SENT)));
        assertEquals("535 Authentication failed", result.unsent().get(0).reason());
        assertEquals("No further login is tried", List.of("a@example.com"), sender.offered);
    }

    @Test
    public void serverThatStopsReplyingStopsTheBatch() {
        subscribers("a@example.com", "b@example.com", "c@example.com", "d@example.com");
        admins("admin@example.com");
        timeOut("a@example.com", "b@example.com", "c@example.com", "d@example.com", "admin@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com"), emails(result.unsent(FAILED)));
        assertEquals(List.of("d@example.com", "admin@example.com"), emails(result.unsent(NOT_SENT)));
        assertEquals("Stopped after 3 copies in a row failed at the mail server: "
                + "Exception reading response: Read timed out", result.unsent(NOT_SENT).get(0).reason());
        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com"), sender.offered);
    }

    @Test
    public void replyBetweenUnansweredCopiesKeepsTheBatchGoing() throws Exception {
        subscribers("a@example.com", "b@example.com", "c@example.com", "d@example.com", "e@example.com");
        timeOut("a@example.com", "b@example.com", "d@example.com", "e@example.com");
        refuse("c@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com", "d@example.com", "e@example.com"),
                emails(result.unsent(FAILED)));
        assertEquals(List.of(), result.unsent(NOT_SENT));
    }

    @Test
    public void serverLostMidBatchLeavesTheRestNotSent() {
        subscribers("a@example.com", "b@example.com", "c@example.com", "d@example.com", "e@example.com");
        admins("admin@example.com");
        refuseConnections("c@example.com", "d@example.com", "e@example.com", "admin@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com", "d@example.com", "e@example.com"),
                sender.offered);
        assertEquals(2, result.sent());
        assertEquals(List.of("c@example.com", "d@example.com", "e@example.com", "admin@example.com"),
                emails(result.unsent(NOT_SENT)));
        assertEquals("Stopped after 3 copies in a row failed at the mail server: "
                + "Couldn't connect to host: Connection refused", result.unsent().get(3).reason());
    }

    @Test(timeout = 5_000)
    public void causeChainThatLoopsIsDescribedOnce() {
        // MessagingException.getCause() returns the next exception, which may be the exception itself.
        var looped = new MessagingException("Exception reading response");
        looped.setNextException(looped);
        sender.answer(Outcome.SERVER_FAILED, looped, "admin@example.com");

        BatchEmailResult result = service.sendTestMessage("admin@example.com", MESSAGE);

        assertEquals(List.of(new UnsentRecipient("admin@example.com", FAILED, "Exception reading response")),
                result.unsent());
    }

    @Test
    public void serverRefusingEverySenderStopsTheBatchAndRaisesANotification() {
        subscribers("a@example.com", "b@example.com", "c@example.com", "d@example.com");
        admins("admin@example.com");
        sender.answer(Outcome.SERVER_FAILED, new SMTPSendFailedException("MAIL FROM:<openleg@example.org>", 530,
                        "530 5.7.0 Authentication required", null, null, null, null),
                "a@example.com", "b@example.com", "c@example.com", "d@example.com", "admin@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com"), emails(result.unsent(FAILED)));
        assertEquals("530 5.7.0 Authentication required", result.unsent().get(0).reason());
        assertEquals(List.of("d@example.com", "admin@example.com"), emails(result.unsent(NOT_SENT)));
        assertEquals(List.of("a@example.com", "b@example.com", "c@example.com"), sender.offered);
        verify(eventBus).post(any(Notification.class));
    }

    @Test
    public void oneFailedConnectionMidBatchDoesNotStopTheRest() {
        subscribers("a@example.com", "b@example.com", "c@example.com");
        refuseConnections("b@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(2, result.sent());
        assertEquals(List.of(new UnsentRecipient("b@example.com", NOT_SENT,
                "Couldn't connect to host: Connection refused")), result.unsent());
    }

    @Test
    public void firstFailedConnectionDoesNotStopTheBatch() {
        subscribers("a@example.com", "b@example.com", "c@example.com");
        refuseConnections("a@example.com");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(2, result.sent());
        assertEquals(List.of(new UnsentRecipient("a@example.com", NOT_SENT,
                "Couldn't connect to host: Connection refused")), result.unsent());
    }

    @Test
    public void testEmailToAnAddressThatCantBeParsedIsSkippedWithTheReason() {
        BatchEmailResult result = service.sendTestMessage("not an address", MESSAGE);

        assertEquals(0, result.sent());
        assertEquals(List.of(new UnsentRecipient("not an address", SKIPPED,
                "Local address contains control or whitespace")), result.unsent());
        assertEquals(List.of(), sender.offered);
    }

    @Test
    public void copyThatCantBeBuiltIsReportedAsFailedNotAsAnUnusableAddress() {
        subscribers("a@example.com");
        // The mail settings are checked before sending, so an unusable sender only stands in for a copy that
        // can't be built.
        when(env.getEmailFromAddress()).thenReturn("not an address");

        BatchEmailResult result = service.sendMessage(MESSAGE);

        assertEquals(List.of("a@example.com"), emails(result.unsent(FAILED)));
        assertEquals(List.of(), sender.offered);
    }

    @Test
    public void testEmailIsSentToALocalAdminAddress() {
        BatchEmailResult result = service.sendTestMessage("admin@localhost", MESSAGE);

        assertEquals(1, result.sent());
        assertEquals(List.of("admin@localhost"), sender.offered);
        assertNull("A test email isn't a batch", service.getStatus());
    }

    @Test
    public void batchStartedInTheBackgroundIsSentAloneAndReportsItsProgress() throws Exception {
        List<Runnable> tasks = new ArrayList<>();
        var background = new ApiUserBatchEmailServiceImpl(mailUtils, env, sender, apiUserService, adminUserService,
                eventBus, tasks::add);
        subscribers("a@example.com", "b@example.com");
        refuse("b@example.com");

        BatchEmailStatus started = background.startMessage(MESSAGE);

        assertTrue(started.running());
        assertEquals("Subject", started.subject());
        assertEquals(2, started.recipients());
        assertEquals(new BatchEmailResult(0, List.of()), started.result());
        assertThrows(BatchEmailInProgressException.class, () -> background.startMessage(MESSAGE));
        assertThrows(BatchEmailInProgressException.class, () -> background.sendMessage(MESSAGE));
        assertEquals(List.of(), sender.offered);

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

        assertEquals(List.of(), sender.offered);
        assertEquals(List.of("a@example.com", "b@example.com"), emails(result.unsent(NOT_SENT)));
        assertEquals("Stopped because the server is shutting down", result.unsent().get(0).reason());
        assertFalse(service.getStatus().running());
    }

    @Test
    public void testMessageFailureGivesTheReason() throws Exception {
        refuse("admin@example.com");

        BatchEmailResult result = service.sendTestMessage("admin@example.com", MESSAGE);

        assertEquals(0, result.sent());
        assertEquals(List.of(new UnsentRecipient("admin@example.com", FAILED,
                "Invalid Addresses: 451 4.3.0 Mailbox temporarily unavailable")), result.unsent());
        assertEquals(1, service.sendTestMessage("other@example.com", MESSAGE).sent());
    }

    /** The mail server refuses copies to these recipients, as it would an unknown mailbox. */
    private void refuse(String... recipients) throws AddressException {
        for (String recipient : recipients) {
            sender.answer(Outcome.REFUSED, new SendFailedException("Invalid Addresses",
                    new SMTPAddressFailedException(new InternetAddress(recipient), "RCPT TO:<" + recipient + ">", 451,
                            "451 4.3.0 Mailbox temporarily unavailable")), recipient);
        }
    }

    /** The mail server doesn't reply in time to copies to these recipients. */
    private void timeOut(String... recipients) {
        sender.answer(Outcome.SERVER_FAILED, new MessagingException("Exception reading response",
                new SocketTimeoutException("Read timed out")), recipients);
    }

    /** Connections for copies to these recipients are refused. */
    private void refuseConnections(String... recipients) {
        sender.answer(Outcome.NOT_CONNECTED, new MessagingException("Couldn't connect to host",
                new ConnectException("Connection refused")), recipients);
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

    /**
     * Builds messages as the real sender does but sends none: each copy gets the answer set for its recipient,
     * or is accepted. How the real sender answers is tested against an SMTP server in MimeSendMailServiceSmtpTest.
     */
    private static class ScriptedSender extends MimeSendMailService {
        private final Map<String, SendResult> answers = new HashMap<>();
        /** The recipient of each copy offered to the mail server, in order. */
        final List<String> offered = new ArrayList<>();

        ScriptedSender(MailUtils mailUtils, OpenLegEnvironment env) {
            super(mailUtils, env);
        }

        void answer(Outcome outcome, Exception cause, String... recipients) {
            for (String recipient : recipients) {
                answers.put(recipient, new SendResult(outcome, cause));
            }
        }

        @Override
        public SendResult trySend(MimeMessage message) {
            try {
                String recipient = message.getAllRecipients()[0].toString();
                offered.add(recipient);
                return answers.getOrDefault(recipient, SendResult.SENT);
            } catch (MessagingException ex) {
                throw new AssertionError(ex);
            }
        }
    }
}
