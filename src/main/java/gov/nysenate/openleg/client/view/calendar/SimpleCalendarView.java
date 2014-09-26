package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.ViewMap;
import gov.nysenate.openleg.model.calendar.Calendar;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** A calendar view with a minimum of information */
public class SimpleCalendarView extends CalendarIdView {

    protected ViewMap<String, SimpleCalendarSupView> floorCalendars;

    protected ViewMap<Integer, SimpleActiveListView> activeLists;

    public SimpleCalendarView(Calendar calendar) {
        super(calendar.getId());
        this.floorCalendars = new ViewMap<String, SimpleCalendarSupView>(
                calendar.getSupplementalMap().values().parallelStream()
                    .map(SimpleCalendarSupView::new)
                    .collect(Collectors.toMap(SimpleCalendarSupView::getVersion, scsv -> scsv, (a, b) -> b, TreeMap::new))
        );
        this.activeLists = new ViewMap<Integer, SimpleActiveListView>(
                calendar.getActiveListMap().values().parallelStream()
                    .map(SimpleActiveListView::new)
                    .collect(Collectors.toMap(SimpleActiveListView::getSequenceNumber, salv -> salv, (a, b) -> b, TreeMap::new))
        );
    }

    public ViewMap<String, SimpleCalendarSupView> getFloorCalendars() {
        return floorCalendars;
    }

    public ViewMap<Integer, SimpleActiveListView> getActiveLists() {
        return activeLists;
    }
}
