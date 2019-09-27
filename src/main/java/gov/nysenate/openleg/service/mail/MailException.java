package gov.nysenate.openleg.service.mail;

public class MailException extends RuntimeException {

    public MailException(String str) {
        this(str, null);
    }

    public MailException(String str, Throwable ex) {
        super(str,ex);
    }
}
