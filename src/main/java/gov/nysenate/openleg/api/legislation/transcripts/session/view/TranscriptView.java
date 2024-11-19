package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;

public class TranscriptView extends TranscriptInfoView {
    protected String text;

    public TranscriptView(Transcript transcript) {
        super(transcript);
        this.text = transcript.getText();
    }

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "transcript";
    }
}
