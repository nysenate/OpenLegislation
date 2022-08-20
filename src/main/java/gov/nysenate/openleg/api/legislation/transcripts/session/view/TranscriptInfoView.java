package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;

public class TranscriptInfoView extends TranscriptIdView {
    private final String location;

    public TranscriptInfoView(Transcript transcript) {
        super(transcript.getId());
        this.location = transcript.getLocation();
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String getViewType() {
        return "transcript-info";
    }
}
