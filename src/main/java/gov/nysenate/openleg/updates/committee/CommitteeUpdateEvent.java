package gov.nysenate.openleg.updates.committee;

import gov.nysenate.openleg.legislation.committee.Committee;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.time.LocalDateTime;

public class CommitteeUpdateEvent extends ContentUpdateEvent {

    protected Committee committee;

    public CommitteeUpdateEvent(Committee committee, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.committee = committee;
    }

    public Committee getCommittee() {
        return committee;
    }
}
