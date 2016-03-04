package gov.nysenate.openleg.model.spotcheck;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.spotcheck.MismatchOrderBy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

/** Contains query parameters used to poll for open spotcheck mismatches */
public class OpenMismatchQuery {

    /** The type of reports to query from */
    Set<SpotCheckRefType> refTypes;
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
    /** Get only ignored mismatches.  This overrides ignoredShown if that is false */
    boolean ignoredOnly;
    /** Show tracked/untracked mismatches */
    boolean trackedShown;
    boolean untrackedShown;


    /** --- Constructors --- */

    public OpenMismatchQuery(Set<SpotCheckRefType> refTypes, Collection<SpotCheckMismatchType> mismatchTypes,
                             LocalDateTime observedAfter, MismatchOrderBy orderBy, SortOrder order,
                             LimitOffset limitOffset, boolean resolvedShown, boolean ignoredShown, boolean ignoredOnly,
                             boolean trackedShown, boolean untrackedShown) {
        this.refTypes = refTypes;
        this.mismatchTypes = mismatchTypes;
        this.observedAfter = observedAfter;
        this.orderBy = orderBy;
        this.order = order;
        this.limitOffset = limitOffset;
        this.resolvedShown = resolvedShown;
        this.ignoredShown = ignoredShown;
        this.ignoredOnly = ignoredOnly;
        this.trackedShown = trackedShown;
        this.untrackedShown = untrackedShown;
    }

    /** --- Functional Getters --- */

    @JsonIgnore
    public OrderBy getFullOrderBy() {
        return orderBy != null ? orderBy.toOrderBy(order) : new OrderBy();
    }

    /** --- Getters --- */

    public Set<SpotCheckRefType> getRefTypes() {
        return refTypes;
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

    public boolean isIgnoredOnly() {
        return ignoredOnly;
    }

    public boolean isTrackedShown() {
        return trackedShown;
    }

    public boolean isUntrackedShown() {
        return untrackedShown;
    }
}
