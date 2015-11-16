package gov.nysenate.openleg.model.transcript;

public class TranscriptNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = -6423218803207531965L;

    protected TranscriptId transcriptId;

    public TranscriptNotFoundEx(TranscriptId transcriptId, Exception ex) {
        super((transcriptId != null) ?"Transcript " + transcriptId.toString() + " could not be retrieved."
                        : "Transcript could not be retrieved since the given TranscriptId was null", ex);
    }

    public TranscriptId getTranscriptId() {
        return transcriptId;
    }
}
