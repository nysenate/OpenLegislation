package gov.nysenate.openleg.model.spotcheck.senatesite;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class SenateSiteBillDumpFragId extends SenateSiteBillDumpId {

    @JsonProperty("part")
    protected int sequenceNo;

    // Protected default constructor for serialization
    protected SenateSiteBillDumpFragId() {}

    public SenateSiteBillDumpFragId(SenateSiteBillDumpFragId other) {
        super(other);
        this.sequenceNo = other.sequenceNo;
    }

    /** --- Getters --- */

    public int getSequenceNo() {
        return sequenceNo;
    }
}
