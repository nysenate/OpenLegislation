package gov.nysenate.openleg.legislation.transcripts.session;

import java.io.Serial;

public class TranscriptNotFoundEx extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6423218803207531965L;

    private final TranscriptId transcriptId;

    public TranscriptNotFoundEx(TranscriptId transcriptId, Exception ex) {
        super((transcriptId != null) ? "Transcript " + transcriptId + " could not be retrieved."
                        : "Transcript could not be retrieved since the given TranscriptId was null", ex);
        this.transcriptId = transcriptId;
    }

    public TranscriptId getTranscriptId() {
        return transcriptId;
    }
}
