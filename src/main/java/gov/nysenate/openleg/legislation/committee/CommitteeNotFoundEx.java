package gov.nysenate.openleg.legislation.committee;

public class CommitteeNotFoundEx extends RuntimeException {

    private static final long serialVersionUID = -1117480470668390088L;

    protected CommitteeId committeeId;

    public CommitteeNotFoundEx(CommitteeId committeeId, Throwable cause) {
        super(
                "Could not find committee " + committeeId,
                cause
        );
        this.committeeId = committeeId;
    }

    public CommitteeNotFoundEx(CommitteeSessionId committeeId, Throwable cause) {
        super(
                String.format("Could not find instance of committee %s for session year %d", committeeId,
                        committeeId.getSession().year()),
                cause
        );
        this.committeeId = committeeId;
    }

    public CommitteeNotFoundEx(CommitteeVersionId committeeId, Throwable cause) {
        super(
                String.format("Could not find instance of committee %s at reference time %s", committeeId,
                        committeeId.getReferenceDate().toString()),
                cause
        );
        this.committeeId = committeeId;
    }

    public CommitteeNotFoundEx(Chamber chamber, Throwable cause) {
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
