package gov.nysenate.openleg.spotchecks.model;

import gov.nysenate.openleg.legislation.SessionYear;

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

    public LocalDateTime getObservedStartDateTime(LocalDate date) {
        return switch (this) {
            case NEW, RESOLVED -> date.atStartOfDay();
            default -> SessionYear.of(date.getYear()).getStartDateTime();
        };
    }

    public LocalDateTime getFirstSeenStartDateTime(LocalDate date) {
        if (this == NEW) {
            return date.atStartOfDay();
        }
        return SessionYear.of(date.getYear()).getStartDateTime();
    }

    public LocalDateTime getFirstSeenEndDateTime(LocalDate date) {
        if (this == EXISTING) {
            return date.minusDays(1).atTime(23, 59, 59);
        }
        return date.atTime(23, 59, 59);
    }

    public LocalDateTime getObservedEndDateTime(LocalDate date) {
        return date.atTime(23, 59, 59);
    }
}
