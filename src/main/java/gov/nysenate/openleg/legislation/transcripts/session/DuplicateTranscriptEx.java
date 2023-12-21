package gov.nysenate.openleg.legislation.transcripts.session;

import java.time.LocalDateTime;

public class DuplicateTranscriptEx extends RuntimeException {
    private final String dateTime;

    public DuplicateTranscriptEx(LocalDateTime localDateTime) {
        super("There are multiple transcripts at " + localDateTime + ". Please specify.");
        this.dateTime = localDateTime.toString();
    }

    public String getDateTime() {
        return dateTime;
    }
}
