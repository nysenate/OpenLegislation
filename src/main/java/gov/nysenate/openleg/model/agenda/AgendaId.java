package gov.nysenate.openleg.model.agenda;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.util.Objects;

/**
 * AgendaId is a simple wrapper used to uniquely identify an Agenda instance.
 */
public class AgendaId implements Serializable, Comparable<AgendaId>
{
    private static final long serialVersionUID = -8234649498537551140L;

    /** The agenda's calendar number. Starts at 1 at the beginning of each calendar year. */
    private long number;

    /** The year this agenda was active in. */
    private int year;

    /** --- Constructors --- */

    public AgendaId(Long number, int year) {
        this.number = number;
        this.year = year;
    }

    public AgendaId(Integer number, int year) {
        this.number = number;
        this.year = year;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return year + "-" + number;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AgendaId other = (AgendaId) obj;
        return Objects.equals(this.number, other.number) &&
               Objects.equals(this.year, other.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, year);
    }

    @Override
    public int compareTo(AgendaId o) {
        return ComparisonChain.start()
            .compare(this.getYear(), o.getYear())
            .compare(this.getNumber(), o.getNumber())
            .result();
    }

    /** --- Basic Getters/Setters --- */

    public Long getNumber() {
        return number;
    }

    public int getYear() {
        return year;
    }
}
