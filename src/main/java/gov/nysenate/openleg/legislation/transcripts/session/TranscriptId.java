package gov.nysenate.openleg.legislation.transcripts.session;

import java.time.LocalDateTime;

/**
 * Used to uniquely identify transcripts.
 */
public record TranscriptId(LocalDateTime dateTime, SessionType sessionType)
        implements Comparable<TranscriptId> {
    public static TranscriptId from(LocalDateTime dateTime, String typeStr) {
        return new TranscriptId(dateTime, new SessionType(typeStr));
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(dateTime, sessionType);
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
