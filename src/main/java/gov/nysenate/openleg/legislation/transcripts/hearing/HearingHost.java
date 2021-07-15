package gov.nysenate.openleg.legislation.transcripts.hearing;

import gov.nysenate.openleg.legislation.committee.Chamber;

import java.util.Objects;

/**
 * Contains data about what group is hosting a public hearing.
 */
public class HearingHost {
    private static final String IRRELEVANT_TEXT = "^(\\s?(ON|FOR|THE|AND))+|((,|THE|AND|;)\\s?)+$";
    private final Chamber chamber;
    private final HearingHostType type;
    private final String name;

    public HearingHost(Chamber chamber, HearingHostType type, String name) {
        this.chamber = chamber;
        this.type = type;
        this.name = name.toUpperCase().replaceAll("\\s+", " ").replaceAll(", AND| &", " AND")
                .replaceAll(IRRELEVANT_TEXT, "").trim();
    }

    /** --- Basic Getters/Setters --- */

    public Chamber getChamber() {
        return chamber;
    }

    public HearingHostType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HearingHost that = (HearingHost) o;
        return chamber == that.chamber && type == that.type && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chamber, type, name);
    }

    @Override
    public String toString() {
        return "HearingHost{chamber=" + chamber + ", type=" + type +
                ", name='" + name + '\'' + '}';
    }
}
