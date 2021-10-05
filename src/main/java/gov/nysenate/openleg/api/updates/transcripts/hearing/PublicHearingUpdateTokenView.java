package gov.nysenate.openleg.api.updates.transcripts.hearing;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingIdView;
import gov.nysenate.openleg.updates.transcripts.hearing.PublicHearingUpdateToken;

import java.time.LocalDateTime;

public class PublicHearingUpdateTokenView implements ViewObject
{

    protected PublicHearingIdView publicHearingId;
    protected LocalDateTime dateTime;

    public PublicHearingUpdateTokenView(PublicHearingUpdateToken token, String filename) {
        this.publicHearingId = new PublicHearingIdView(token.getPublicHearingId(), filename);
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
