package gov.nysenate.openleg.search;

public class InvalidSearchParamException extends RuntimeException {

    private static final long serialVersionUID = 2245867337608015453L;

    public InvalidSearchParamException() {}

    public InvalidSearchParamException(String message) {
        super(message);
    }

    public InvalidSearchParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
