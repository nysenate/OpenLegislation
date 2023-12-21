package gov.nysenate.openleg.api.legislation.calendar.view;

import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveList;
import gov.nysenate.openleg.legislation.calendar.CalendarEntry;
import gov.nysenate.openleg.legislation.calendar.CalendarSupplemental;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CalendarViewFactory {
    private final BillDataService billDataService;

    @Autowired
    public CalendarViewFactory(BillDataService billDataService) {
        this.billDataService = billDataService;
    }

    public CalendarView getCalendarView(Calendar calendar) {
        Map<BillId, BillInfo> infoMap = new HashMap<>();
        for (var activeList : calendar.getActiveListMap().values()) {
            infoMap.putAll(getActiveInfoMap(activeList));
        }
        for (var calSup : calendar.getSupplementalMap().values()) {
            infoMap.putAll(getSupInfoMap(calSup));
        }
        return new CalendarView(calendar, infoMap);
    }

    public ActiveListView getActiveListView(CalendarActiveList activeList) {
        return new ActiveListView(activeList, getActiveInfoMap(activeList));
    }

    public CalendarSupView getCalendarSupView(CalendarSupplemental calendarSupplemental) {
        return new CalendarSupView(calendarSupplemental, getSupInfoMap(calendarSupplemental));
    }

    public CalendarEntryView getCalEntryView(CalendarEntry entry) {
        var info = billDataService.getBillInfoSafe(BillId.getBaseId(entry.getBillId()));
        return new CalendarEntryView(entry, info);
    }

    private Map<BillId, BillInfo> getSupInfoMap(CalendarSupplemental calendarSupplemental) {
        Map<BillId, BillInfo> infoMap = new HashMap<>();
        for (var supEntry : calendarSupplemental.getAllEntries()) {
            var baseId = BillId.getBaseId(supEntry.getBillId());
            infoMap.put(baseId, billDataService.getBillInfo(baseId));
        }
        return infoMap;
    }

    private Map<BillId, BillInfo> getActiveInfoMap(CalendarActiveList activeList) {
        Map<BillId, BillInfo> infoMap = new HashMap<>();
        for (var entry : activeList.getEntries()) {
            var baseId = BillId.getBaseId(entry.getBillId());
            infoMap.put(baseId, billDataService.getBillInfoSafe(baseId));
        }
        return infoMap;
    }
}
