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

    public ActiveListView(CalendarActiveList activeList, BillDataService billDataService) {
        super(activeList);
        this.entries = ListView.of(
                activeList.getEntries().stream()
                        .map(entry -> new CalendarEntryView(entry, billDataService))
                        .sorted(CalendarEntryView.calEntryViewComparator)
                        .collect(Collectors.toList())
        );
    }

    //Added for Json deserialization
    public ActiveListView() {}

    //Added for Json deserialization
    public void setEntries(ListView<CalendarEntryView> entries) {
        this.entries = entries;
    }

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
