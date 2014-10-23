package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;

public class TranscriptView extends TranscriptIdView
{
    protected String location;
    protected String text;

    public TranscriptView(Transcript transcript) {
        super(new TranscriptId(transcript.getSessionType(), transcript.getDateTime()));
        this.location = transcript.getLocation();
        this.text = transcript.getTranscriptText();
    }

    public String getLocation() {
        return location;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "transcript";
    }
}
