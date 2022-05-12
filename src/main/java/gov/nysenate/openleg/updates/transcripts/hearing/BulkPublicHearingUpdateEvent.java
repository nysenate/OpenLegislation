package gov.nysenate.openleg.updates.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Collection;

public class BulkPublicHearingUpdateEvent extends ContentUpdateEvent {
    // TODO: unused?
    private final Collection<PublicHearing> hearings;

    public BulkPublicHearingUpdateEvent(Collection<PublicHearing> hearings) {
        this.hearings = hearings;
    }

    public Collection<PublicHearing> getPublicHearings() {
        return hearings;
    }
}
