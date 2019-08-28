package gov.nysenate.openleg.dao.spotcheck.keymapper;

import gov.nysenate.openleg.model.law.LawObservationType;
import gov.nysenate.openleg.model.law.LawSpotCheckId;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static gov.nysenate.openleg.model.law.LawObservationType.*;

@Service
public class LawSpotCheckIdKeyMapper implements SpotCheckDaoKeyMapper<LawSpotCheckId> {

    @Override
    public Class<LawSpotCheckId> getKeyClass() {
        return LawSpotCheckId.class;
    }

    @Override
    public LawSpotCheckId getKeyFromMap(Map<String, String> keyMap) {
        LawObservationType obsType = valueOf(keyMap.get("law_obs_type"));
        String chapter = keyMap.get("law_chapter");
        String locationId = keyMap.get("location_id");
        return new LawSpotCheckId(obsType, chapter, locationId);
    }

    @Override
    public Map<String, String> getMapFromKey(LawSpotCheckId id) {
        Map<String, String> keyMap = new HashMap<>();
        LawObservationType obsType = id.getObsType();
        keyMap.put("law_obs_type", obsType.toString());
        if (obsType == TREE || obsType == DOCUMENT) {
            keyMap.put("law_chapter", id.getLawChapter());
        }
        if (obsType == DOCUMENT) {
            keyMap.put("location_id", id.getLocationId());
        }
        return keyMap;
    }
}
