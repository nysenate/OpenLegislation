package gov.nysenate.openleg.service.mail.apiuser;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.auth.AdminUserDao;
import gov.nysenate.openleg.dao.auth.ApiUserDao;
import gov.nysenate.openleg.model.auth.AdminUser;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
import gov.nysenate.openleg.service.mail.MailException;
import gov.nysenate.openleg.service.mail.MimeSendMailService;
import gov.nysenate.openleg.util.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

import org.apache.commons.validator.routines.EmailValidator;

@Service
public class ApiUserBatchEmailServiceImpl implements ApiUserBatchEmailService {

    private MimeSendMailService mimeSender;
    private final Environment env;
    private final ApiUserDao apiUserDao;
    private final AdminUserDao adminUserDao;
    private EmailValidator validator = EmailValidator.getInstance();

    @Autowired
    public ApiUserBatchEmailServiceImpl(MailUtils mailUtils, Environment env, ApiUserDao apiUserDao,
                                        AdminUserDao adminUserDao) {
        mimeSender = new MimeSendMailService(mailUtils, env);
        this.apiUserDao = apiUserDao;
        this.env = env;
        this.adminUserDao = adminUserDao;
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
        Set<ApiUserSubscriptionType> subs = message.getSubscriptionTypes();
        List<ApiUser> usersList = new ArrayList<>();
        for (ApiUserSubscriptionType sub : subs) {
            usersList.addAll(apiUserDao.getUsersWithSubscription(sub));
        }
        //use a set for the users to avoid sending duplicate emails
        Set<ApiUser> users = new HashSet<>(usersList);

        //create a set with all the admins
        List<AdminUser> adminsList = adminUserDao.getAdminUsers();
        Set<AdminUser> admins = new HashSet<>(adminsList);

        Set<MimeMessage> allEmails = new HashSet<>();
        MimeMessage mimeMessage;
        String email;

        //go through set of users an admins
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
            textPart.setText(text);
            multipart.addBodyPart(htmlPart);
            multipart.addBodyPart(textPart);
        } catch (MessagingException ex) {
            throw new MailException(ex.toString());
        }
        return multipart;
    }

    /**
     * This method takes in an ApiUserMessage, an email address (String) and an optional ApiUser. It
     * creates a MimeMessage, with the recipient being the email address passed in, and the subject and
     * content being based on the message passed in. The content is set by making a call to getMultiPart()
     * @param message
     * @param email
     * @param user
     * @return MimeMessage
     * @throws MailException
     */
    //method that takes in a message and an email (string)  and returns a mimeMessage object (without the content set)
    //sets the subject and recipient
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
}
