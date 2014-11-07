package gov.nysenate.openleg.service.entity.committee.event;

import gov.nysenate.openleg.model.entity.Committee;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

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
