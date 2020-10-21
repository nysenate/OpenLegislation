package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;

public class PublicHearingIdView implements ViewObject
{
    private String filename;

    public PublicHearingIdView(PublicHearingId publicHearingId) {
        this.filename = publicHearingId.getFileName();
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String getViewType() {
        return "hearing-id";
    }
}
