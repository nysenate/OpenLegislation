package gov.nysenate.openleg.processor.sobi;

import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.processor.base.ParseError;

import java.time.LocalDateTime;

public class LegDataParseException extends ParseError
{
    /** Reference to the LegDataFragment that resulted in the exception */
    private LegDataFragment legDataFragment;

    /** The date/time when the exception was encountered. */
    private LocalDateTime parseDateTime;

    public LegDataParseException(String message, LegDataFragment legDataFragment, LocalDateTime parseDateTime) {
        super(message);
        this.legDataFragment = legDataFragment;
        this.parseDateTime = parseDateTime;
    }

    public LegDataParseException(String message, Throwable cause, LegDataFragment legDataFragment, LocalDateTime parseDateTime) {
        super(message, cause);
        this.legDataFragment = legDataFragment;
        this.parseDateTime = parseDateTime;
    }
}
