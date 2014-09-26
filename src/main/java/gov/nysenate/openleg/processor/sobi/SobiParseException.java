package gov.nysenate.openleg.processor.sobi;

import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.time.LocalDateTime;


public class SobiParseException extends Exception
{
    /** Reference to the SobiFragment that resulted in the exception */
    private SobiFragment sobiFragment;

    /** The date/time when the exception was encountered. */
    private LocalDateTime parseDateTime;

    public SobiParseException(SobiFragment sobiFragment, LocalDateTime parseDateTime) {
        this.sobiFragment = sobiFragment;
        this.parseDateTime = parseDateTime;
    }

    public SobiParseException(String message, SobiFragment sobiFragment, LocalDateTime parseDateTime) {
        super(message);
        this.sobiFragment = sobiFragment;
        this.parseDateTime = parseDateTime;
    }

    public SobiParseException(String message, Throwable cause, SobiFragment sobiFragment, LocalDateTime parseDateTime) {
        super(message, cause);
        this.sobiFragment = sobiFragment;
        this.parseDateTime = parseDateTime;
    }
}
