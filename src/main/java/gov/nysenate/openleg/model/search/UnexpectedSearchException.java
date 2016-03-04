package gov.nysenate.openleg.model.search;

public class UnexpectedSearchException extends SearchException {

    private static final String defaultMessage = "Unexpected Search Exception!";

    public UnexpectedSearchException() {
        this(defaultMessage);
    }

    public UnexpectedSearchException(Throwable cause) {
        this(defaultMessage, cause);
    }

    public UnexpectedSearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedSearchException(String message) {
        super(message);
    }
}
