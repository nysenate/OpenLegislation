package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.ViewList;
import gov.nysenate.openleg.client.view.bill.ActiveListEntryView;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;

import java.util.stream.Collectors;


public class ActiveListView extends SimpleActiveListView {

    ViewList<ActiveListEntryView> entries;

    public ActiveListView(CalendarActiveList activeList) {
        super(activeList);

        this.entries = new ViewList<ActiveListEntryView>(
            activeList.getEntries().parallelStream()
                    .map(ActiveListEntryView::new)
                    .collect(Collectors.toList())
        );
    }

    public ViewList<ActiveListEntryView> getEntries() {
        return entries;
    }
}
