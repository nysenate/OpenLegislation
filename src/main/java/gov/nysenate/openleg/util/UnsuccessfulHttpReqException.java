package gov.nysenate.openleg.util;

/**
 * Thrown by when an http request returns a non successful response (status code != 2xx)
 */
public class UnsuccessfulHttpReqException extends RuntimeException {

    public UnsuccessfulHttpReqException(String message) {
        super(message);
    }

    public UnsuccessfulHttpReqException(Throwable cause) {
        super(cause);
    }

    public UnsuccessfulHttpReqException(String message, Throwable cause) {
        super(message, cause);
    }
}
