package gov.nysenate.openleg.dao.bill.reference.openleg;

import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.model.base.SessionYear;

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
