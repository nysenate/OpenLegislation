package gov.nysenate.openleg.notifications.mail.apiuser;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.auth.admin.AdminUserService;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.common.util.OpenlegThreadFactory;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.notifications.mail.MailConfigurationException;
import gov.nysenate.openleg.notifications.mail.MailException;
import gov.nysenate.openleg.notifications.mail.MimeSendMailService;
import gov.nysenate.openleg.notifications.mail.SendResult;
import gov.nysenate.openleg.notifications.mail.SendResult.Outcome;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.Status;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult.UnsentRecipient;
import gov.nysenate.openleg.notifications.model.Notification;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import jakarta.annotation.Nullable;
import jakarta.annotation.PreDestroy;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.notifications.model.NotificationType.REQUEST_EXCEPTION;

@Service
public class ApiUserBatchEmailServiceImpl implements ApiUserBatchEmailService {

    private final MailUtils mailUtils;
    private final MimeSendMailService mimeSender;
    private final OpenLegEnvironment env;
    private final ApiUserService apiUserService;
    private final AdminUserService adminUserService;
    private final EventBus eventBus;
    private final Executor executor;
    private final EmailValidator validator = EmailValidator.getInstance();

    /** The batch email being sent, or the last one sent since startup; null before the first. */
    private volatile Batch current;

    @Autowired
    public ApiUserBatchEmailServiceImpl(MailUtils mailUtils, OpenLegEnvironment env,
                                        ApiUserService apiUserService,
                                        AdminUserService adminUserService,
                                        @Qualifier("eventBus") EventBus eventBus) {
        // A thread of its own, so a batch never waits behind other background work.
        this(mailUtils, env, apiUserService, adminUserService, eventBus,
                Executors.newSingleThreadExecutor(new OpenlegThreadFactory("batch-email")));
    }

    /**
     * @param executor runs the batch emails sent in the background
     */
    ApiUserBatchEmailServiceImpl(MailUtils mailUtils, OpenLegEnvironment env, ApiUserService apiUserService,
                                 AdminUserService adminUserService, EventBus eventBus, Executor executor) {
        this.mailUtils = mailUtils;
        this.mimeSender = new MimeSendMailService(mailUtils, env);
        this.apiUserService = apiUserService;
        this.env = env;
        this.adminUserService = adminUserService;
        this.eventBus = eventBus;
        this.executor = executor;
    }

    /**
     * Interrupts a batch being sent, which then stops after the copy in progress and reports the rest as not sent.
     */
    @PreDestroy
    public void destroy() {
        if (executor instanceof ExecutorService executorService) {
            executorService.shutdownNow();
        }
    }

    public BatchEmailResult sendTestMessage(String email, ApiUserMessage message) throws MailException {
        checkMailSettings();
        // The admin's own address isn't held to the stored-address check, which rejects local ones like admin@localhost.
        var batch = new Batch(message, List.of(new Recipient(email, null)));
        send(batch, false);
        return batch.status().result();
    }

    public BatchEmailResult sendMessage(ApiUserMessage message) throws MailException {
        Batch batch = begin(message);
        send(batch, true);
        return batch.status().result();
    }

    public BatchEmailStatus startMessage(ApiUserMessage message) throws MailException {
        Batch batch = begin(message);
        executor.execute(() -> {
            try {
                send(batch, true);
            } catch (RuntimeException ex) {
                logger.error("Batch email \"{}\" stopped unexpectedly", message.getSubject(), ex);
            }
        });
        return batch.status();
    }

    @Nullable
    public BatchEmailStatus getStatus() {
        Batch batch = current;
        return batch == null ? null : batch.status();
    }

