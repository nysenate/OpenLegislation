package gov.nysenate.openleg.spotchecks.keymapper;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CommitteeAgendaKeyMapper implements SpotCheckDaoKeyMapper<CommitteeAgendaAddendumId> {
    @Override
    public Class<CommitteeAgendaAddendumId> getKeyClass() {
        return CommitteeAgendaAddendumId.class;
    }

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
