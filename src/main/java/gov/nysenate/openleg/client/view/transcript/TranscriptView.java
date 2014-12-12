package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;

import java.time.LocalDateTime;

public class TranscriptView extends TranscriptIdView
{
    protected String sessionType;
    protected LocalDateTime dateTime;
    protected String location;
    protected String text;

    public TranscriptView(Transcript transcript) {
        super(new TranscriptId(transcript.getTranscriptId().getFilename()));
        this.sessionType = transcript.getSessionType();
        this.dateTime = transcript.getDateTime();
        this.location = transcript.getLocation();
        this.text = transcript.getText();
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

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "transcript";
    }
}
