package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.transcript.TranscriptId;

import java.time.LocalDateTime;

public class TranscriptIdView implements ViewObject
{
    private String filename;

    public TranscriptIdView(TranscriptId transId) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String getViewType() {
        return "transcript-id";
    }
}
