package gov.nysenate.openleg.model.spotcheck.senatesite;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

public class SenateSiteDumpId implements Comparable<SenateSiteDumpId> {

    private final SpotCheckRefType refType;
    private final int fragmentCount;
    private final int year;
    private final LocalDateTime dumpTime;

    public SenateSiteDumpId(SpotCheckRefType refType, int fragmentCount, int year, LocalDateTime dumpTime) {
        this.refType = refType;
        this.fragmentCount = fragmentCount;
        this.year = year;
        this.dumpTime = dumpTime;
    }

    /* --- Functional Getters --- */

    public SessionYear getSession() {
        return SessionYear.of(year);
    }

    /** Description of this dumps time range. */
    public String getNotes() {
        return "Generated from year dump: " + year;
    }

    /* --- Getters --- */

    public SpotCheckRefType getRefType() {
        return refType;
    }

    public int getFragmentCount() {
        return fragmentCount;
    }

    public int getYear() {
        return year;
    }

    public LocalDateTime getDumpTime() {
        return dumpTime;
    }

    /* --- Overrides --- */

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("refType", refType)
                .append("fragmentCount", fragmentCount)
                .append("year", year)
                .append("dumpTime", dumpTime)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SenateSiteDumpId)) return false;
        SenateSiteDumpId that = (SenateSiteDumpId) o;
        return fragmentCount == that.fragmentCount &&
                year == that.year &&
                refType == that.refType &&
                Objects.equal(dumpTime, that.dumpTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(refType, fragmentCount, year, dumpTime);
    }

    @Override
    public int compareTo(SenateSiteDumpId o) {
        return ComparisonChain.start()
                .compare(this.dumpTime, o.dumpTime)
                .compare(this.year, o.year)
                .result();
    }

}
