package gov.nysenate.openleg.spotchecks.sensite;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReferenceId;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

public record SenateSiteDumpId(SpotCheckRefType refType, int fragmentCount, int year,
                               LocalDateTime dumpTime) implements Comparable<SenateSiteDumpId> {
    /* --- Functional Getters --- */

    public SpotCheckReferenceId getReferenceId() {
        return new SpotCheckReferenceId(refType, dumpTime);
    }

    public SessionYear getSession() {
        return SessionYear.of(year);
    }

    /** Description of this dumps' time range. */
    public String getNotes() {
        return "Generated from year dump: " + year;
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
    public int compareTo(SenateSiteDumpId o) {
        return ComparisonChain.start()
                .compare(this.dumpTime, o.dumpTime)
                .compare(this.year, o.year)
                .result();
    }

}
