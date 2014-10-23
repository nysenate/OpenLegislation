package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;

import java.util.stream.Collectors;

public class ActiveListView extends SimpleActiveListView
{
    ListView<ActiveListEntryView> entries;

    public ActiveListView(CalendarActiveList activeList) {
        super(activeList);
        this.entries = ListView.of(
            activeList.getEntries().parallelStream()
                .map(ActiveListEntryView::new)
                .collect(Collectors.toList())
        );
    }

    public ListView<ActiveListEntryView> getEntries() {
        return entries;
    }

    @Override
    public String getViewType() {
        return "calendar-activelist";
    }
}
