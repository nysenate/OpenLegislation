package gov.nysenate.openleg.dao.spotcheck.keymapper;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CalendarEntryListIdKeyMapper implements SpotCheckDaoKeyMapper<CalendarEntryListId> {
    @Override
    public Class<CalendarEntryListId> getKeyClass() {
        return CalendarEntryListId.class;
    }

    @Override
    public CalendarEntryListId getKeyFromMap(Map<String, String> keyMap) {
        Objects.requireNonNull(keyMap);

        CalendarId calendarId = new CalendarId(Integer.parseInt(keyMap.get("calNo")), Integer.parseInt(keyMap.get("year")));
        return new CalendarEntryListId(
                calendarId,
                CalendarType.valueOf(keyMap.get("type")),
                keyMap.get("version") == null ? null : Version.of(keyMap.get("version")),
                keyMap.get("sequenceNo") == null ? null : Integer.parseInt(keyMap.get("sequenceNo")));
    }

    @Override
    public Map<String, String> getMapFromKey(CalendarEntryListId calendarEntryListId) {
        Objects.requireNonNull(calendarEntryListId);

        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("calNo", Integer.toString(calendarEntryListId.getCalendarId().getCalNo()));
        keyMap.put("year", Integer.toString(calendarEntryListId.getCalendarId().getYear()));
        keyMap.put("type", calendarEntryListId.getType().toString());
        keyMap.put("version", calendarEntryListId.getVersion() == null ? null : calendarEntryListId.getVersion().toString());
        keyMap.put("sequenceNo", calendarEntryListId.getSequenceNo() == null ? null : calendarEntryListId.getSequenceNo().toString());
        return keyMap;
    }
}
