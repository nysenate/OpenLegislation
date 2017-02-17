package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.model.base.SessionYear;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

/** Contains query parameters used to query for spotcheck mismatches */
public class MismatchQuery {

    private SpotCheckDataSource dataSource;
    private Set<SpotCheckContentType> contentTypes;
    private Set<SpotCheckMismatchStatus> mismatchStatuses;
    private Set<SpotCheckMismatchIgnore> ignoredStatuses;
    private LocalDateTime toDate;
    private LocalDateTime fromDate;

    public MismatchQuery(SpotCheckDataSource dataSource, Set<SpotCheckContentType> contentTypes) {
        this.dataSource = dataSource;
        this.contentTypes = contentTypes;
        // Default values
        this.mismatchStatuses = EnumSet.of(SpotCheckMismatchStatus.NEW, SpotCheckMismatchStatus.EXISTING, SpotCheckMismatchStatus.REGRESSION);
        this.ignoredStatuses = EnumSet.of(SpotCheckMismatchIgnore.NOT_IGNORED);
        this.toDate = LocalDateTime.now();
        this.fromDate = SessionYear.of(this.toDate.getYear()).asDateTimeRange().lowerEndpoint();
    }

    public MismatchQuery withMismatchStatuses(Set<SpotCheckMismatchStatus> mismatchStatuses) {
        this.mismatchStatuses = mismatchStatuses;
        return this;
    }

    public MismatchQuery withIgnoredStatuses(Set<SpotCheckMismatchIgnore> ignoredStatuses) {
        this.ignoredStatuses = ignoredStatuses;
        return this;
    }

    public MismatchQuery withToDate(LocalDateTime toDate) {
        this.toDate = toDate;
        return this;
    }

    public MismatchQuery withFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public SpotCheckDataSource getDataSource() {
        return dataSource;
    }

    public Set<SpotCheckContentType> getContentTypes() {
        return contentTypes;
    }

    public Set<SpotCheckMismatchStatus> getMismatchStatuses() {
        return mismatchStatuses;
    }

    public Set<SpotCheckMismatchIgnore> getIgnoredStatuses() {
        return ignoredStatuses;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }
}
