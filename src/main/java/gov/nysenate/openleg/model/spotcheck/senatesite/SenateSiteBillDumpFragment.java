package gov.nysenate.openleg.model.spotcheck.senatesite;

import java.time.LocalDateTime;

public class SenateSiteBillDumpFragment {

    protected LocalDateTime fromDateTime;
    protected LocalDateTime toDateTime;

    protected int sequenceNumber;
    protected int totalFragmentCount;

    /** --- Functional Getters --- */

    public SenateSiteBillDumpId getBillDumpId() {
        return new SenateSiteBillDumpId(fromDateTime, toDateTime, totalFragmentCount);
    }

    /** --- Getters / Setters --- */

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getTotalFragmentCount() {
        return totalFragmentCount;
    }
}
