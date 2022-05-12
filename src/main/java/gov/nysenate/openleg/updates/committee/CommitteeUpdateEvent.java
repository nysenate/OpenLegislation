package gov.nysenate.openleg.updates.committee;

import gov.nysenate.openleg.legislation.committee.Committee;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public class CommitteeUpdateEvent extends ContentUpdateEvent {
    private final Committee committee;

    public CommitteeUpdateEvent(Committee committee) {
        super();
        this.committee = committee;
    }

    public Committee getCommittee() {
        return committee;
    }
}
