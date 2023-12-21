package gov.nysenate.openleg.api.updates.transcripts.session;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptIdView;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateToken;

import java.time.LocalDateTime;

public record TranscriptUpdateTokenView(TranscriptIdView transcriptId, LocalDateTime updateDateTime)
        implements ViewObject {

    public TranscriptUpdateTokenView(TranscriptUpdateToken token) {
        this(new TranscriptIdView(token.getTranscriptId()), token.getUpdateDateTime());
    }

    @Override
    public String getViewType() {
        return "transcript-update-token";
    }
}
