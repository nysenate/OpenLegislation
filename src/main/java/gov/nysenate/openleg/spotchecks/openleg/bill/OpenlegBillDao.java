package gov.nysenate.openleg.spotchecks.openleg.bill;

import gov.nysenate.openleg.api.legislation.bill.view.BillView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.legislation.SessionYear;

/**
 * Gets bills from an openleg api
 */
public interface OpenlegBillDao {
    /**
     * Given a session year and pagination params, return the list of BillView from openleg.
     *
     * @param sessionYear {@link SessionYear}
     * @param limitOffset {@link LimitOffset}
     * @return {@link PaginatedList<BillView>}
     */
    PaginatedList<BillView> getBillViews(SessionYear sessionYear, LimitOffset limitOffset);
}
