package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;

public enum MismatchOrderBy {

    OBSERVED("o.observed_date_time"),
    KEY("o.key"),
    REFERENCE("r.reference_date_time"),
    REPORT("r.report_date_time"),
    TYPE("m.type"),
    STATUS("m.status")
    ;

    private String colName;

    MismatchOrderBy(String colName) {
        this.colName = colName;
    }

    public OrderBy toOrderBy(SortOrder order) {
        return new OrderBy(colName, order);
    }

    public String getColName() {
        return colName;
    }
}
