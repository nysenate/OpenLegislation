package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.transcript.TranscriptId;

public class TranscriptIdView implements ViewObject
{
    protected String sessionDateTime;

    public TranscriptIdView(TranscriptId transcriptId) {
        this.sessionDateTime = transcriptId.getSessionDateTime().toString();
    }

    public String getSessionDateTime() {
        return sessionDateTime;
    }

    @Override
    public String getViewType() {
        return "transcript-id";
    }
}
