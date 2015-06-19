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
    LocalDateTime earliestObserved;
    /** Order mismatches by this mismatch parameter */
    MismatchOrderBy mismatchOrderBy;
    /** in ascending or descending sort order */
    SortOrder order;
    /** Limit offset */
    LimitOffset limitOffset;
    /** Show resolved/ignored mismatches */
    boolean resolvedShown;
    boolean ignoredShown;

    /** --- Constructors --- */

    public OpenMismatchQuery(SpotCheckRefType refType, Collection<SpotCheckMismatchType> mismatchTypes,
                             LocalDateTime earliestObserved, MismatchOrderBy mismatchOrderBy, SortOrder order,
                             LimitOffset limitOffset, boolean resolvedShown, boolean ignoredShown) {
        this.refType = refType;
        this.mismatchTypes = mismatchTypes;
        this.earliestObserved = earliestObserved;
        this.mismatchOrderBy = mismatchOrderBy;
        this.order = order;
        this.limitOffset = limitOffset;
        this.resolvedShown = resolvedShown;
        this.ignoredShown = ignoredShown;
    }

    /** --- Functional Getters --- */

    @JsonIgnore
    public OrderBy getOrderBy() {
        return mismatchOrderBy != null ? mismatchOrderBy.toOrderBy(order) : new OrderBy();
    }

    /** --- Getters --- */

    public SpotCheckRefType getRefType() {
        return refType;
    }

    public Collection<SpotCheckMismatchType> getMismatchTypes() {
        return mismatchTypes;
    }

    public LocalDateTime getEarliestObserved() {
        return earliestObserved;
    }

    public MismatchOrderBy getMismatchOrderBy() {
        return mismatchOrderBy;
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
