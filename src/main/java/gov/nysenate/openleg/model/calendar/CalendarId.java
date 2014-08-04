package gov.nysenate.openleg.model.calendar;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.util.Objects;

/**
 * CalendarId is a simple wrapper used to uniquely identify a Calendar instance.
 */
public class CalendarId implements Serializable, Comparable<CalendarId>
{
    private static final long serialVersionUID = -3781478188305754813L;

    /** The calendar id which is scoped to a single year. */
    private int calNo;

    /** The year in which this calendar belongs to.
     *  Does not have to be the session year. */
    private int year;

    /** --- Constructors --- */

    public CalendarId(int calNo, int year) {
        this.calNo = calNo;
        this.year = year;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "{CalNo=" + calNo + ", year=" + year + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final CalendarId other = (CalendarId) obj;
        return Objects.equals(this.calNo, other.calNo) &&
               Objects.equals(this.year, other.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calNo, year);
    }

    @Override
    public int compareTo(CalendarId o) {
        return ComparisonChain.start()
            .compare(this.getYear(), o.getYear())
            .compare(this.getCalNo(), o.getCalNo())
            .result();
    }

    /** --- Basic Getters/Setters --- */

    public int getCalNo() {
        return calNo;
    }

    public int getYear() {
        return year;
    }
}
