package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.model.bill.BillId;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class BillIdSpotCheckReportDao extends AbstractSpotCheckReportDao<BillId> {

    /** --- Override Methods --- */

    /**
     * {@inheritDoc
     *
     * Converts the entries in the keyMap into a BillId by assuming that this map
     * has the same contents as the result of {@link #getMapFromKey(BillId)}.
     * Returns null if the given map is also null.
     */
    @Override
    public BillId getKeyFromMap(Map<String, String> keyMap) {
        if (keyMap != null) {
            return new BillId(keyMap.get("print_no"), Integer.parseInt(keyMap.get("session_year")), keyMap.get("version"));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * Converts the billId into a Map that fully represents it.
     * Returns null if the given billId is also null.
     */
    @Override
    public Map<String, String> getMapFromKey(BillId billId) {
        if (billId != null) {
            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("print_no", billId.getBasePrintNo());
            keyMap.put("session_year", billId.getSession().toString());
            keyMap.put("version", billId.getVersion().toString());
            return keyMap;
        }
        return null;
    }
}
