package gov.nysenate.openleg.legislation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ComparisonChain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Simple representation of a session year. The senate has two year session periods, the start of
 * which is always on an odd numbered year. This class will perform the minimal validation necessary
 * to convert a year into its proper session year.
 */
public record SessionYear(int year) implements Serializable, Comparable<SessionYear> {
    @Serial
    private static final long serialVersionUID = 4084929981265208671L;
    private static final int senateStartYear = 1777;

    public SessionYear(int year) {
        int computedSession = (year % 2 == 0) ? year - 1 : year;
        if (computedSession < senateStartYear) {
            throw new BadSessionYearException(year, senateStartYear);
        }
        this.year = computedSession;
    }

    /**
     * Return a new SessionYear representing the previous session.
     */
    public SessionYear previousSessionYear() {
        return new SessionYear(year - 2);
    }

    /**
     * Return a new SessionYear representing the next session.
     */
    public SessionYear nextSessionYear() {
        return new SessionYear(year + 2);
    }

    // Static constructors for personal preference.
    public static SessionYear of(int year) {
        return new SessionYear(year);
    }

    public static SessionYear current() {
        return new SessionYear(LocalDate.now().getYear());
    }

    /**
     * @return The LocalDateTime representing the start of this session year.
     */
    @JsonIgnore
    public LocalDateTime getStartDateTime() {
        return LocalDate.ofYearDay(year, 1).atStartOfDay();
    }

    @Override
    public String toString() {
        return String.valueOf(year);
    }

    @Override
    public int compareTo(SessionYear o) {
        return ComparisonChain.start().compare(this.year, o.year).result();
    }
}
