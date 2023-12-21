package gov.nysenate.openleg.api.legislation.calendar.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveList;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarEntryListId;

import java.util.Comparator;
import java.util.Map;

public class ActiveListView extends SimpleActiveListView implements CalendarEntryList {
    private ListView<CalendarEntryView> entries;

    public ActiveListView(CalendarActiveList activeList, Map<BillId, BillInfo> infoMap) {
        super(activeList);
        this.entries = ListView.of(
                activeList.getEntries().stream()
                        .map(entry -> new CalendarEntryView(entry, infoMap.get(entry.getBillId())))
                        .sorted(Comparator.comparingInt(CalendarEntryView::getBillCalNo))
                        .toList()
        );
    }

    //Added for Json deserialization
    protected ActiveListView() {}

    @JsonIgnore
    public CalendarEntryListId getCalendarEntryListId() {
        return this.toCalendarEntryListId();
    }

    public ListView<CalendarEntryView> getEntries() {
        return entries;
    }

    @Override
    public String getViewType() {
        return "calendar-activelist";
    }
}
