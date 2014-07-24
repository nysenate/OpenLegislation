package gov.nysenate.openleg.service.entity;

import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.util.Date;

public class CommitteeNotFoundEx extends Exception{
    protected CommitteeId committeeId;

    public CommitteeNotFoundEx(CommitteeId committeeId, Throwable cause) {
        super(
                "Could not find committee " + committeeId,
                cause
        );
        this.committeeId = committeeId;
    }
    public CommitteeNotFoundEx(Chamber chamber,Throwable cause) {
        super(
                "Could not find committee records for " + chamber,
                cause
        );
        this.committeeId = new CommitteeId(chamber, "All Committees");
    }

    public CommitteeId getCommitteeId(){
        return committeeId;
    }
}
