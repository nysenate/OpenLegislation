package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.spotcheck.SpotcheckCalendarId;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PKS on 3/9/16.
 */
@Repository
public class SpotcheckCalendarIdSpotCheckReportDao extends AbstractSpotCheckReportDao<SpotcheckCalendarId> {
    @Override
    public SpotcheckCalendarId getKeyFromMap(Map<String, String> keyMap) {
        if (keyMap != null) {
            return new SpotcheckCalendarId(
                    new CalendarId(Integer.parseInt(keyMap.get("calNo")), Integer.parseInt(keyMap.get("year"))),
                    CalendarType.valueOf(keyMap.get("type")),
                    Version.of(keyMap.get("version")),
                    Integer.parseInt(keyMap.get("sequenceNo")));
        }
        return null;
    }

    @Override
    public Map<String, String> getMapFromKey(SpotcheckCalendarId spotcheckCalendarId) {
        if (spotcheckCalendarId != null) {
            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("calNo", Integer.toString(spotcheckCalendarId.getCalendarId().getCalNo()));
            keyMap.put("year", Integer.toString(spotcheckCalendarId.getCalendarId().getYear()));
            keyMap.put("type", spotcheckCalendarId.getType().toString());
            keyMap.put("version", spotcheckCalendarId.getVersion().toString());
            keyMap.put("sequenceNo",spotcheckCalendarId.getSequenceNo().toString());
            return keyMap;
        }
        return null;
    }
}
