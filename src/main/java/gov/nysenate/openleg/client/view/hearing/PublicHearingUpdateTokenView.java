package gov.nysenate.openleg.client.view.hearing;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.hearing.PublicHearingUpdateToken;

import java.time.LocalDateTime;

public class PublicHearingUpdateTokenView implements ViewObject
{

    protected PublicHearingIdView publicHearingId;
    protected LocalDateTime dateTime;

    public PublicHearingUpdateTokenView(PublicHearingUpdateToken token) {
        this.publicHearingId = new PublicHearingIdView(token.getPublicHearingId());
        this.dateTime = token.getDateTime();
    }

    public PublicHearingIdView getPublicHearingId() {
        return publicHearingId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String getViewType() {
        return "public_hearing-update-token";
    }
}
