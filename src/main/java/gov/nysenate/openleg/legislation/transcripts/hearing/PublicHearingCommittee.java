package gov.nysenate.openleg.legislation.transcripts.hearing;

import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.Committee;

import java.util.Objects;

/**
 * A Committee, Task Force, or other group that can hold Public Hearings.
 * Not necessarily a valid {@link Committee}.
 */
public class PublicHearingCommittee {
    private final String name;
    private final Chamber chamber;

    public PublicHearingCommittee(String name, String chamber) {
        this.name = name.trim();
        this.chamber = Chamber.getValue(chamber);
    }

    /** --- Basic Getters/Setters --- */

    public Chamber getChamber() {
        return chamber;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicHearingCommittee that = (PublicHearingCommittee) o;
        return Objects.equals(chamber, that.chamber) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, chamber);
    }

    @Override
    public String toString() {
        return "PublicHearingCommittee{" +
               "name='" + name + '\'' +
               ", chamber=" + chamber +
               '}';
    }
}