    /**
     * Gathers the recipients and makes the message the current batch, unless another is still being sent.
     */
    private synchronized Batch begin(ApiUserMessage message) {
        checkMailSettings();
        Batch previous = current;
        if (previous != null && previous.running()) {
            throw new BatchEmailInProgressException(previous.message.getSubject());
        }
        //get all the users who are subscribed to one or more of the subscriptions
        Set<ApiUserSubscriptionType> subscriptions = message.getSubscriptionTypes();
        Set<ApiUser> users = new HashSet<>();
        for (ApiUserSubscriptionType sub : subscriptions) {
            users.addAll(apiUserService.getUsersWithSubscription(sub));
        }

        // Sorted by address, so that reports are reproducible. All admins receive all batch emails.
        List<Recipient> recipients = new ArrayList<>();
        users.stream().map(user -> new Recipient(user.getEmail(), user))
                .sorted(BY_EMAIL).forEach(recipients::add);
        adminUserService.getAdminUsers().stream().map(admin -> new Recipient(admin.getUsername(), null))
                .sorted(BY_EMAIL).forEach(recipients::add);
        current = new Batch(message, recipients);
        logger.info("Batch email \"{}\" started for {} recipients", message.getSubject(), recipients.size());
        return current;
    }

    /**
     * Builds and sends each recipient's copy separately, recording each outcome in the batch as it happens.
     * An address that can't be used is skipped, and a copy the mail server refuses is recorded as failed; neither
     * affects the other recipients. If the mail server rejects the credentials, or fails several copies in a row for
     * reasons that aren't about their recipients, such as failed connections, the rest are not sent; nor are they
     * if the thread is interrupted, as when the server shuts down.
     * @param checkAddresses whether to skip addresses that fail {@link EmailValidator}; unparseable ones
     *                       are always skipped
     */
    private void send(Batch batch, boolean checkAddresses) {
        ApiUserMessage message = batch.message;
        int serverFailuresInARow = 0;
        String stopReason = null;
        try {
            for (Recipient recipient : batch.recipients) {
                String email = recipient.email();
                if (checkAddresses && !validator.isValid(email)) {
                    unsent(batch, email, Status.SKIPPED, "Not a valid email address");
                    continue;
                }
                MimeMessage mimeMessage;
                try {
                    mimeMessage = getMimeMessage(message, email, recipient.user());
                } catch (MailException ex) {
                    unsent(batch, email, Status.SKIPPED, describe(ex.getCause() == null ? ex : ex.getCause()));
                    continue;
                }
                if (stopReason == null && Thread.currentThread().isInterrupted()) {
                    stopReason = "Stopped because the server is shutting down";
                }
                if (stopReason != null) {
                    unsent(batch, email, Status.NOT_SENT, stopReason);
                    continue;
                }

                SendResult sendResult = mimeSender.trySend(mimeMessage);
                Outcome outcome = sendResult.outcome();
                Status status = switch (outcome) {
                    case SENT -> null;
                    case REFUSED, SERVER_FAILED -> Status.FAILED;
                    case UNCONFIRMED -> Status.UNCONFIRMED;
                    case NOT_CONNECTED, LOGIN_REJECTED -> Status.NOT_SENT;
                };
                if (status == null) {
                    batch.recordSent();
                } else {
                    unsent(batch, email, status, describe(sendResult.cause()));
                }

                if (outcome == Outcome.LOGIN_REJECTED) {
                    // Rejected credentials won't work for anyone, and every further try is another failed login.
                    stopReason = describe(sendResult.cause());
                } else if (outcome == Outcome.SENT || outcome == Outcome.REFUSED) {
                    serverFailuresInARow = 0;
                } else if (++serverFailuresInARow == MAX_SERVER_FAILURES_IN_A_ROW) {
                    // Such as a server that can't be reached, stopped answering or refuses every sender; the rest
                    // would fail the same way. A single failure, such as a reset connection, doesn't stop the batch.
                    stopReason = "Stopped after " + MAX_SERVER_FAILURES_IN_A_ROW
                            + " copies in a row failed at the mail server: " + describe(sendResult.cause());
                }
            }
        } finally {
            // Even if sending failed unexpectedly, so that another batch can be sent.
            batch.finish();
        }
        log(message, batch.status().result());
    }

    /** Records a recipient who may not have got a copy, and logs it at once in case the server stops mid-batch. */
    private static void unsent(Batch batch, String email, Status status, String reason) {
        batch.recordUnsent(new UnsentRecipient(email, status, reason));
        logger.warn("Batch email {} for {}: {}", status, email, reason);
    }

