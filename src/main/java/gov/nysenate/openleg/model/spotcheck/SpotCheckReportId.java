package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;

public class SpotCheckReportId
{
    /** The reference type used to validate data against. */
    protected SpotCheckRefType referenceType;

    /** The date that the reference was registered */
    protected LocalDateTime referenceDateTime;

    /** When this report was generated. */
    protected LocalDateTime reportDateTime;

    /** --- Constructor --- */

    public SpotCheckReportId(SpotCheckRefType referenceType, LocalDateTime reportDateTime) {
        this.referenceType = referenceType;
        this.reportDateTime = reportDateTime;
    }

    public SpotCheckReportId(SpotCheckRefType referenceType, LocalDateTime referenceDateTime, LocalDateTime reportDateTime) {
        this.referenceType = referenceType;
        this.referenceDateTime = referenceDateTime;
        this.reportDateTime = reportDateTime;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "SpotCheckReportId{" + "referenceType=" + referenceType + ", referenceDateTime=" + referenceDateTime +
                ", reportDateTime=" + reportDateTime + '}';
    }

    /** --- Basic Getters --- */

    public SpotCheckRefType getReferenceType() {
        return referenceType;
    }

    public LocalDateTime getReferenceDateTime() {
        return referenceDateTime;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }
}
