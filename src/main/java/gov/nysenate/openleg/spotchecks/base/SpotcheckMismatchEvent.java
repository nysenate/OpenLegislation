package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatch;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Optional;

/** An event that is posted when a spotcheck mismatch occurs */
public class SpotcheckMismatchEvent<ContentId> extends ContentUpdateEvent {
    private final ContentId contentId;
    private final SpotCheckMismatch mismatch;

    public SpotcheckMismatchEvent(ContentId contentId, SpotCheckMismatch mismatch) {
        this.contentId = contentId;
        this.mismatch = mismatch;
    }

    public SpotCheckMismatchType getMismatchType() {
        return Optional.ofNullable(mismatch)
                .map(SpotCheckMismatch::getMismatchType)
                .orElse(null);
    }

    public ContentId getContentId() {
        return contentId;
    }

    public SpotCheckMismatch getMismatch() {
        return mismatch;
    }
}
