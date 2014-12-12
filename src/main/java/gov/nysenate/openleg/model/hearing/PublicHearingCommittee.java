package gov.nysenate.openleg.model.hearing;

import gov.nysenate.openleg.model.entity.Chamber;

/**
 * A Committee, Task Force, or other group
 * that can hold Public Hearings. Not necessarily a valid
 * {@link gov.nysenate.openleg.model.entity.Committee}
 */
public class PublicHearingCommittee
{
    /** The name of the PublicHearingCommittee */
    private String name;

    /** The Chamber the PublicHearingCommittee belongs to */
    private Chamber chamber;

    /** --- Basic Getters/Setters --- */

    public Chamber getChamber() {
        return chamber;
    }

    public void setChamber(Chamber chamber) {
        this.chamber = chamber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublicHearingCommittee that = (PublicHearingCommittee) o;

        if (chamber != null ? !chamber.equals(that.chamber) : that.chamber != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (chamber != null ? chamber.hashCode() : 0);
        return result;
    }
}
