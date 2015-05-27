package gov.nysenate.openleg.service.spotcheck.base;

import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;

/** An event that is posted when a spotcheck mismatch occurs */
public class SpotcheckMismatchEvent<ContentId> extends ContentUpdateEvent {

    private ContentId contentId;

    private SpotCheckMismatch mismatch;

    public SpotcheckMismatchEvent(LocalDateTime updateDateTime, ContentId contentId, SpotCheckMismatch mismatch) {
        super(updateDateTime);
        this.contentId = contentId;
        this.mismatch = mismatch;
    }

    public ContentId getContentId() {
        return contentId;
    }

    public SpotCheckMismatch getMismatch() {
        return mismatch;
    }
}
