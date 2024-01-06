package gov.nysenate.openleg.legislation.transcripts.session;

import java.time.LocalDateTime;

/**
 * Used to uniquely identify transcripts.
 */
public record TranscriptId(LocalDateTime dateTime, String sessionType)
        implements Comparable<TranscriptId> {
    @Override
    public int compareTo(TranscriptId o) {
        int temp = dateTime.compareTo(o.dateTime);
        if (temp != 0) {
            return temp;
        }
        return o.sessionType.compareTo(sessionType);
    }
}
