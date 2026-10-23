package gov.nysenate.openleg.notifications.mail;

import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.notifications.mail.SendResult.Outcome;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.angus.mail.smtp.SMTPAddressFailedException;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import jakarta.mail.Address;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;

@Service
public class MimeSendMailService implements SendMailService {

    private final OpenLegEnvironment env;

    private final JavaMailSender mailSender;

    private final Session session;

    /** Refusals that apply to every message, not to one recipient: closing, TLS or relay unavailable, login needed. */
    private static final Set<Integer> SERVER_WIDE_REPLIES = Set.of(421, 454, 530);

    @Autowired
    public MimeSendMailService(MailUtils mailUtils, OpenLegEnvironment env) {
        this.env = env;
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        this.session = mailUtils.getSmtpSession();
        javaMailSender.setSession(session);
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
     * The message is prepared as Spring's send prepares it. Spring's send isn't used because its exceptions don't
     * say how far the message got.
     */
    @Override
    public SendResult trySend(MimeMessage message) {
        TrackedCopy copy;
        Address[] recipients;
        try {
            if (message.getSentDate() == null) {
                message.setSentDate(new Date());
            }
            String messageId = message.getMessageID();
            message.saveChanges();
            if (messageId != null) {
                // saveChanges replaces it; keep one the caller set, as Spring does.
                message.setHeader("Message-ID", messageId);
            }
            copy = new TrackedCopy(message);
            recipients = message.getAllRecipients();
        } catch (Exception ex) {
            return new SendResult(Outcome.REFUSED, ex);
        }
        Transport transport = null;
        try {
            try {
                transport = session.getTransport("smtp");
                transport.connect();
            } catch (Exception ex) {
                return new SendResult(ex instanceof AuthenticationFailedException
                        ? Outcome.LOGIN_REJECTED : Outcome.NOT_CONNECTED, ex);
            }
            transport.sendMessage(copy, recipients == null ? new Address[0] : recipients);
            return SendResult.SENT;
        } catch (Exception ex) {
            int reply = replyCode(ex);
            if (reply <= 0) {
                // Without a reply to a message sent in full, the server may have queued it.
                return new SendResult(copy.written ? Outcome.UNCONFIRMED : Outcome.SERVER_FAILED, ex);
            }
            boolean serverWide = SERVER_WIDE_REPLIES.contains(reply)
                    || ex instanceof SMTPSendFailedException sfe && String.valueOf(sfe.getCommand()).startsWith("MAIL");
            return new SendResult(serverWide ? Outcome.SERVER_FAILED : Outcome.REFUSED, ex);
        } finally {
            close(transport);
        }
    }

    /** The mail server's reply code from a send failure, or 0 or less if it didn't reply or the reply was garbled. */
    private static int replyCode(Exception ex) {
        for (Throwable cause : ExceptionUtils.getThrowableList(ex)) {
            if (cause instanceof SMTPSendFailedException sfe) {
                return sfe.getReturnCode();
            }
            if (cause instanceof SMTPAddressFailedException afe) {
                return afe.getReturnCode();
            }
        }
        return 0;
    }

    /**
     * A copy of a message that records whether the transport wrote it out in full. Only then can the server
     * have received the end of the message, so a failure before that means it wasn't delivered.
     */
    private static final class TrackedCopy extends MimeMessage {
        private boolean written;

        TrackedCopy(MimeMessage source) throws MessagingException {
            super(source);
        }

        @Override
        public void writeTo(OutputStream os, String[] ignoreList) throws IOException, MessagingException {
            super.writeTo(os, ignoreList);
            written = true;
        }
    }

    private static void close(Transport transport) {
        if (transport != null) {
            try {
                transport.close();
            } catch (MessagingException ignored) {
                // Every message has already been sent or recorded as failed.
            }
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
