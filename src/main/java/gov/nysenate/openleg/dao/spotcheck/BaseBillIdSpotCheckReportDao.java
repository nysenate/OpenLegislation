package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.model.bill.BaseBillId;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class BaseBillIdSpotCheckReportDao extends AbstractSpotCheckReportDao<BaseBillId>
{
    /** --- Override Methods --- */

    /**
     * {@inheritDoc
     *
     * Converts the entries in the keyMap into a BaseBillId by assuming that this map
     * has the same contents as the result of {@link #getMapFromKey(BaseBillId)}.
     * Returns null if the given map is also null.
     */
    @Override
    public BaseBillId getKeyFromMap(Map<String, String> keyMap) {
        if (keyMap != null) {
            return new BaseBillId(keyMap.get("print_no"), Integer.parseInt(keyMap.get("session_year")));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * Converts the baseBillId into a Map that fully represents it.
     * Returns null if the given baseBillId is also null.
     */
    @Override
    public Map<String, String> getMapFromKey(BaseBillId baseBillId) {
        if (baseBillId != null) {
            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("print_no", baseBillId.getBasePrintNo());
            keyMap.put("session_year", baseBillId.getSession().toString());
            return keyMap;
        }
        return null;
    }
}
