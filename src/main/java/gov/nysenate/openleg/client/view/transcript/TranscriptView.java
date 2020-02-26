package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.model.transcript.Transcript;

public class TranscriptView extends TranscriptInfoView
{
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
