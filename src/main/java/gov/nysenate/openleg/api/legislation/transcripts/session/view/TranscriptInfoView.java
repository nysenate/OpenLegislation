package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;

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
