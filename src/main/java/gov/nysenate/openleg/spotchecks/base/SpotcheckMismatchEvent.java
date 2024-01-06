package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatch;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Optional;

/**
 * An event that is posted when a spotcheck mismatch occurs
 */
public record SpotcheckMismatchEvent<ContentId>(ContentId contentId, SpotCheckMismatch mismatch)
        implements ContentUpdateEvent {

    public SpotCheckMismatchType getMismatchType() {
        return Optional.ofNullable(mismatch)
                .map(SpotCheckMismatch::getMismatchType)
                .orElse(null);
    }
}
