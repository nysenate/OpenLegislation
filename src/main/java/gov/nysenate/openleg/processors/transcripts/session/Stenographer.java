package gov.nysenate.openleg.processors.transcripts.session;

import com.google.common.collect.ImmutableSortedSet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contains information about which Stenographer wrote different Transcripts.
 */
public enum Stenographer {
    KIRKLAND(LocalDate.of(2011, 5, 16), "Kirkland Reporting Service"),
    CANDYCO1(2005, "Candyco Transcription Service, Inc."),
    NONE(2004, ""), CANDYCO2(1999, CANDYCO1.name),
    WILLIMAN(1993, "Pauline Williman, Certified Shorthand Reporter");

    private static final ImmutableSortedSet<Stenographer> sortedByStartDate =
            ImmutableSortedSet.copyOf((o1, o2) -> o2.start.compareTo(o1.start), List.of(values()));
    private final LocalDateTime start;
    private final String name;

    Stenographer(int year, String name) {
        this(LocalDate.of(year, 1, 1), name);
    }

    Stenographer(LocalDate start, String name) {
        this.start = start.atStartOfDay();
        this.name = name;
    }

    /**
     * Gets the name of the stenographer who wrote at that time.
     * @param ldt DateTime of transcript.
     * @return their name.
     */
    public static String getStenographer(LocalDateTime ldt) {
        for (Stenographer s : sortedByStartDate) {
            if (s.start.isBefore(ldt) || s.start.equals(ldt))
                return s.name;
        }
        throw new IllegalArgumentException("There should be a Stenographer for every DateTime.");
    }

    public String getName() {
        return name;
    }
}
