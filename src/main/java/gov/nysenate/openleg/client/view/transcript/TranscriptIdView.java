package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.transcript.TranscriptId;

public class TranscriptIdView implements ViewObject
{
    protected String filename;

    public TranscriptIdView(TranscriptId transcriptId) {
        this.filename = transcriptId.getFilename();
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String getViewType() {
        return "transcript-id";
    }
}
