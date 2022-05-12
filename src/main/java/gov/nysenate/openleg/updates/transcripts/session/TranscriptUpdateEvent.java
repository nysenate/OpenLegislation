package gov.nysenate.openleg.updates.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public class TranscriptUpdateEvent extends ContentUpdateEvent {
    private final Transcript transcript;

    public TranscriptUpdateEvent(Transcript transcript) {
        this.transcript = transcript;
    }

    public Transcript getTranscript() {
        return transcript;
    }
}
