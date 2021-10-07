package gov.nysenate.openleg.processors;

public class ParseError extends RuntimeException
{
    private static final long serialVersionUID = 2809768377369235106L;

    public ParseError(String message) { super(message); }

    public ParseError(String message, Throwable cause) {
        super(message, cause);
    }
}
