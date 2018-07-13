package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PKS on 4/28/16.
 */
@Repository
public class CommitteeAgendaAddendumIdSpotCheckReportDao extends AbstractSpotCheckReportDao<CommitteeAgendaAddendumId> {
    @Override
    public CommitteeAgendaAddendumId getKeyFromMap(Map<String, String> keyMap) {
        if (keyMap != null) {
            return new CommitteeAgendaAddendumId(new AgendaId(Long.parseLong(keyMap.get("agendaNo")), Integer.parseInt(keyMap.get("year"))),
                    new CommitteeId(Chamber.valueOf(keyMap.get("chamber")),keyMap.get("committeeName")),
                    Version.of(keyMap.get("addendum")));
        }
        return null;
    }

    @Override
    public Map<String, String> getMapFromKey(CommitteeAgendaAddendumId committeeAgendaAddendumId) {
        if (committeeAgendaAddendumId != null) {
            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("agendaNo", Long.toString(committeeAgendaAddendumId.getAgendaId().getNumber()));
            keyMap.put("year", Long.toString(committeeAgendaAddendumId.getAgendaId().getYear()));
            keyMap.put("addendum", committeeAgendaAddendumId.getAddendum().name());
            keyMap.put("committeeName", committeeAgendaAddendumId.getCommitteeId().getName());
            keyMap.put("chamber",committeeAgendaAddendumId.getCommitteeId().getChamber().toString());
            return keyMap;
        }
        return null;
    }
}
