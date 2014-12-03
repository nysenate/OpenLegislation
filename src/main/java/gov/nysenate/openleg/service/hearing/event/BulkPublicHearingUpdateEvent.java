package gov.nysenate.openleg.service.hearing.event;

import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;
import java.util.Collection;

public class BulkPublicHearingUpdateEvent extends ContentUpdateEvent
{
    protected Collection<PublicHearing> hearings;

    public BulkPublicHearingUpdateEvent(Collection<PublicHearing> hearings, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.hearings = hearings;
    }

    public Collection<PublicHearing> getPublicHearings() {
        return hearings;
    }
}
