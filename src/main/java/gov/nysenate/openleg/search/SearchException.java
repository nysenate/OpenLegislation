package gov.nysenate.openleg.search;

import java.io.Serial;

/**
 * General search exception
 */
public class SearchException extends Exception {
    @Serial
    private static final long serialVersionUID = 1973429629373205362L;

    public SearchException() {}

    public SearchException(String message) {
        super(message);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
