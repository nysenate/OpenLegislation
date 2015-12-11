package gov.nysenate.openleg.model.spotcheck.senatesite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Range;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Identifies a senate site bill dump via the referenced update date times
 * and total fragment count
 */
public class SenateSiteBillDumpId implements Serializable, Comparable<SenateSiteBillDumpId> {

    private static final long serialVersionUID = 6581598432572888768L;

    @JsonProperty("from")
    protected LocalDateTime fromDateTime;
    @JsonProperty("to")
    protected LocalDateTime toDateTime;

    @JsonProperty("totalParts")
    protected int fragmentCount;

    // Protected default constructor for serialization
    protected SenateSiteBillDumpId() {}

    public SenateSiteBillDumpId(SenateSiteBillDumpId other) {
        this.fromDateTime = other.fromDateTime;
        this.toDateTime = other.toDateTime;
        this.fragmentCount = other.fragmentCount;
    }

    /** --- Functional Getters / Setters --- */

    /**
     * @return Range<LocalDateTime> - the update datetime range used to generate this dump
     */
    public Range<LocalDateTime> getUpdateInterval() {
        return Range.open(fromDateTime, toDateTime);
    }

    /** --- Overridden Methods --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SenateSiteBillDumpId)) return false;
        SenateSiteBillDumpId that = (SenateSiteBillDumpId) o;
        return fragmentCount == that.fragmentCount &&
                Objects.equal(fromDateTime, that.fromDateTime) &&
                Objects.equal(toDateTime, that.toDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fromDateTime, toDateTime, fragmentCount);
    }

    @Override
    public int compareTo(SenateSiteBillDumpId o) {
        return ComparisonChain.start()
                .compare(this.toDateTime, o.toDateTime)
                .compare(this.fromDateTime, o.fromDateTime)
                .result();
    }

    /** --- Getters --- */

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    public int getFragmentCount() {
        return fragmentCount;
    }
}
