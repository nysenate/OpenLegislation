package gov.nysenate.openleg.notifications.mail;

import java.util.List;

/**
 * Thrown before sending anything when the SMTP settings can't produce a well-formed message.
 */
public class MailConfigurationException extends MailException {

    public MailConfigurationException(List<String> problems) {
        super("Mail is not configured: " + String.join("; ", problems) + ".");
    }
}
