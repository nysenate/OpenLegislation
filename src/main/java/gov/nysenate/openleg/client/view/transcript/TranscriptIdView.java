package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.transcript.TranscriptId;

public class TranscriptIdView implements ViewObject
{
    protected String localDateTime;

    public TranscriptIdView(TranscriptId transcriptId) {
        this.localDateTime = transcriptId.getLocalDateTime().toString();
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    @Override
    public String getViewType() {
        return "transcript-id";
    }
}
