package gov.nysenate.openleg.updates.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public class PublicHearingUpdateEvent extends ContentUpdateEvent {
    private final PublicHearing hearing;

    public PublicHearingUpdateEvent(PublicHearing hearing) {
        this.hearing = hearing;
    }

    public PublicHearing getPublicHearing() {
        return hearing;
    }
}
