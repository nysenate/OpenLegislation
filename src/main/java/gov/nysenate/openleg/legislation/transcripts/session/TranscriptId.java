package gov.nysenate.openleg.legislation.transcripts.session;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Used to uniquely identify transcripts.
 */
public record TranscriptId(LocalDateTime dateTime, String sessionType) implements Comparable<TranscriptId> {
    /**
     * Creates a TranscriptId from a string representation of an ISO date time.
     * @param dateTime String in the format of 'yyyy-mm-ddThh:mm:ss'
     * @throws DateTimeParseException if <code>datetime</code> is in an invalid format.
     */
    public TranscriptId(String dateTime, String sessionType) {
        this(LocalDateTime.parse(dateTime), sessionType);
    }

    @Override
    public int compareTo(TranscriptId o) {
        int temp = dateTime.compareTo(o.dateTime);
        if (temp != 0) {
            return temp;
        }
        return sessionType.compareTo(o.sessionType);
    }
}
