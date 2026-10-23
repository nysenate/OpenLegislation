package gov.nysenate.openleg.notifications.mail;

import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.mail.SimpleMailMessage;

import jakarta.mail.internet.MimeMessage;

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
     * Sends a MIME message on a connection of its own, reporting what is known about its delivery instead of
     * throwing, so a caller sending many messages can decide whether to go on.
     * @param message the message to send
     * @return {@link SendResult} - the outcome, with the cause unless it was sent
     */
    SendResult trySend(MimeMessage message);

    /**
     * Creates and returns a MIME message
     *
     * @return MimeMessage
     */
    MimeMessage createMessage();

    /**
     * Creates and returns a MimeMultiPart
     *
     * @return MimeMultiPart
     */
    MimeMultipart createMimeMultipart();

    /**
     * Creates and returns an empty BodyPart
     *
     * @return BodyPart
     */
    MimeBodyPart getMimeBodyPart();
}
