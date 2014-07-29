package gov.nysenate.openleg.model.agenda;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import java.io.Serializable;

/**
 * AgendaId is a simple wrapper used to uniquely identify an Agenda instance.
 */
public class AgendaId implements Serializable, Comparable<AgendaId>
{
    private static final long serialVersionUID = -8234649498537551140L;

    /** The agenda's calendar number. Starts at 1 at the beginning of each calendar year. */
    private int number;

    /** The year this agenda was active in. */
    private int year;

    /** --- Constructors --- */

    public AgendaId(Integer number, int year) {
        this.number = number;
        this.year = year;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("number", number)
            .add("year", year)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgendaId)) return false;
        AgendaId agendaId = (AgendaId) o;
        if (number != agendaId.number) return false;
        if (year != agendaId.year) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + year;
        return result;
    }

    @Override
    public int compareTo(AgendaId o) {
        return ComparisonChain.start()
            .compare(this.getYear(), o.getYear())
            .compare(this.getNumber(), o.getNumber())
            .result();
    }

    /** --- Basic Getters/Setters --- */

    public Integer getNumber() {
        return number;
    }

    public int getYear() {
        return year;
    }
}
