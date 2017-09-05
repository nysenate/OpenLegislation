package gov.nysenate.openleg.dao.bill.reference.openleg;

import gov.nysenate.openleg.client.view.bill.BillView;

import java.util.List;

/**
 * Gets bills from an openleg api
 */
public interface OpenlegBillDao {
    /**
     * Given a session year and apiKey, return the list of BillView from openleg.
     * @param sessionYear
     * @return List of BillView
     */
    public List<BillView> getOpenlegBillView(String sessionYear, String apiKey);
}
