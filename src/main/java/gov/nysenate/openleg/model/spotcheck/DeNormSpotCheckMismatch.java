package gov.nysenate.openleg.model.spotcheck;

import org.apache.commons.lang3.StringUtils;

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

    private SpotCheckDataSource dataSource;

    /** The status of the mismatch (new, existing, etc.) */
    private SpotCheckMismatchStatus status;

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

    /** The ignore status of this mismatch. */
    private SpotCheckMismatchIgnore ignoreStatus;

    /** A list of related issue tracker ids */
    private Set<String> issueIds = new HashSet<>();

    public DeNormSpotCheckMismatch(ContentKey key, SpotCheckMismatchType mismatchType, SpotCheckDataSource dataSource) {
       this.key = key;
       this.mismatchType = mismatchType;
       this.dataSource = dataSource;
       this.status = SpotCheckMismatchStatus.NEW;
       this.ignoreStatus = SpotCheckMismatchIgnore.NOT_IGNORED;
    }

    public void setReferenceDateTime(LocalDateTime referenceDateTime) {
        this.setReferenceId(new SpotCheckReferenceId(this.getReferenceId().getReferenceType(), referenceDateTime));
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

    public void setContentType(SpotCheckContentType contentType) {
        this.contentType = contentType;
    }

    public void setIssueIds(Set<String> issueIds) {
        this.issueIds = issueIds;
    }

    public int getMismatchId() {
        return mismatchId;
    }

    public int getReportId() {
        return reportId;
    }

    public ContentKey getKey() {
        return key;
    }

    public SpotCheckMismatchType getMismatchType() {
        return mismatchType;
    }

    public SpotCheckMismatchStatus getStatus() {
        return status;
    }

    public SpotCheckDataSource getDataSource() {
        return dataSource;
    }

    public SpotCheckContentType getContentType() {
        return contentType;
    }

    public SpotCheckReferenceId getReferenceId() {
        return referenceId;
    }

    public String getReferenceData() {
        return referenceData;
    }

    public String getObservedData() {
        return observedData;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getObservedDateTime() {
        return observedDateTime;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }

    public SpotCheckMismatchIgnore getIgnoreStatus() {
        return ignoreStatus;
    }

    public Set<String> getIssueIds() {
        return issueIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeNormSpotCheckMismatch<?> mismatch = (DeNormSpotCheckMismatch<?>) o;

        if (key != null ? !key.equals(mismatch.key) : mismatch.key != null) return false;
        if (mismatchType != mismatch.mismatchType) return false;
        return dataSource == mismatch.dataSource;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (mismatchType != null ? mismatchType.hashCode() : 0);
        result = 31 * result + (dataSource != null ? dataSource.hashCode() : 0);
        return result;
    }
}
