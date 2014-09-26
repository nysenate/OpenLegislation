package gov.nysenate.openleg.processor.law;

public class LawParseException extends RuntimeException
{
    public LawParseException() {
        super();
    }

    public LawParseException(String message) {
        super(message);
    }
}
