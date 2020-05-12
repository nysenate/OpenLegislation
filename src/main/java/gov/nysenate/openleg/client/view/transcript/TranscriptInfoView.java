package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.model.transcript.Transcript;

import java.time.LocalDateTime;

public class TranscriptInfoView extends TranscriptIdView
{
    protected String sessionType;
    protected String location;

    public TranscriptInfoView(Transcript transcript) {
        super((transcript != null) ? transcript.getTranscriptId() : null);
        if (transcript != null) {
            this.sessionType = transcript.getSessionType();
            this.location = transcript.getLocation();
        }
    }

    public String getSessionType() {
        return sessionType;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String getViewType() {
        return "transcript-info";
    }
}
