package gov.nysenate.openleg.client.view.hearing;

import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingCommittee;
import gov.nysenate.openleg.model.hearing.PublicHearingId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
