package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;

public class PublicHearingIdView implements ViewObject {
    private final Integer id;
    private final String filename;

    public PublicHearingIdView(PublicHearingId publicHearingId, String filename) {
        this.id = publicHearingId == null ? null : publicHearingId.getId();
        this.filename = filename;
    }

    public Integer getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String getViewType() {
        return "hearing-id";
    }
}
