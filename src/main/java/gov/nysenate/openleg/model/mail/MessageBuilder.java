package gov.nysenate.openleg.model.mail;

import gov.nysenate.openleg.util.MailUtils;

import javax.mail.*;
import javax.mail.internet.MimeMessage;

/**
 * A builder class that constructs a mail message
 */
public class MessageBuilder
{
    private Message message;

    private MessageBuilder(Session session) {
        message = new MimeMessage(session);
    }

    public static MessageBuilder newMessage(Session session) {
        return new MessageBuilder(session);
    }

    public MessageBuilder from(Address... addresses) throws MessagingException {
        message.addFrom(addresses);
        return this;
    }

    public MessageBuilder to(Address... addresses) throws MessagingException {
        message.addRecipients(Message.RecipientType.TO, addresses);
        return this;
    }

    public MessageBuilder cc(Address... addresses) throws MessagingException {
        message.addRecipients(Message.RecipientType.CC, addresses);
        return this;
    }

    public MessageBuilder bcc(Address... addresses) throws MessagingException {
        message.addRecipients(Message.RecipientType.BCC, addresses);
        return this;
    }

    public MessageBuilder subject(String subject) throws MessagingException {
        message.setSubject(subject);
        return this;
    }

    public MessageBuilder text(String text) throws MessagingException {
        message.setText(text);
        return this;
    }

    public MessageBuilder content(Object content, String contentType) throws MessagingException {
        message.setContent(content, contentType);
        return this;
    }

    public MessageBuilder content(Multipart multipart) throws MessagingException {
        message.setContent(multipart);
        return this;
    }

    public Message build() {
        return message;
    }
}
