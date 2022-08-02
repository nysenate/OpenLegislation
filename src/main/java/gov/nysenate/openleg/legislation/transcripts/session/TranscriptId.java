package gov.nysenate.openleg.legislation.transcripts.session;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Used to uniquely identify transcripts.
 */
public record TranscriptId(LocalDateTime dateTime) implements Comparable<TranscriptId> {
    /**
     * Creates a TranscriptId from a string representation of an ISO date time.
     * @param dateTime String in the format of 'yyyy-mm-ddThh:mm:ss'
     * @throws DateTimeParseException if <code>datetime</code> is in an invalid format.
     */
    public TranscriptId(String dateTime) {
        this(LocalDateTime.parse(dateTime));
    }

    @Override
    public int compareTo(TranscriptId o) {
        return dateTime.compareTo(o.dateTime);
    }
}
