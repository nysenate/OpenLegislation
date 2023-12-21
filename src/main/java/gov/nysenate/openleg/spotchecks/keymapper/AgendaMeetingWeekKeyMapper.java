package gov.nysenate.openleg.spotchecks.keymapper;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.spotchecks.alert.agenda.AgendaMeetingWeekId;
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
        var committeeId = new CommitteeId(Chamber.getValue(keyMap.get("chamber")), keyMap.get("committee_name"));
        return new AgendaMeetingWeekId(
                Integer.valueOf(keyMap.get("year")),
                LocalDate.parse(keyMap.get("week_of"), DATE_FORMAT),
                Version.of(keyMap.get("addendum")),
                committeeId
        );
    }

    @Override
    public Map<String, String> getMapFromKey(AgendaMeetingWeekId alertId) {
        Objects.requireNonNull(alertId);
        return ImmutableMap.<String, String>builder()
                .put("year", String.valueOf(alertId.year()))
                .put("week_of", alertId.weekOf().format(DATE_FORMAT))
                .put("addendum", alertId.addendum().name())
                .put("chamber", alertId.committeeId().getChamber().asSqlEnum())
                .put("committee_name", alertId.committeeId().getName())
                .build();
    }
}
