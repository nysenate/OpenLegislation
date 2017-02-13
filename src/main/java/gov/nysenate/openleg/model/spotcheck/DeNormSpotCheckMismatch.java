package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class DeNormSpotCheckMismatch<ContentKey> {

    /** An integer id that uniquely identifies this mismatch */
    private int mismatchId;

    /** Identifies the report this mismatch is associated with. */
    private int reportId;

    /** A key that identifies the content being checked. */
    private ContentKey key;

    /** The type of mismatch that occurred. */
    private SpotCheckMismatchType mismatchType;

    /** The status of the mismatch (new, existing, etc.) */
    private SpotCheckMismatchStatus status = SpotCheckMismatchStatus.NEW;

    private SpotCheckDataSource dataSource;

    private SpotCheckContentType contentType;

    /** The source used to compare our data against. */
    private SpotCheckReferenceId referenceId;

    /** String representation of the reference data. (e.g. lbdc daybreak content) */
    private String referenceData;

    /** String representation of the observed data (typically openleg processed content) */
    private String observedData;

    /** Any details about this mismatch. (Optional) */
    private String notes;

    /** The datetime this observation was made. */
    private LocalDateTime observedDateTime;

    /** The date time when the report that generated this observation was run */
    private LocalDateTime reportDateTime;

    /** The ignore status of this mismatch. (Optional) */
    private SpotCheckMismatchIgnore ignoreStatus;

    /** A list of related issue tracker ids */
    private Set<String> issueIds = new HashSet<>();

    public DeNormSpotCheckMismatch(ContentKey key, SpotCheckMismatchType mismatchType) {
       this.key = key;
       this.mismatchType = mismatchType;
    }

    public void setMismatchId(int mismatchId) {
        this.mismatchId = mismatchId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public void setStatus(SpotCheckMismatchStatus status) {
        this.status = status;
    }

    public void setReferenceId(SpotCheckReferenceId referenceId) {
        this.referenceId = referenceId;
    }

    public void setReferenceData(String referenceData) {
        this.referenceData = referenceData;
    }

    public void setObservedData(String observedData) {
        this.observedData = observedData;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setObservedDateTime(LocalDateTime observedDateTime) {
        this.observedDateTime = observedDateTime;
    }

    public void setReportDateTime(LocalDateTime reportDateTime) {
        this.reportDateTime = reportDateTime;
    }

    public void setIgnoreStatus(SpotCheckMismatchIgnore ignoreStatus) {
        this.ignoreStatus = ignoreStatus;
    }

    public void setDataSource(SpotCheckDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setContentType(SpotCheckContentType contentType) {
        this.contentType = contentType;
    }

    public void setIssueIds(Set<String> issueIds) {
        this.issueIds = issueIds;
    }
}
