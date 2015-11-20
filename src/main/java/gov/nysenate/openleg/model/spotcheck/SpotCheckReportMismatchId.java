package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;

/**
 * Identifies a single spotcheck mismatch
 */
public class SpotCheckReportMismatchId<ContentId> extends SpotCheckMismatchId<ContentId> {

    protected LocalDateTime reportDateTime;

    public SpotCheckReportMismatchId(SpotCheckRefType refType, ContentId contentId, SpotCheckMismatchType mismatchType,
                                     LocalDateTime reportDateTime) {
        super(refType, contentId, mismatchType);
        this.reportDateTime = reportDateTime;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }
}
