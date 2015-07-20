package gov.nysenate.openleg.model.spotcheck;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.spotcheck.MismatchOrderBy;

import java.time.LocalDateTime;
import java.util.Collection;

/** Contains query parameters used to poll for open spotcheck mismatches */
public class OpenMismatchQuery {

    /** The type of report to query from */
    SpotCheckRefType refType;
    /** A set of mismatch types to filter by */
    Collection<SpotCheckMismatchType> mismatchTypes;
    /** Get mismatches observed on or after this date */
    LocalDateTime observedAfter;
    /** Order mismatches by this mismatch parameter */
    MismatchOrderBy orderBy;
    /** in ascending or descending sort order */
    SortOrder order;
    /** Limit offset */
    LimitOffset limitOffset;
    /** Show resolved/ignored mismatches */
    boolean resolvedShown;
    boolean ignoredShown;

    /** --- Constructors --- */

    public OpenMismatchQuery(SpotCheckRefType refType, Collection<SpotCheckMismatchType> mismatchTypes,
                             LocalDateTime observedAfter, MismatchOrderBy orderBy, SortOrder order,
                             LimitOffset limitOffset, boolean resolvedShown, boolean ignoredShown) {
        this.refType = refType;
        this.mismatchTypes = mismatchTypes;
        this.observedAfter = observedAfter;
        this.orderBy = orderBy;
        this.order = order;
        this.limitOffset = limitOffset;
        this.resolvedShown = resolvedShown;
        this.ignoredShown = ignoredShown;
    }

    /** --- Functional Getters --- */

    @JsonIgnore
    public OrderBy getFullOrderBy() {
        return orderBy != null ? orderBy.toOrderBy(order) : new OrderBy();
    }

    /** --- Getters --- */

    public SpotCheckRefType getRefType() {
        return refType;
    }

    public Collection<SpotCheckMismatchType> getMismatchTypes() {
        return mismatchTypes;
    }

    public LocalDateTime getObservedAfter() {
        return observedAfter;
    }

    public MismatchOrderBy getOrderBy() {
        return orderBy;
    }

    public SortOrder getOrder() {
        return order;
    }

    public LimitOffset getLimitOffset() {
        return limitOffset;
    }

    public boolean isResolvedShown() {
        return resolvedShown;
    }

    public boolean isIgnoredShown() {
        return ignoredShown;
    }
}
