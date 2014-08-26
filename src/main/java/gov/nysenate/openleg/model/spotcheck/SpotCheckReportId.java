package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;

public class SpotCheckReportId
{
    /** The reference type used to validate data against. */
    protected SpotCheckRefType referenceType;

    /** When this report was generated. */
    protected LocalDateTime reportDateTime;

    /** --- Constructor --- */

    public SpotCheckReportId(SpotCheckRefType referenceType, LocalDateTime reportDateTime) {
        this.referenceType = referenceType;
        this.reportDateTime = reportDateTime;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "SpotCheckReportId{" + "referenceType=" + referenceType + ", reportDateTime=" + reportDateTime + '}';
    }

    /** --- Basic Getters --- */

    public SpotCheckRefType getReferenceType() {
        return referenceType;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }
}
