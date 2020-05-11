package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.transcript.TranscriptId;

public class TranscriptIdView implements ViewObject
{
    protected String dateTime;

    public TranscriptIdView(TranscriptId transcriptId) {
        this.dateTime = transcriptId.getDateTime().toString();
    }

    public String getDateTime() {
        return dateTime;
    }

    @Override
    public String getViewType() {
        return "transcript-id";
    }
}
