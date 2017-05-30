package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.model.base.SessionYear;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum MismatchStatus {
    NEW(MismatchState.OPEN),
    EXISTING(MismatchState.OPEN),
    OPEN(MismatchState.OPEN),
    RESOLVED(MismatchState.CLOSED);

    private final MismatchState state;

    MismatchStatus(MismatchState state) {
        this.state = state;
    }

    public MismatchState getState() {
        return state;
    }

    public LocalDateTime getStartDateTime(LocalDate date) {
        switch(this) {
            case NEW:
            case RESOLVED:
                return date.atStartOfDay();
            default:
                return SessionYear.of(date.getYear()).getStartDateTime();
        }
    }

    public LocalDateTime getEndDateTime(LocalDate date) {
        switch(this) {
            case EXISTING:
                return date.minusDays(1).atTime(23, 59, 59);
            default:
                return date.atTime(23, 59, 59);
        }
    }
}
