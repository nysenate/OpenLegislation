package gov.nysenate.openleg.model.spotcheck.agenda;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.Chamber;

import java.time.LocalDate;
import java.util.Objects;

/**
 * An Id used as the ContentKey in Agenda Alert Spotchecks.
 *
 * The fields chosen for this id are available in both Openleg Agendas
 * and Agenda Alerts.
 */
public class AgendaAlertCheckId {

    private final int year;
    /** The starting day of the week of the referenced agenda */
    private final LocalDate weekOf;
    private final Version addendum;
    private final Chamber chamber;
    private final String committeeName;

    public AgendaAlertCheckId(int year, LocalDate weekOf, Version addendum, Chamber chamber, String committeeName) {
        this.year = Objects.requireNonNull(year);
        this.weekOf = Objects.requireNonNull(weekOf);
        this.addendum = Objects.requireNonNull(addendum);
        this.chamber = Objects.requireNonNull(chamber);
        this.committeeName = Objects.requireNonNull(committeeName);
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

    public Chamber getChamber() {
        return chamber;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    @Override
    public String toString() {
        return getChamber() + "-" + getCommitteeName() + " " + getAddendum().name() + " weekOf:" + getWeekOf().toString() + " year:" + year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendaAlertCheckId that = (AgendaAlertCheckId) o;
        return year == that.year &&
                Objects.equals(weekOf, that.weekOf) &&
                addendum == that.addendum &&
                chamber == that.chamber &&
                Objects.equals(committeeName, that.committeeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, weekOf, addendum, chamber, committeeName);
    }
}
