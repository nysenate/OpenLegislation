package gov.nysenate.openleg.dao.spotcheck.keymapper;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaMeetingWeekId;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@Service
public class AgendaMeetingWeekKeyMapper implements SpotCheckDaoKeyMapper<AgendaMeetingWeekId> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;


    @Override
    public Class<AgendaMeetingWeekId> getKeyClass() {
        return AgendaMeetingWeekId.class;
    }

    @Override
    public AgendaMeetingWeekId getKeyFromMap(Map<String, String> keyMap) {
        Objects.requireNonNull(keyMap);
        return new AgendaMeetingWeekId(
                Integer.valueOf(keyMap.get("year")),
                LocalDate.parse(keyMap.get("week_of"), DATE_FORMAT),
                Version.of(keyMap.get("addendum")),
                Chamber.getValue(keyMap.get("chamber")),
                keyMap.get("committee_name")
        );
    }

    @Override
    public Map<String, String> getMapFromKey(AgendaMeetingWeekId alertId) {
        Objects.requireNonNull(alertId);
        return ImmutableMap.<String, String>builder()
                .put("year", String.valueOf(alertId.getYear()))
                .put("week_of", alertId.getWeekOf().format(DATE_FORMAT))
                .put("addendum", alertId.getAddendum().name())
                .put("chamber", alertId.getCommitteeId().getChamber().asSqlEnum())
                .put("committee_name", alertId.getCommitteeId().getName())
                .build();
    }
}
