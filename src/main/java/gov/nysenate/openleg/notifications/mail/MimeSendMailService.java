package gov.nysenate.openleg.notifications.mail;

import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Collection;

@Service
public class MimeSendMailService implements SendMailService {

    private final OpenLegEnvironment env;

    private final JavaMailSender mailSender;

    @Autowired
    public MimeSendMailService(MailUtils mailUtils, OpenLegEnvironment env) {
        this.env = env;
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setSession(mailUtils.getSmtpSession());
        this.mailSender = javaMailSender;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String to, String from, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String to, String subject, String text) {
        sendMessage(to, env.getEmailFromAddress(), subject, text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(SimpleMailMessage... messages) {
        mailSender.send(messages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessages(Collection<MimeMessage> messages) {
        for (MimeMessage message : messages) {
            mailSender.send(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeMessage createMessage() {
        return mailSender.createMimeMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeMultipart createMimeMultipart() {
        return new MimeMultipart("alternative");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeBodyPart getMimeBodyPart() {
        return new MimeBodyPart();
    }
}
