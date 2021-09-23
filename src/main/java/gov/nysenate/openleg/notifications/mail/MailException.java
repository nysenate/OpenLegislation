package gov.nysenate.openleg.notifications.mail;

public class MailException extends RuntimeException {

    public MailException(String str) {
        this(str, null);
    }

    public MailException(String str, Throwable ex) {
        super(str,ex);
    }
}
