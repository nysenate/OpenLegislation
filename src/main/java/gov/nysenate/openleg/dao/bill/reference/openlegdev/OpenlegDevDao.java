package gov.nysenate.openleg.dao.bill.reference.openlegdev;

import gov.nysenate.openleg.client.view.bill.BillView;

import java.util.List;

/**
 * Created by Chenguang He on 2017/3/21.
 */
public interface OpenlegDevDao {
    /**
     * Given a session year and apiKey, return the list of BillView from openleg dev.
     * @param sessionYear
     * @return List of BillView
     */
    public List<BillView> getOpenlegBillView(String sessionYear, String apiKey);
}
