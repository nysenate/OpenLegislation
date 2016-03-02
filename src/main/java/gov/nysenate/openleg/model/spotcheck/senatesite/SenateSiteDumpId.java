package gov.nysenate.openleg.model.spotcheck.senatesite;

import com.google.common.collect.Range;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;

import java.time.LocalDateTime;

public abstract class SenateSiteDumpId {

    protected final SpotCheckRefType refType;
    protected final int fragmentCount;

    protected SenateSiteDumpId(SpotCheckRefType refType, int fragmentCount) {
        this.refType = refType;
        this.fragmentCount = fragmentCount;
    }

    /** Abstract Methods */

    /** Return the date time range of this dump. */
    public abstract Range<LocalDateTime> getRange();

    /** Basic get/set methods */

    public SpotCheckRefType getRefType() {
        return refType;
    }

    public int getFragmentCount() {
        return fragmentCount;
    }

    @Override
    public String toString() {
        return "SenateSiteDumpId{" +
               "refType=" + refType +
               ", fragmentCount=" + fragmentCount +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SenateSiteDumpId dumpId = (SenateSiteDumpId) o;

        if (fragmentCount != dumpId.fragmentCount) return false;
        return refType == dumpId.refType;
    }

    @Override
    public int hashCode() {
        int result = refType != null ? refType.hashCode() : 0;
        result = 31 * result + fragmentCount;
        return result;
    }
}
