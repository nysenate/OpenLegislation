package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;

public class HearingView extends HearingInfoView {
    private final String text;

    public HearingView(Hearing hearing) {
        super(hearing);
        this.text = hearing.getText();
    }

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "hearing";
    }
}
