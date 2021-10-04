package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;

public class PublicHearingIdView implements ViewObject
{
    private final int id;

    public PublicHearingIdView(PublicHearingId publicHearingId) {
        this.id = publicHearingId.getId();
    }

    public int getId() {
        return id;
    }

    @Override
    public String getViewType() {
        return "hearing-id";
    }
}
