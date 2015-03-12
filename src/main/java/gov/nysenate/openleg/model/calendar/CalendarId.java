package gov.nysenate.openleg.model.calendar;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * CalendarId is a simple wrapper used to uniquely identify a Calendar instance.
 */
public class CalendarId implements Serializable, Comparable<CalendarId>
{
    private static final long serialVersionUID = -3781478188305754813L;

    /** The calendar id which is scoped to a single year. */
    protected int calNo;

    /** The year in which this calendar belongs to.
     *  Does not have to be the session year. */
    protected int year;

    public static final Pattern calendarIdPattern = Pattern.compile("(\\d{4})#(\\d+)");

    /** --- Constructors --- */

    public CalendarId(int calNo, int year) {
        this.calNo = calNo;
        this.year = year;
    }

    public CalendarId(CalendarId calendarId) {
        this(calendarId.getCalNo(), calendarId.getYear());
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "#" + calNo + " (" + year + ')';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof CalendarId)) return false;
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
