package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;

public enum MismatchOrderBy {

    OBSERVED_DATE("o.observed_date_time"),
    CONTENT_KEY("o.key"),
    REFERENCE_DATE("r.reference_date_time"),
    MISMATCH_TYPE("m.type"),
    STATUS("m.status")
    ;

    private String colName;

    MismatchOrderBy(String colName) {
        this.colName = colName;
    }

    public OrderBy toOrderBy(SortOrder order) {
        // Always use content key as a secondary order
        if (CONTENT_KEY.equals(this)) {
            return new OrderBy(CONTENT_KEY.colName, order);
        }
        return new OrderBy(colName, order, CONTENT_KEY.colName, order);
    }

    public String getColName() {
        return colName;
    }
}
