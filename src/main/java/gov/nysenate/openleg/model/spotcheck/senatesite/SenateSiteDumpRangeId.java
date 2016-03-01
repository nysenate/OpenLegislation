package gov.nysenate.openleg.model.spotcheck.senatesite;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Range;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Identifies a Senate Site dump that was generated with to and from timestamps.
 */
public class SenateSiteDumpRangeId extends SenateSiteDumpId implements Serializable, Comparable<SenateSiteDumpRangeId> {

    private static final long serialVersionUID = 6311044922519172423L;

    protected final LocalDateTime fromDateTime;
    protected final LocalDateTime toDateTime;

    public SenateSiteDumpRangeId(SpotCheckRefType refType, int fragmentCount, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        super(refType, fragmentCount);
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
    }

    @Override
    public Range<LocalDateTime> getRange() {
        return Range.closed(fromDateTime, toDateTime);
    }

    @Override
    public int compareTo(SenateSiteDumpRangeId o) {
        return ComparisonChain.start()
                              .compare(this.toDateTime, o.toDateTime)
                              .compare(this.fromDateTime, o.fromDateTime)
                              .result();
    }

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    @Override
    public String toString() {
        return "SenateSiteDumpRangeId{" +
               "fromDateTime=" + fromDateTime +
               ", toDateTime=" + toDateTime +
               '}';
    }
}
