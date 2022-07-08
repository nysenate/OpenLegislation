package gov.nysenate.openleg.api.legislation.calendar.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveList;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarEntryListId;

public class ActiveListView extends SimpleActiveListView implements CalendarEntryList
{
    private ListView<CalendarEntryView> entries;

    public ActiveListView(CalendarActiveList activeList, BillDataService billDataService) {
        super(activeList);
        this.entries = ListView.of(
                activeList.getEntries().stream()
                        .map(entry -> new CalendarEntryView(entry, billDataService))
                        .sorted(CalendarEntryView.calEntryViewComparator)
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
