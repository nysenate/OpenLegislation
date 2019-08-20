package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.spotcheck.MismatchOrderBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * Contains query parameters used to query for spotcheck mismatches.
 * Provides reasonable defaults for non required parameters.
 */
public class MismatchQuery {

    private LocalDate reportDate;
    private SpotCheckDataSource dataSource;
    private Set<SpotCheckContentType> contentTypes;
    private MismatchStatus status;
    private Set<SpotCheckMismatchType> mismatchTypes;
    private Set<SpotCheckMismatchIgnore> ignoredStatuses;
    private Multimap<SpotCheckContentType, Object> keys = null;

    private OrderBy orderBy;

    public MismatchQuery(LocalDate reportDate, SpotCheckDataSource dataSource,
                         MismatchStatus status, Set<SpotCheckContentType> contentTypes) {
        this.reportDate = reportDate;
        this.dataSource = dataSource;
        this.status = status;
        this.contentTypes = contentTypes;
        // Default values
        this.mismatchTypes = EnumSet.allOf(SpotCheckMismatchType.class);
        this.ignoredStatuses = EnumSet.of(SpotCheckMismatchIgnore.NOT_IGNORED);
        this.orderBy = new OrderBy(MismatchOrderBy.REFERENCE_DATE.getColumnName(), SortOrder.DESC);
    }

    /* --- Functional Getters --- */

    public MismatchState getState() {
        return status.getState();
    }

    public LocalDateTime getObservedStartDateTime() {
        return status.getObservedStartDateTime(reportDate);
    }

    public LocalDateTime getFirstSeenStartDateTime() {
        return status.getFirstSeenStartDateTime(reportDate);
    }

    public LocalDateTime getFirstSeenEndDateTime() {
        return status.getFirstSeenEndDateTime(reportDate);
    }

    public LocalDateTime getObservedEndDateTime() {
        return status.getObservedEndDateTime(reportDate);
    }

    public boolean isFilteringKeys() {
        return keys != null && !keys.isEmpty();
    }

    /* --- Builder Methods --- */

    public MismatchQuery withIgnoredStatuses(Set<SpotCheckMismatchIgnore> ignoredStatuses) {
        this.ignoredStatuses = ignoredStatuses;
        return this;
    }

    public MismatchQuery withOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public MismatchQuery withMismatchTypes(EnumSet<SpotCheckMismatchType> mismatchTypes){
        this.mismatchTypes = mismatchTypes;
        return this;
    }

    public MismatchQuery withKeys(SpotCheckContentType contentType, Collection keys) {
        if (this.keys == null) {
            this.keys = HashMultimap.create();
        }
        this.keys.putAll(contentType, keys);
        return this;
    }

    /* --- Getters --- */

    public LocalDate getReportDate() {
        return reportDate;
    }

    public SpotCheckDataSource getDataSource() {
        return dataSource;
    }

    public Set<SpotCheckContentType> getContentTypes() {
        return contentTypes;
    }

    public MismatchStatus getStatus() {
        return status;
    }

    public Set<SpotCheckMismatchType> getMismatchTypes() {
        return mismatchTypes;
    }

    public Set<SpotCheckMismatchIgnore> getIgnoredStatuses() {
        return ignoredStatuses;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public Multimap<SpotCheckContentType, Object> getKeys() {
        return keys;
    }
}
