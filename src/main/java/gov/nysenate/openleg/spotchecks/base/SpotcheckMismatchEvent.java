package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatch;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.time.LocalDateTime;
import java.util.Optional;

/** An event that is posted when a spotcheck mismatch occurs */
public class SpotcheckMismatchEvent<ContentId> extends ContentUpdateEvent {

    private ContentId contentId;

    private SpotCheckMismatch mismatch;

    public SpotcheckMismatchEvent(LocalDateTime updateDateTime, ContentId contentId, SpotCheckMismatch mismatch) {
        super(updateDateTime);
        this.contentId = contentId;
        this.mismatch = mismatch;
    }

    /* --- Functional Getters --- */

    public SpotCheckMismatchType getMismatchType() {
        return Optional.ofNullable(mismatch)
                .map(SpotCheckMismatch::getMismatchType)
                .orElse(null);
    }

    /* --- Getters --- */

    public ContentId getContentId() {
        return contentId;
    }

    public SpotCheckMismatch getMismatch() {
        return mismatch;
    }
}
