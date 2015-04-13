package gov.nysenate.openleg.model.agenda.reference;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.CommitteeId;

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

    /** --- Getters / Setters --- */

    public LocalDateTime getReferenceDateTime() {
        return referenceDateTime;
    }

    public LocalDate getWeekOf() {
        return weekOf;
    }
}
