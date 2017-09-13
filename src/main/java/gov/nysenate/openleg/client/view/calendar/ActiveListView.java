package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.service.bill.data.BillDataService;

import java.util.stream.Collectors;

public class ActiveListView extends SimpleActiveListView implements CalendarEntryList
{
    ListView<CalendarEntryView> entries;

    CalendarEntryListId calendarEntryListId;

    public ActiveListView(CalendarActiveList activeList, BillDataService billDataService) {
        super(activeList);
        this.entries = ListView.of(
                activeList.getEntries().stream()
                        .map(entry -> new CalendarEntryView(entry, billDataService))
                        .sorted(CalendarEntryView.calEntryViewComparator)
                        .collect(Collectors.toList())
        );
        calendarEntryListId = new CalendarActiveListId(activeList.getCalendarId(),activeList.getSequenceNo()).toCalendarEntryListId();
    }

    public ListView<CalendarEntryView> getEntries() {
        return entries;
    }

    @Override
    public String getViewType() {
        return "calendar-activelist";
    }

    @Override
    public CalendarEntryListId getCalendarEntryListId() {
        return null;
    }
}
