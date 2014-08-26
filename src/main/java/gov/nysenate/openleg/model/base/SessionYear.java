package gov.nysenate.openleg.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Simple representation of a session year. The senate has two year session periods, the start of
 * which is always on an odd numbered year. This class will perform the minimal validation necessary
 * to convert a year into its proper session year.
 */
public class SessionYear implements Serializable, Comparable<SessionYear>
{
    private static final long serialVersionUID = 4084929981265208671L;

    private static final SessionYear CURRENT = new SessionYear();

    private int year;

    /** Constructs SessionYear as current session year. */
    public SessionYear() {
        this(LocalDate.now());
    }

    public SessionYear(int year) {
        setYear(year);
    }

    public SessionYear(LocalDate localDate) {
        if (localDate == null) {
            throw new IllegalArgumentException("Supplied LocalDate cannot be null");
        }
        setYear(localDate.getYear());
    }

    public SessionYear(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("Supplied LocalDateTime cannot be null");
        }
        setYear(localDateTime.getYear());
    }

    /** Static constructors for personal preference. */

    public static SessionYear of(int year) {
        return new SessionYear(year);
    }

    public static SessionYear current() {
        return CURRENT;
    }

    /**
     * A session year will only start on odd numbered years, so an even year will have 1 year
     * subtracted from it.
     */
    private void setYear(int year) {
        this.year = (year % 2 == 0) ? year - 1 : year;
    }

    /**
     * Get the session year.
     */
    public int getYear() {
        return this.year;
    }

    /**
     * Same as {@link #getYear()}
     */
    @JsonIgnore
    public int getSessionStartYear() {
        return this.year;
    }

    /**
     * The year when the current session will end.
     * Always the year after the starting year.
     */
    @JsonIgnore
    public int getSessionEndYear() {
        return this.year + 1;
    }

    /** --- Overrides --- */

    @Override
    public int compareTo(SessionYear o) {
        return ComparisonChain.start().compare(this.year, o.year).result();
    }

    @Override
    public String toString() {
        return Integer.toString(year);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionYear)) return false;
        SessionYear that = (SessionYear) o;
        if (year != that.year) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return year;
    }
}
