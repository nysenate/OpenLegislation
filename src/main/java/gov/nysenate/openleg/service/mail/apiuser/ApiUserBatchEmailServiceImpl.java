package gov.nysenate.openleg.service.mail.apiuser;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.auth.ApiUserDao;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
import gov.nysenate.openleg.service.mail.MailException;
import gov.nysenate.openleg.service.mail.MimeSendMailService;
import gov.nysenate.openleg.util.MailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ApiUserBatchEmailServiceImpl implements ApiUserBatchEmailService {

    private MimeSendMailService mimeSender;
    private final Environment env;
    private final ApiUserDao apiUserDao;

    @Autowired
    public ApiUserBatchEmailServiceImpl(MailUtils mailUtils, Environment env, ApiUserDao apiUserDao) {
        mimeSender = new MimeSendMailService(mailUtils, env);
        this.apiUserDao = apiUserDao;
        this.env = env;
    }

    //rethrow a runtime exception in catch
    public int sendMessage(ApiUserMessage message) throws MailException {
        //get all the users who are subscribed to one or more of the subscriptions
        Set<ApiUserSubscriptionType> subs = message.getSubscriptionTypes();
        List<ApiUser> usersList = new ArrayList<>();
        for(ApiUserSubscriptionType sub: subs) {
            usersList.addAll(apiUserDao.getUsersWithSubscription(sub));
        }
        //use a set for the users to avoid sending duplicate emails
        Set<ApiUser> users = new HashSet<>(usersList);

        Set<MimeMessage> allEmails = new HashSet<>();
        MimeMessage mimeMessage;
        Multipart multipart;
        BodyPart htmlPart;
        BodyPart textPart;

        //go through set of users
        try {
            for (ApiUser user : users) {
                //get an instance of MimeMessage
                mimeMessage = mimeSender.createMessage();

                //create the mimeMessage
                mimeMessage.setSubject(message.getSubject());

                //Set the recipient of the mimeMessage
                Address emailAddress = new InternetAddress(user.getEmail());
                mimeMessage.setRecipient(MimeMessage.RecipientType.TO, emailAddress);

                //create mimeMultiPart objects (html, text)
                multipart = mimeSender.createMimeMultipart();
                htmlPart = mimeSender.getMimeBodyPart();
                textPart = mimeSender.getMimeBodyPart();
                String content =  message.getBody() + "<br/><br/>" + getLink(user);
                String text = message.getBody() + "\n\n" + getLinkText(user);
                htmlPart.setContent(content, "text/html");
                textPart.setText(text);
                multipart.addBodyPart(htmlPart);
                multipart.addBodyPart(textPart);

                //set the content of the message to be the multipart object
                mimeMessage.setContent(multipart);

                //add the email to a list of mimeMessages
                allEmails.add(mimeMessage);
            }
        } catch (MessagingException ex) {
            throw new MailException(ex.toString());
        }

        //send the emails
        mimeSender.sendMessages(allEmails);

        return allEmails.size();
    }

    protected String getLink(ApiUser user) {
        String apiKey = user.getApiKey();
        String link = env.getUrl();
        link += "/subscriptions?key=" + apiKey;
        String clickHere = "<a href=\""+link+"\">Click Here.</a>";
        clickHere = "To update your subscription preferences, " + clickHere;
        return clickHere;
    }

    protected String getLinkText(ApiUser user) {
        String apiKey = user.getApiKey();
        String link = env.getUrl();
        link += "/subscriptions?key=" + apiKey;
        String clickHere = "To update your subscription preferences, use this URL: " + link;
        return clickHere;
    }


}
