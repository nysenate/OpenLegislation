package gov.nysenate.openleg.spotchecks.alert.agenda;

import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.CommitteeId;

import java.time.LocalDate;
import java.util.Objects;

/**
 * An alternative agenda/commitee meeting id that uses the first day of the agenda week instead of year/number.
 *
 * @see AgendaId
 * @see CommitteeAgendaAddendumId
 * Notably used as the ContentKey in Agenda Alert Spotchecks.
 */
public record AgendaMeetingWeekId(int year, LocalDate weekOf, Version addendum, CommitteeId committeeId) {
    public AgendaMeetingWeekId {
        Objects.requireNonNull(weekOf);
        Objects.requireNonNull(addendum);
    }
}
