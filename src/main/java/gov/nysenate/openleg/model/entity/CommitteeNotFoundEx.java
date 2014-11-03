package gov.nysenate.openleg.model.entity;

import com.google.common.collect.Range;
import gov.nysenate.openleg.util.DateUtils;

import java.time.LocalDateTime;

public class CommitteeNotFoundEx extends Exception {

    private static final long serialVersionUID = -1117480470668390088L;

    protected CommitteeId committeeId;
    protected Range<LocalDateTime> dateTimeRange = null;

    public CommitteeNotFoundEx(CommitteeId committeeId, Throwable cause) {
        super(
                "Could not find committee " + committeeId,
                cause
        );
        this.committeeId = committeeId;
    }

    public CommitteeNotFoundEx(CommitteeId committeeId, Range<LocalDateTime> dateTimeRange, Throwable cause) {
        super(
                String.format("Could not find instance of committee %s from %s to %s", committeeId,
                        DateUtils.startOfDateTimeRange(dateTimeRange), DateUtils.endOfDateTimeRange(dateTimeRange)),
                cause
        );
        this.committeeId = committeeId;
        this.dateTimeRange = dateTimeRange;
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

    public LocalDateTime getStartDateTime() {
        return dateTimeRange != null ? DateUtils.startOfDateTimeRange(dateTimeRange) : null;
    }

    public LocalDateTime getEndDateTime() {
        return dateTimeRange != null ? DateUtils.endOfDateTimeRange(dateTimeRange) : null;
    }

    public Range<LocalDateTime> getDateTimeRange() {
        return dateTimeRange;
    }
}
