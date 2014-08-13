package gov.nysenate.openleg.model.entity;

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