    private void log(ApiUserMessage message, BatchEmailResult result) {
        List<UnsentRecipient> notSent = result.unsent(Status.NOT_SENT);
        logger.info("Batch email \"{}\": {} sent, {} skipped, {} failed, {} unconfirmed, {} not sent",
                message.getSubject(), result.sent(), result.unsent(Status.SKIPPED).size(),
                result.unsent(Status.FAILED).size(), result.unsent(Status.UNCONFIRMED).size(), notSent.size());
        if (!notSent.isEmpty()) {
            // Such as a mail server that is down or misconfigured, which the page alone wouldn't bring to anyone's
            // attention.
            String summary = "Batch email \"" + message.getSubject() + "\" not sent to " + notSent.size()
                    + " of " + result.total() + " recipients";
            String reason = notSent.get(0).reason();
            logger.error("{}: {}", summary, reason);
            eventBus.post(new Notification(REQUEST_EXCEPTION, LocalDateTime.now(), summary, summary + ": " + reason));
        }
    }

    /**
     * Describes why a message couldn't be built or sent, from the most general to the most specific
     * cause, such as "Invalid Addresses: 451 Rejected" for a recipient the mail server refused.
     */
    private static String describe(Throwable ex) {
        List<String> reasons = new ArrayList<>();
        // The list stops at a loop, which MessagingException allows by being its own next exception.
        for (Throwable cause : ExceptionUtils.getThrowableList(ex)) {
            String reason = cause.getMessage() == null ? "" : cause.getMessage().strip();
            if (!reason.isEmpty() && (reasons.isEmpty() || !reasons.get(reasons.size() - 1).contains(reason))) {
                reasons.add(reason);
            }
        }
        return reasons.isEmpty() ? ex.getClass().getSimpleName() : String.join(": ", reasons);
    }

    /**
     * Fails before any message is built if the SMTP settings can't send a well-formed message,
     * so a batch is never sent partially or without a sender.
     */
    private void checkMailSettings() throws MailConfigurationException {
        List<String> problems = mailUtils.getSmtpSettingProblems();
        if (!problems.isEmpty()) {
            throw new MailConfigurationException(problems);
        }
    }

    /**
     * The subscription preferences page, where the user can change or remove all of their subscriptions.
     * Opening it never changes subscriptions, so link scanners cannot unsubscribe anyone.
     */
    private String getSubscriptionUrl(String apiKey) {
        return env.getUrl() + "/subscriptions?key=" + apiKey;
    }

    /**
     * Renders the HTML part a subscriber would receive for the given body, with a placeholder
     * in place of their API key, so admins can preview exactly what will be sent.
     * @param body String, plain text as typed by the admin
     * @return String, a complete HTML document
     */
    public String renderPreviewHtml(String body) {
        return renderHtml(body, getSubscriptionUrl(PREVIEW_API_KEY));
    }

