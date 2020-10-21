package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;

public class PublicHearingView extends PublicHearingInfoView
{
    protected String text;

    public PublicHearingView(PublicHearing publicHearing) {
        super(publicHearing);
        this.text = publicHearing.getText();
    }

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "hearing";
    }
}
