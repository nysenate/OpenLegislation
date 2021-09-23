package gov.nysenate.openleg.updates.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.time.LocalDateTime;

public class TranscriptUpdateEvent extends ContentUpdateEvent
{
    protected Transcript transcript;

    public TranscriptUpdateEvent(Transcript transcript, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.transcript = transcript;
    }

    public Transcript getTranscript() {
        return transcript;
    }
}
