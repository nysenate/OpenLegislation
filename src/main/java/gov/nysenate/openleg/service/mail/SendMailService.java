package gov.nysenate.openleg.service.mail;

import org.springframework.mail.SimpleMailMessage;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Collection;

public interface SendMailService {

    /**
     * Sends an email message constructed from basic message parameters
     *
     * @param to The intended receiver of the email
     * @param from The from address for the email
     * @param subject Subject of the email
     * @param text The email body
     */
    void sendMessage(String to, String from, String subject, String text);

    /**
     * Sends an email message constructed from basic message parameters
     *
     * @param to The intended receiver of the email
     * @param subject Subject of the email
     * @param text The email body
     */
    void sendMessage(String to, String subject, String text);

    /**
     * Sends one or more email messages constructed from the given SimpleMailMessages
     * @param messages
     */
    void sendMessage(SimpleMailMessage... messages);

    /**
     * Sends each of the given MIME Messages
     * @param messages {@link Collection<MimeMessage>} - the messages to send
     */
    void sendMessages(Collection<MimeMessage> messages);

    /**
     * Creates and returns a MIME message
     * @return MimeMessage
     */
    MimeMessage createMessage();

    /**
     * Creates and returns a MimeMultiPart
     * @return MimeMultiPart
     */
    MimeMultipart createMimeMultipart();

    /**
     * Creates and returns an empty BodyPart
     * @return BodyPart
     */
    MimeBodyPart getMimeBodyPart();
}
