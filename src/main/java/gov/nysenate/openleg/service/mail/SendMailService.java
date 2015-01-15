package gov.nysenate.openleg.service.mail;

import org.springframework.mail.SimpleMailMessage;

import javax.mail.MessagingException;

public interface SendMailService {

    /**
     * Sends an email message constructed from basic message parameters
     *
     * @param to The intended receiver of the email
     * @param from The from address for the email
     * @param subject Subject of the email
     * @param text The email body
     */
    public void sendMessage(String to, String from, String subject, String text);

    /**
     * Sends an email message constructed from basic message parameters
     *
     * @param to The intended receiver of the email
     * @param subject Subject of the email
     * @param text The email body
     */
    public void sendMessage(String to, String subject, String text);

    /**
     * Sends one or more email messages constructed from the given SimpleMailMessages
     * @param messages
     */
    public void sendMessage(SimpleMailMessage... messages);
}
