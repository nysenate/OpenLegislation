package gov.nysenate.openleg.service.transcript.event;

import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

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
