package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.transcript.TranscriptUpdateToken;

import java.time.LocalDateTime;

public class TranscriptUpdateTokenView implements ViewObject
{
    private TranscriptIdView transcriptId;
    private LocalDateTime dateTime;

    public TranscriptUpdateTokenView(TranscriptUpdateToken token) {
        this.transcriptId = new TranscriptIdView(token.getTranscriptId());
        this.dateTime = token.getUpdateDateTime();
    }

    public TranscriptIdView getTranscriptId() {
        return transcriptId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String getViewType() {
        return "transcript-update-token";
    }
}
