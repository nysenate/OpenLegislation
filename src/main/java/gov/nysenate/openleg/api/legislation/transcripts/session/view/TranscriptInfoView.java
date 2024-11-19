package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;

public class TranscriptInfoView extends TranscriptIdView {
    private final String location;
    private final String dayType;

    public TranscriptInfoView(Transcript transcript) {
        super(transcript.getId());
        this.location = transcript.getLocation();
        this.dayType = String.valueOf(transcript.getDayType());
    }

    public String getLocation() {
        return location;
    }

    public String getDayType() {
        return dayType;
    }

    @Override
    public String getViewType() {
        return "transcript-info";
    }
}
