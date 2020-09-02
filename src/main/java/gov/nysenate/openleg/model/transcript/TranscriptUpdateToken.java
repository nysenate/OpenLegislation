package gov.nysenate.openleg.model.transcript;

import java.time.LocalDateTime;

public class TranscriptUpdateToken
{
    private final TranscriptId transcriptId;
    private final LocalDateTime updateDateTime;

    public TranscriptUpdateToken(TranscriptId transcriptId, LocalDateTime updateDateTime) {
        this.transcriptId = transcriptId;
        this.updateDateTime = updateDateTime;
    }

    public TranscriptId getTranscriptId() {
        return transcriptId;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }
}
