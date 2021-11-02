package gov.nysenate.openleg.legislation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Simple representation of a session year. The senate has two year session periods, the start of
 * which is always on an odd numbered year. This class will perform the minimal validation necessary
 * to convert a year into its proper session year.
 */
public class SessionYear implements Serializable, Comparable<SessionYear>
{
    @Serial
    private static final long serialVersionUID = 4084929981265208671L;

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

    /**
     * Return a new SessionYear representing the previous session.
     */
    public SessionYear prev() {
        return new SessionYear(getSessionStartYear() - 1);
    }

    /**
     * Return a new SessionYear representing the next session.
     */
    public SessionYear next() {
        return new SessionYear(getSessionEndYear() + 1);
    }

    /** Static constructors for personal preference. */

    public static SessionYear of(int year) {
        return new SessionYear(year);
    }

    public static SessionYear of(LocalDate localDate) {
        return SessionYear.of(localDate.getYear());
    }

    public static SessionYear of(LocalDateTime localDateTime) {
        return SessionYear.of(localDateTime.getYear());
    }

    public static SessionYear current() {
        return new SessionYear();
    }

    /**
     * A session year will only start on odd numbered years, so an even year will have 1 year
     * subtracted from it.
     */
    private void setYear(int year) {
        int computedSession = (year % 2 == 0) ? year - 1 : year;
        if (computedSession < 0) {
            throw new IllegalArgumentException("Session year cannot be negative! " +
                    "(" + computedSession + " computed from " + year + ")");
        }
        this.year = computedSession;
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

    /** Returns a date range encompassing this session */
    @JsonIgnore
    public Range<LocalDate> asDateRange() {
        return Range.closed(LocalDate.of(getSessionStartYear(), 1, 1), LocalDate.of(getSessionEndYear(), 12, 31));
    }

    /** Returns a date time range encompassing this session. */
    @JsonIgnore
    public Range<LocalDateTime> asDateTimeRange() {
        return Range.closed(LocalDateTime.of(getSessionStartYear(), 1, 1, 0, 0, 0),
                            LocalDateTime.of(getSessionEndYear(), 12, 31, 23, 59, 59));
    }

    /** Returns a sorted list containing the years of the session */
    @JsonIgnore
    public List<Integer> asYearList() {
        return Lists.newArrayList(this.year, this.year + 1);
    }

    /**
     * @return The LocalDateTime representing the start of this session year.
     */
    @JsonIgnore
    public LocalDateTime getStartDateTime() {
        return this.asDateTimeRange().lowerEndpoint();
    }

    /**
     * @return The LocalDateTime representing the end of this session year.
     */
    @JsonIgnore
    public LocalDateTime getEndDateTime() {
        return this.asDateTimeRange().upperEndpoint();
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
        if (!(o instanceof SessionYear that)) return false;
        return year == that.year;
    }

    @Override
    public int hashCode() {
        return year;
    }
}
