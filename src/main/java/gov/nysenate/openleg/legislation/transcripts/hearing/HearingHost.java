package gov.nysenate.openleg.legislation.transcripts.hearing;

import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.Committee;

import java.util.Objects;

/**
 * A Committee, Task Force, or other group that can hold Public Hearings.
 * Not necessarily a valid {@link Committee}.
 */
public class HearingHost {
    private final Chamber chamber;
    private final HearingHostType type;
    private final String name;

    public HearingHost(String chamber, HearingHostType type, String name) {
        this.chamber = Chamber.getValue(chamber);
        this.type = type;
        // TODO: test these
        this.name = name.replaceAll("(?i)^ *and,? *|(,|AND)\\s*$", "").replaceAll("\\s+", " ")
                .replaceAll(", AND| &", " AND").trim();
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
