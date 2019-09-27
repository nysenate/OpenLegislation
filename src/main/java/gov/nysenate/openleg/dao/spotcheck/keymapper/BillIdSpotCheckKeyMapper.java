package gov.nysenate.openleg.dao.spotcheck.keymapper;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BillIdSpotCheckKeyMapper implements SpotCheckDaoKeyMapper<BillId> {

    private final SpotCheckDaoKeyMapper<BaseBillId> baseMapper;

    public BillIdSpotCheckKeyMapper(SpotCheckDaoKeyMapper<BaseBillId> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public Class<BillId> getKeyClass() {
        return BillId.class;
    }

    @Override
    public BillId getKeyFromMap(Map<String, String> keyMap) {
        if (keyMap == null) {
            return null;
        }
        BaseBillId baseBillId = baseMapper.getKeyFromMap(keyMap);
        Version version = Version.of(keyMap.get("version"));
        return baseBillId.withVersion(version);
    }

    @Override
    public Map<String, String> getMapFromKey(BillId billId) {
        if (billId == null) {
            return null;
        }
        Map<String, String> keyMap = baseMapper.getMapFromKey(BaseBillId.of(billId));
        keyMap.put("version", billId.getVersion().toString());

        return keyMap;
    }
}
