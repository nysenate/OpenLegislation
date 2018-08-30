package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class CommitteeAgendaReportDao extends AbstractSpotCheckReportDao<CommitteeAgendaAddendumId> {

    @Override
    public CommitteeAgendaAddendumId getKeyFromMap(Map<String, String> keyMap) {
        return new CommitteeAgendaAddendumId(
                new AgendaId(
                        Long.parseLong(keyMap.get("agenda_no")),
                        Integer.parseInt(keyMap.get("year"))
                ),
                new CommitteeId(
                        Chamber.getValue(keyMap.get("chamber")),
                        keyMap.get("committee_name")
                ),
                Version.of(keyMap.get("addendum"))
        );
    }

    @Override
    public Map<String, String> getMapFromKey(CommitteeAgendaAddendumId addendumId) {
        Optional<AgendaId> agendaIdOpt = Optional.ofNullable(addendumId.getAgendaId());
        Optional<CommitteeId> committeeIdOpt = Optional.ofNullable(addendumId.getCommitteeId());
        return ImmutableMap.<String, String>builder()
                .put("agenda_no", agendaIdOpt.map(AgendaId::getNumber).map(String::valueOf).orElse("null"))
                .put("year", agendaIdOpt.map(AgendaId::getYear).map(String::valueOf).orElse("null"))
                .put("chamber", committeeIdOpt.map(CommitteeId::getChamber).map(Chamber::asSqlEnum).orElse("null"))
                .put("committee_name", committeeIdOpt.map(CommitteeId::getName).orElse("null"))
                .put("addendum", Optional.ofNullable(addendumId.getAddendum()).map(Enum::name).orElse("null"))
                .build();
    }
}
