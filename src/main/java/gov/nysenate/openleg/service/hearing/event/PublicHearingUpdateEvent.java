package gov.nysenate.openleg.service.hearing.event;

import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;

public class PublicHearingUpdateEvent extends ContentUpdateEvent
{
    protected PublicHearing hearing;

    public PublicHearingUpdateEvent(PublicHearing hearing, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.hearing = hearing;
    }

    public PublicHearing getPublicHearing() {
        return hearing;
    }
}
