package gov.nysenate.openleg.model.entity;

import com.google.common.collect.Range;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.CommitteeSessionId;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.util.DateUtils;

import java.time.LocalDateTime;

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
                        committeeId.getSession().getYear()),
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
