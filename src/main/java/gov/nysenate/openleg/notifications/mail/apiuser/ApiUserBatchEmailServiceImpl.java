package gov.nysenate.openleg.notifications.mail.apiuser;

import gov.nysenate.openleg.auth.admin.AdminUser;
import gov.nysenate.openleg.auth.admin.AdminUserService;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.notifications.mail.MailConfigurationException;
import gov.nysenate.openleg.notifications.mail.MailException;
import gov.nysenate.openleg.notifications.mail.MimeSendMailService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import jakarta.annotation.Nullable;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ApiUserBatchEmailServiceImpl implements ApiUserBatchEmailService {

    private final MailUtils mailUtils;
    private final MimeSendMailService mimeSender;
    private final OpenLegEnvironment env;
    private final ApiUserService apiUserService;
    private final AdminUserService adminUserService;
    private final EmailValidator validator = EmailValidator.getInstance();

    @Autowired
    public ApiUserBatchEmailServiceImpl(MailUtils mailUtils, OpenLegEnvironment env,
                                        ApiUserService apiUserService,
                                        AdminUserService adminUserService) {
        this.mailUtils = mailUtils;
        this.mimeSender = new MimeSendMailService(mailUtils, env);
        this.apiUserService = apiUserService;
        this.env = env;
        this.adminUserService = adminUserService;
    }

    public void sendTestMessage(String email, ApiUserMessage message) throws MailException {
        checkMailSettings();
        MimeMessage mimeMessage =  getMimeMessage(message, email, null);
        List<MimeMessage> mimeMessageList = new ArrayList<>();
        mimeMessageList.add(mimeMessage);
        mimeSender.sendMessages(mimeMessageList);
    }

    //rethrow a runtime exception in catch
    public int sendMessage(ApiUserMessage message) throws MailException {
        checkMailSettings();
        //get all the users who are subscribed to one or more of the subscriptions
        Set<ApiUserSubscriptionType> subscriptions = message.getSubscriptionTypes();
        Set<ApiUser> users = new HashSet<>();
        for (ApiUserSubscriptionType sub : subscriptions) {
            users.addAll(apiUserService.getUsersWithSubscription(sub));
        }

        //create a set with all the admins
        Set<AdminUser> admins = new HashSet<>(adminUserService.getAdminUsers());

        Set<MimeMessage> allEmails = new HashSet<>();
        MimeMessage mimeMessage;
        String email;

        //go through set of users
        for (ApiUser user : users) {
            email = user.getEmail();
            mimeMessage = getMimeMessage(message, email, user);

            //add the email to a list of mimeMessages if email address is valid
            if (validator.isValid(email)) {
                allEmails.add(mimeMessage);
            }
        }

        //repeat the process for admins (all admins will receive all batch emails
        for (AdminUser admin : admins) {
            email = admin.getUsername();
            mimeMessage = getMimeMessage(message, email, null);

            //add the email to a list of mimeMessages if email address is valid
            if (validator.isValid(email)) {
                allEmails.add(mimeMessage);
            }
        }

        //send the emails and return the number sent
        mimeSender.sendMessages(allEmails);
        return allEmails.size();
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
            throw new MailException(ex.toString());
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
            throw new MailException(ex.toString());
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
