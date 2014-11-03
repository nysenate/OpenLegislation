package gov.nysenate.openleg.model.search;

/**
 * General search exception
 */
public class SearchException extends Exception
{
    private static final long serialVersionUID = 1973429629373205362L;

    public SearchException() {}

    public SearchException(String message) {
        super(message);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
