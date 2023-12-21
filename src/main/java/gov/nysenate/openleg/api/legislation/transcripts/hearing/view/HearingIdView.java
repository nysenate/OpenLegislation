package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;

public class HearingIdView implements ViewObject {
    private final Integer id;
    private final String filename;

    public HearingIdView(HearingId hearingId, String filename) {
        this.id = hearingId == null ? null : hearingId.id();
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
