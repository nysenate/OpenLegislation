package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MismatchView<ContentKey> implements ViewObject
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
    protected String referenceData;
    protected String observedData;
    protected String notes;
    protected LocalDateTime observedDateTime;
    protected LocalDateTime reportDateTime;
    protected SpotCheckMismatchIgnore ignoreStatus;
    protected ListView<String> issueIds;

    public MismatchView(DeNormSpotCheckMismatch<ContentKey> mismatch) {
        this.mismatchId = mismatch.getMismatchId();
        this.reportId = mismatch.getReportId();
        this.key = mismatch.getKey();
        this.mismatchType = mismatch.getType();
        this.status = mismatch.getState();
        this.dataSource = mismatch.getDataSource();
        this.contentType = mismatch.getContentType();
        this.referenceType = mismatch.getReferenceId().getReferenceType();
        this.referenceDateTime = mismatch.getReferenceId().getRefActiveDateTime();
        this.referenceData = mismatch.getReferenceData();
        this.observedData = mismatch.getObservedData();
        this.notes = mismatch.getNotes();
        this.observedDateTime = mismatch.getObservedDateTime();
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

    public ListView<String> getIssueIds() {
        return issueIds;
    }

    @Override
    public String getViewType() {
        return "mismatch";
    }
}