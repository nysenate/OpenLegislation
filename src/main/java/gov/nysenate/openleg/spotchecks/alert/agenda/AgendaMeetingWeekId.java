package gov.nysenate.openleg.spotchecks.alert.agenda;

import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.Chamber;
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
public class AgendaMeetingWeekId {

    private final int year;
    /** The starting day of the week of the referenced agenda */
    private final LocalDate weekOf;
    private final Version addendum;
    private final CommitteeId committeeId;

    public AgendaMeetingWeekId(int year, LocalDate weekOf, Version addendum, Chamber chamber, String committeeName) {
        this.year = year;
        this.weekOf = Objects.requireNonNull(weekOf);
        this.addendum = Objects.requireNonNull(addendum);
        this.committeeId = new CommitteeId(chamber, committeeName);
    }

    public int getYear() {
        return year;
    }

    public LocalDate getWeekOf() {
        return weekOf;
    }

    public Version getAddendum() {
        return addendum;
    }

    public CommitteeId getCommitteeId() {
        return committeeId;
    }

    @Override
    public String toString() {
        return getCommitteeId() + " " + getAddendum().name() + " weekOf:" + getWeekOf().toString() + " year:" + year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendaMeetingWeekId that = (AgendaMeetingWeekId) o;
        return year == that.year &&
                Objects.equals(weekOf, that.weekOf) &&
                addendum == that.addendum &&
                Objects.equals(committeeId, that.committeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, weekOf, addendum, committeeId);
    }
}
