package gov.nysenate.openleg.updates.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

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
