package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.model.agenda.AgendaId;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PKS on 4/28/16.
 */
@Repository
public class AgendaIdSpotCheckReportDao extends AbstractSpotCheckReportDao<AgendaId> {
    @Override
    public AgendaId getKeyFromMap(Map<String, String> keyMap) {
        if (keyMap != null) {
            return new AgendaId(Integer.parseInt(keyMap.get("agendaNo")), Integer.parseInt(keyMap.get("year")));
        }
        return null;
    }

    @Override
    public Map<String, String> getMapFromKey(AgendaId agendaId) {
        if (agendaId != null) {
            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("agendaNo", Integer.toString(agendaId.getNumber().intValue()));
            keyMap.put("year", Integer.toString(agendaId.getYear()));
            return keyMap;
        }
        return null;
    }
}