    /**
     * Renders the plain-text body as an HTML document followed by the footer.
     * The text is escaped, blank lines separate paragraphs, single newlines become line breaks,
     * and http(s) URLs become links.
     * @param body String
     * @param subscriptionUrl String @Nullable, the recipient's preferences page; omitted for admin copies
     * @return String
     */
    protected String renderHtml(String body, @Nullable String subscriptionUrl) {
        StringBuilder html = new StringBuilder("<!DOCTYPE html>\n<html><head><meta charset=\"UTF-8\"></head>\n")
                .append("<body style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;line-height:1.4;\">\n");
        String text = normalize(body);
        if (!text.isEmpty()) {
            for (String paragraph : PARAGRAPH_BREAK.split(text)) {
                html.append("<p style=\"margin:0 0 1em 0;\">");
                String[] lines = paragraph.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    if (i > 0) {
                        html.append("<br>\n");
                    }
                    html.append(linkify(lines[i]));
                }
                html.append("</p>\n");
            }
        }
        html.append(SIGNATURE_HTML);
        if (subscriptionUrl != null) {
            html.append("<p style=\"font-size:11px;color:#555555;margin:8px 0 0 0;\">")
                    .append("To manage your subscriptions or unsubscribe, <a href=\"")
                    .append(escape(subscriptionUrl))
                    .append("\">visit your subscription preferences</a>.</p>\n");
        }
        return html.append("</body></html>\n").toString();
    }

    /**
     * Renders the body as typed, followed by a plain-text equivalent of the footer.
     * @param body String
     * @param subscriptionUrl String @Nullable, the recipient's preferences page; omitted for admin copies
     * @return String
     */
    protected String renderText(String body, @Nullable String subscriptionUrl) {
        String text = normalize(body);
        StringBuilder plain = new StringBuilder();
        if (!text.isEmpty()) {
            plain.append(text).append("\n\n");
        }
        plain.append(SIGNATURE_TEXT);
        if (subscriptionUrl != null) {
            plain.append("\n\nTo manage your subscriptions or unsubscribe, visit: ").append(subscriptionUrl);
        }
        return plain.append("\n").toString();
    }

    /**
     * This function takes in an ApiUserMessage and an optional ApiUser parameter. It creates
     * and returns a Multipart object; one part contains the message in HTML, and a second part
     * contains the message in plain text. Both are rendered from the same plain-text body.
     * NOTE: The ApiUser parameter is optional because only ApiUsers will have a link in their
     * email to their subscription preferences. That link will not be added for admins.
     * @param message ApiUserMessage
     * @param user ApiUser @Nullable
     * @return Multipart
     */
    protected Multipart getMultiPart(ApiUserMessage message, @Nullable ApiUser user) {
        Multipart multipart = mimeSender.createMimeMultipart();
        MimeBodyPart htmlPart = mimeSender.getMimeBodyPart();
        MimeBodyPart textPart = mimeSender.getMimeBodyPart();
        String subscriptionUrl = user == null ? null : getSubscriptionUrl(user.getApiKey());
        try {
            textPart.setText(renderText(message.getBody(), subscriptionUrl), CHARSET);
            htmlPart.setText(renderHtml(message.getBody(), subscriptionUrl), CHARSET, "html");
            // Add parts in order of increasing preference.
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);
        } catch (MessagingException ex) {
            throw new MailException(ex.toString(), ex);
        }
        return multipart;
    }

    /**
     * This method takes in an ApiUserMessage, an email address (String) and an optional ApiUser. It
     * creates a MimeMessage from the configured sender address, with the recipient being the email
     * address passed in, and the subject and content being based on the message passed in.
     * The content is set by making a call to getMultiPart()
     *
     * If ApiUser is not null, there will be a subscription preferences link generated in their message.
     *
     * @param message
     * @param email
     * @param user
     * @return MimeMessage
     * @throws MailException
     */
    protected MimeMessage getMimeMessage(ApiUserMessage message, String email, @Nullable ApiUser user) throws MailException {
        try {
            MimeMessage mimeMessage = mimeSender.createMessage();
            mimeMessage.setFrom(new InternetAddress(env.getEmailFromAddress()));
            mimeMessage.setSubject(message.getSubject(), CHARSET);
            Address emailAddress = new InternetAddress(email);
            mimeMessage.setRecipient(MimeMessage.RecipientType.TO, emailAddress);
            mimeMessage.setContent(getMultiPart(message, user));
            return mimeMessage;
        } catch (MessagingException ex) {
            throw new MailException(ex.toString(), ex);
        }
    }

    /** One copy of the message; only API users get a subscription preferences link. */
    private record Recipient(String email, @Nullable ApiUser user) {}

    /** One sending of a message, whose progress can be read while it is being sent. */
    private static final class Batch {
        private final ApiUserMessage message;
        private final List<Recipient> recipients;
        private final LocalDateTime started = LocalDateTime.now();
        private int sent;
        private final List<UnsentRecipient> unsent = new ArrayList<>();
        private LocalDateTime finished;

        Batch(ApiUserMessage message, List<Recipient> recipients) {
            this.message = message;
            this.recipients = recipients;
        }

        synchronized void recordSent() {
            sent++;
        }

        synchronized void recordUnsent(UnsentRecipient recipient) {
            unsent.add(recipient);
        }

        synchronized void finish() {
            finished = LocalDateTime.now();
        }

        synchronized boolean running() {
            return finished == null;
        }

        synchronized BatchEmailStatus status() {
            return new BatchEmailStatus(message.getSubject(), recipients.size(),
                    new BatchEmailResult(sent, List.copyOf(unsent)), started, finished);
        }
    }

    /** Unifies line endings and drops leading and trailing blank space. */
    private static String normalize(@Nullable String body) {
        return body == null ? "" : body.replace("\r\n", "\n").replace('\r', '\n').strip();
    }

    /** Escapes one line of text and turns the http(s) URLs in it into links. */
    private static String linkify(String line) {
        StringBuilder html = new StringBuilder();
        Matcher matcher = URL.matcher(line);
        int last = 0;
        while (matcher.find()) {
            String url = trimTrailingPunctuation(matcher.group());
            int end = matcher.start() + url.length();
            html.append(escape(line.substring(last, matcher.start())))
                    .append("<a href=\"").append(escape(url)).append("\">")
                    .append(escape(url)).append("</a>");
            last = end;
        }
        return html.append(escape(line.substring(last))).toString();
    }

    /**
     * Drops sentence punctuation that commonly follows a URL but is rarely part of one.
     * A closing parenthesis is kept when the URL opened it, as in .../wiki/Bill_(law).
     */
    private static String trimTrailingPunctuation(String url) {
        int end = url.length();
        while (end > 0) {
            char last = url.charAt(end - 1);
            boolean unbalanced = last == ')' && count(url, end, ')') > count(url, end, '(');
            if (TRAILING_PUNCTUATION.indexOf(last) < 0 && !unbalanced) {
                break;
            }
            end--;
        }
        return url.substring(0, end);
    }

    private static long count(String text, int end, char c) {
        return text.substring(0, end).chars().filter(ch -> ch == c).count();
    }

    private static String escape(String text) {
        return HtmlUtils.htmlEscape(text, CHARSET);
    }

    private static final Logger logger = LoggerFactory.getLogger(ApiUserBatchEmailServiceImpl.class);

    /** Copies in a row that may fail because of the mail server before the rest of a batch is given up. */
    static final int MAX_SERVER_FAILURES_IN_A_ROW = 3;

    private static final Comparator<Recipient> BY_EMAIL =
            Comparator.comparing(Recipient::email, Comparator.nullsFirst(Comparator.naturalOrder()));

    private static final String CHARSET = StandardCharsets.UTF_8.name();

    private static final String PREVIEW_API_KEY = "YOUR_API_KEY";

    private static final Pattern PARAGRAPH_BREAK = Pattern.compile("\n\\s*\n");

    private static final Pattern URL = Pattern.compile("https?://[^\\s<>\"]+");

    private static final String TRAILING_PUNCTUATION = ".,;:!?]'";

    /** Uses a table rather than flexbox, which many email clients don't support. */
    private static final String SIGNATURE_HTML = """
            <table role="presentation" cellpadding="0" cellspacing="0" border="0" \
            style="background:#d3d3d3;margin-top:16px;width:100%;">
            <tr>
            <td style="padding:5px;width:70px;vertical-align:top;">\
            <img src="https://legislation.nysenate.gov/static/img/nys_logo224x224.png" alt="NYS Logo" \
            width="70" height="70" style="display:block;width:70px;height:70px;"></td>
            <td style="padding:5px;font-size:12px;vertical-align:top;">\
            <span style="font-size:16px;">Open Legislation</span><br>
            From the <a href="https://www.nysenate.gov/">New York State Senate</a><br>
            <a href="https://legislation.nysenate.gov/">https://legislation.nysenate.gov/</a><br>
            <a href="https://github.com/nysenate/OpenLegislation">https://github.com/nysenate/OpenLegislation</a>\
            </td>
            </tr>
            </table>
            """;

    private static final String SIGNATURE_TEXT = """
            --\s
            Open Legislation
            From the New York State Senate (https://www.nysenate.gov/)
            https://legislation.nysenate.gov/
            https://github.com/nysenate/OpenLegislation""";
}
