package gov.nysenate.openleg.api.spotcheck.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.spotchecks.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Contains all mismatch info except for the raw data. See MismatchView if you need the raw data.
 */
public class MismatchSummaryView<ContentKey> implements ViewObject
{

    protected int mismatchId;
    protected int reportId;
    protected ContentKey key;
    protected SpotCheckMismatchType mismatchType;
    protected MismatchState status;
    protected SpotCheckDataSource dataSource;
    protected SpotCheckContentType contentType;
    protected SpotCheckRefType referenceType;
    protected LocalDateTime referenceDateTime;
    protected String notes;
    protected LocalDateTime observedDateTime;
    protected LocalDateTime reportDateTime;
    protected LocalDateTime firstSeenDateTime;
    protected SpotCheckMismatchIgnore ignoreStatus;
    protected ListView<String> issueIds;

    public MismatchSummaryView(DeNormSpotCheckMismatch<ContentKey> mismatch) {
        this.mismatchId = mismatch.getMismatchId();
        this.reportId = mismatch.getReportId();
        this.key = mismatch.getKey();
        this.mismatchType = mismatch.getType();
        this.status = mismatch.getState();
        this.dataSource = mismatch.getDataSource();
        this.contentType = mismatch.getContentType();
        this.referenceType = mismatch.getReferenceId().getReferenceType();
        this.referenceDateTime = mismatch.getReferenceId().getRefActiveDateTime();
        this.notes = mismatch.getNotes();
        this.observedDateTime = mismatch.getObservedDateTime();
        this.firstSeenDateTime = mismatch.getFirstSeenDateTime();
        this.reportDateTime = mismatch.getReportDateTime();
        this.ignoreStatus = mismatch.getIgnoreStatus();
        this.issueIds = ListView.ofStringList(new ArrayList<>(mismatch.getIssueIds()));
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

    public MismatchState getStatus() {
        return status;
    }

    public SpotCheckDataSource getDataSource() {
        return dataSource;
    }

    public SpotCheckContentType getContentType() {
        return contentType;
    }

    public SpotCheckRefType getReferenceType() {
        return referenceType;
    }

    public LocalDateTime getReferenceDateTime() {
        return referenceDateTime;
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

    public ListView<String> getIssueIds() {
        return issueIds;
    }

    public LocalDateTime getFirstSeenDateTime() {
        return firstSeenDateTime;
    }

    @Override
    public String getViewType() {
        return "mismatch-summary";
    }
}