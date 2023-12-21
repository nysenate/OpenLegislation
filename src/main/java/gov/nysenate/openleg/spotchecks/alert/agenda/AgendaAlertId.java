package gov.nysenate.openleg.spotchecks.alert.agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A unique identifier for an agenda alert
 *
 * @param referenceDateTime The date that the alert was sent
 * @param weekOf            The week of the agenda
 */
public record AgendaAlertId(LocalDateTime referenceDateTime, LocalDate weekOf) {}
