package gov.nysenate.openleg.updates.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

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
