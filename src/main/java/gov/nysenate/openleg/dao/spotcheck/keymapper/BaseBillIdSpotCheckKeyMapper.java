package gov.nysenate.openleg.dao.spotcheck.keymapper;

import gov.nysenate.openleg.model.bill.BaseBillId;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BaseBillIdSpotCheckKeyMapper implements SpotCheckDaoKeyMapper<BaseBillId> {

    @Override
    public Class<BaseBillId> getKeyClass() {
        return BaseBillId.class;
    }

    @Override
    public BaseBillId getKeyFromMap(Map<String, String> keyMap) {
        if (keyMap == null) {
            return null;
        }
        String printNo = keyMap.get("print_no");
        int session = Integer.parseInt(keyMap.get("session_year"));
        return new BaseBillId(printNo, session);
    }

    @Override
    public Map<String, String> getMapFromKey(BaseBillId baseBillId) {
        if (baseBillId == null) {
            return null;
        }
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("print_no", baseBillId.getBasePrintNo());
        keyMap.put("session_year", baseBillId.getSession().toString());
        return keyMap;
    }
}
