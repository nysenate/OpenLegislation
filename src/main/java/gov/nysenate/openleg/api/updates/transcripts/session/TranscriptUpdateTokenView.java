package gov.nysenate.openleg.api.updates.transcripts.session;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptIdView;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateToken;

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
