package gov.nysenate.openleg.model.spotcheck.agenda;

import com.google.common.base.Objects;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A unique identifier for an agenda alert */
public class AgendaAlertId {

    /** The date that the alert was sent */
    protected LocalDateTime referenceDateTime;

    /** The week of the agenda */
    protected LocalDate weekOf;

    /** --- Constructors --- */

    public AgendaAlertId(LocalDateTime referenceDateTime, LocalDate weekOf) {
        this.referenceDateTime = referenceDateTime;
        this.weekOf = weekOf;
    }

    /** --- Overridden Methods --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgendaAlertId)) return false;
        AgendaAlertId that = (AgendaAlertId) o;
        return Objects.equal(referenceDateTime, that.referenceDateTime) &&
                Objects.equal(weekOf, that.weekOf);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(referenceDateTime, weekOf);
    }

    @Override
    public String toString() {
        return "weekOf:" + weekOf + " refDate:" + referenceDateTime;
    }

    /** --- Getters / Setters --- */

    public LocalDateTime getReferenceDateTime() {
        return referenceDateTime;
    }

    public LocalDate getWeekOf() {
        return weekOf;
    }
}
