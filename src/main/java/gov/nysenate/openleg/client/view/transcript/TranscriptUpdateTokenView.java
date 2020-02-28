package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.transcript.TranscriptUpdateToken;

import java.time.LocalDateTime;

public class TranscriptUpdateTokenView implements ViewObject
{
    private TranscriptIdView transcriptId;
    private LocalDateTime updateDateTime;

    public TranscriptUpdateTokenView(TranscriptUpdateToken token) {
        this.transcriptId = new TranscriptIdView(token.getTranscriptId());
        this.updateDateTime = token.getUpdateDateTime();
    }

    public TranscriptIdView getTranscriptId() {
        return transcriptId;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    @Override
    public String getViewType() {
        return "transcript-update-token";
    }
}
