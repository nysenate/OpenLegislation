package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.transcript.TranscriptId;

public class TranscriptIdView implements ViewObject
{
    protected String timestamp;

    public TranscriptIdView(TranscriptId transcriptId) {
        this.timestamp = transcriptId.getTimestamp().toString();
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String getViewType() {
        return "transcript-id";
    }
}
