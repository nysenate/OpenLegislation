package gov.nysenate.openleg.notifications.mail.apiuser;

import gov.nysenate.openleg.auth.admin.AdminUser;
import gov.nysenate.openleg.auth.admin.AdminUserService;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.notifications.mail.MailException;
import gov.nysenate.openleg.notifications.mail.MimeSendMailService;
import org.apache.commons.validator.routines.EmailValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ApiUserBatchEmailServiceImpl implements ApiUserBatchEmailService {

    private final MimeSendMailService mimeSender;
    private final OpenLegEnvironment env;
    private final ApiUserService apiUserService;
    private final AdminUserService adminUserService;
    private final EmailValidator validator = EmailValidator.getInstance();

    @Autowired
    public ApiUserBatchEmailServiceImpl(MailUtils mailUtils, OpenLegEnvironment env,
                                        ApiUserService apiUserService,
                                        AdminUserService adminUserService) {
        this.mimeSender = new MimeSendMailService(mailUtils, env);
        this.apiUserService = apiUserService;
        this.env = env;
        this.adminUserService = adminUserService;
    }

    public void sendTestMessage(String email, ApiUserMessage message) throws MailException {
        MimeMessage mimeMessage =  getMimeMessage(message, email, null);
        List<MimeMessage> mimeMessageList = new ArrayList<>();
        mimeMessageList.add(mimeMessage);
        mimeSender.sendMessages(mimeMessageList);
    }

    //rethrow a runtime exception in catch
    public int sendMessage(ApiUserMessage message) throws MailException {
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

    protected String getLink(ApiUser user) {
        String apiKey = user.getApiKey();
        String link = env.getUrl();
        link += "/subscriptions?key=" + apiKey;
        String unsubLink = link + "&unsub=yes";
        String clickHere = "<a href=\"" + link + "\">Click Here</a>"+",";
        clickHere = "To update your subscription preferences, " + clickHere;
        String unsubAll = "or <a href=\"" + unsubLink + "\">unsubscribe from <strong>ALL</strong> subscriptions.</a>";
        return clickHere + "<br>" + unsubAll;
    }

    protected String getLinkText(ApiUser user) {
        String apiKey = user.getApiKey();
        String link = env.getUrl();
        link += "/subscriptions?key=" + apiKey;
        String unsubLink = link + "&unsub=yes";
        String clickHere = "To update your subscription preferences, use this URL: " + link;
        String unsubAll = "To unsubscribe from all subscriptions, use this URL: " + unsubLink;
        return clickHere + "\n" + unsubAll;
    }

    /**
     * This function takes in an ApiUserMessage and an optional ApiUser parameter. It creates
     * and returns a Multipart object; one part contains the message in HTML, and a second part
     * contains the message in plain text.
     * NOTE: The ApiUser parameter is optional because only ApiUsers will have a link in their
     * email to unsubscribe. That link will not be added for admins.
     * @param message ApiUserMessage
     * @param user ApiUser @Nullable
     * @return Multipart
     */
    protected Multipart getMultiPart(ApiUserMessage message, @Nullable ApiUser user) {
        Multipart multipart = mimeSender.createMimeMultipart();
        BodyPart htmlPart = mimeSender.getMimeBodyPart();
        BodyPart textPart = mimeSender.getMimeBodyPart();
        String content, text;
        if(user != null) {
            content = message.getBody() + "<br/><br/>" + getLink(user);
            text = message.getBody() + "\n\n" + getLinkText(user);
        } else {
            content = message.getBody();
            text = message.getBody();
        }
        try {
            htmlPart.setContent(content, "text/html");
            // Remove html tags from body for the plain text part.
            String plainText = stripHtmlTags(text);
            textPart.setText(plainText);
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
     * creates a MimeMessage, with the recipient being the email address passed in, and the subject and
     * content being based on the message passed in. The content is set by making a call to getMultiPart()
     *
     * If ApiUser is not null, there will be an unsubscribe link generated in their message.
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
            mimeMessage.setSubject(message.getSubject());
            Address emailAddress = new InternetAddress(email);
            mimeMessage.setRecipient(MimeMessage.RecipientType.TO, emailAddress);
            Multipart multipart;
            if(user != null) {
                multipart = getMultiPart(message, user);
            } else {
                multipart = getMultiPart(message, null);
            }
            mimeMessage.setContent(multipart);
            return mimeMessage;
        } catch (MessagingException ex) {
            throw new MailException(ex.toString());
        }
    }

    /**
     * Removes the html tags from the given string while preserving new lines.
     * @param text
     * @return
     */
    private String stripHtmlTags(String text) {
        return Jsoup.clean(text, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
    }
}
