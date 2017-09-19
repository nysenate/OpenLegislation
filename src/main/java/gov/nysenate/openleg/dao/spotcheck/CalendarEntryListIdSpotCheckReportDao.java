package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.data.CalendarNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PKS on 3/9/16.
 */
@Repository
public class CalendarEntryListIdSpotCheckReportDao extends AbstractSpotCheckReportDao<CalendarEntryListId> {
    private static final Logger logger = LoggerFactory.getLogger(CalendarEntryListIdSpotCheckReportDao.class);
    @Autowired
    private CalendarDataService calendarDataService;

    @Override
    public CalendarEntryListId getKeyFromMap(Map<String, String> keyMap) {
        if (keyMap != null) {
            CalendarId calendarId = new CalendarId(Integer.parseInt(keyMap.get("calNo")), Integer.parseInt(keyMap.get("year")));
           try{
               Calendar calendar = calendarDataService.getCalendar(calendarId);
               CalendarEntryListId result = new CalendarEntryListId(
                       calendarId,
                       CalendarType.valueOf(keyMap.get("type")),
                       Version.of(keyMap.get("version")),
                       Integer.parseInt(keyMap.get("sequenceNo")));
                       return result;
           }catch (CalendarNotFoundEx calendarNotFoundEx){
               CalendarEntryListId result = new CalendarEntryListId(
                       calendarId,
                       CalendarType.valueOf(keyMap.get("type")),
                       Version.of(keyMap.get("version")),
                       Integer.parseInt(keyMap.get("sequenceNo")));
               return result;
           }
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
