package gov.nysenate.openleg.spotchecks.openleg;

public class OpenlegJsonRetrievalEx extends RuntimeException {
    public OpenlegJsonRetrievalEx(String message) {
        super(message);
    }

    public OpenlegJsonRetrievalEx(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenlegJsonRetrievalEx(Throwable cause) {
        super(cause);
    }
}
