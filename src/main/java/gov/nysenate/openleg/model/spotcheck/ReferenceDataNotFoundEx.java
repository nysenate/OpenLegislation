package gov.nysenate.openleg.model.spotcheck;

/**
 * This exception should be thrown when trying to perform a spot check for a piece
 * of content and the data that should be used as a reference does not exist.
 */
public class ReferenceDataNotFoundEx extends Exception
{
    private static final long serialVersionUID = 9178407266953583110L;

    public ReferenceDataNotFoundEx() {}

    public ReferenceDataNotFoundEx(String message) {
        super(message);
    }

    public ReferenceDataNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }
}