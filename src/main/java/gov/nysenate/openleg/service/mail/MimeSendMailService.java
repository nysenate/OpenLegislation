package gov.nysenate.openleg.service.mail;

import gov.nysenate.openleg.model.mail.MessageBuilder;
import gov.nysenate.openleg.util.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.MessagingException;


@Service
public class MimeSendMailService implements SendMailService {

    @Autowired
    private MailUtils mailUtils;

    private MailSender mailSender;

    @PostConstruct
    public void init() {
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

    @Override
    public void sendMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(SimpleMailMessage... messages) {
        mailSender.send(messages);
    }
}
