package gov.nysenate.openleg.service.transcript.event;

import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;
import java.util.Collection;

public class BulkTranscriptUpdateEvent extends ContentUpdateEvent
{
    protected Collection<Transcript> transcripts;

    public BulkTranscriptUpdateEvent(Collection<Transcript> transcripts, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.transcripts = transcripts;
    }

    public Collection<Transcript> getTranscripts() {
        return transcripts;
    }
}
