package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PKS on 3/9/16.
 */
@Repository
public class CalendarEntryListIdSpotCheckReportDao extends AbstractSpotCheckReportDao<CalendarEntryListId> {
    private static final Logger logger = LoggerFactory.getLogger(CalendarEntryListIdSpotCheckReportDao.class);

    @Override
    public CalendarEntryListId getKeyFromMap(Map<String, String> keyMap) {
        if (keyMap != null) {
            logger.info("Loading KeyMap: calNo" +keyMap.get("calNo")+"year"+keyMap.get("year")+"seqNo"+keyMap.get("sequenceNo")+"&&&&&&");
            return new CalendarEntryListId(
                    new CalendarId(Integer.parseInt(keyMap.get("calNo")), Integer.parseInt(keyMap.get("year"))),
                    CalendarType.valueOf(keyMap.get("type")),
                    Version.of(keyMap.get("version")),
                    Integer.parseInt(keyMap.get("sequenceNo")));
        }
        return null;
    }

    @Override
    public Map<String, String> getMapFromKey(CalendarEntryListId calendarEntryListId) {
        if (calendarEntryListId != null) {
            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("calNo", Integer.toString(calendarEntryListId.getCalendarId().getCalNo()));
            keyMap.put("year", Integer.toString(calendarEntryListId.getCalendarId().getYear()));
            keyMap.put("type", calendarEntryListId.getType().toString());
            keyMap.put("version", calendarEntryListId.getVersion().toString());
            keyMap.put("sequenceNo", calendarEntryListId.getSequenceNo().toString());
            return keyMap;
        }
        return null;
    }
}
