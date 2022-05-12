package gov.nysenate.openleg.updates.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Collection;

public class BulkTranscriptUpdateEvent extends ContentUpdateEvent {
    // TODO: unused?
    private final Collection<Transcript> transcripts;

    public BulkTranscriptUpdateEvent(Collection<Transcript> transcripts) {
        this.transcripts = transcripts;
    }

    public Collection<Transcript> getTranscripts() {
        return transcripts;
    }
}
