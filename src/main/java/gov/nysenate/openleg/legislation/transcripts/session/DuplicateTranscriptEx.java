package gov.nysenate.openleg.legislation.transcripts.session;

import java.time.LocalDateTime;

public class DuplicateTranscriptEx extends RuntimeException {
    private final LocalDateTime localDateTime;

    public DuplicateTranscriptEx(LocalDateTime localDateTime) {
        super("There are multiple transcripts at " + localDateTime + ". Please specify.");
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
