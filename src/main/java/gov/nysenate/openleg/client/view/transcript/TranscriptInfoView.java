package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.model.transcript.Transcript;

import java.time.LocalDateTime;

public class TranscriptInfoView extends TranscriptIdView
{
    protected String sessionType;
    protected LocalDateTime dateTime;
    protected String location;

    public TranscriptInfoView(Transcript transcript) {
        super((transcript != null) ? transcript.getTranscriptId() : null);
        if (transcript != null) {
            this.sessionType = transcript.getSessionType();
            this.dateTime = transcript.getDateTime();
            this.location = transcript.getLocation();
        }
    }

    public String getSessionType() {
        return sessionType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String getViewType() {
        return "transcript-info";
    }
}
