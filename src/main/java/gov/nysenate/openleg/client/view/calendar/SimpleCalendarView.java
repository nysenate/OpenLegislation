package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.model.calendar.Calendar;

import java.time.LocalDate;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** A calendar view with a minimum of information */
public class SimpleCalendarView extends CalendarIdView {

    protected MapView<String, SimpleCalendarSupView> floorCalendars;

    protected MapView<Integer, SimpleActiveListView> activeLists;

    private LocalDate calDate;

    public SimpleCalendarView(Calendar calendar) {
        super(calendar != null ? calendar.getId() : null);
        if (calendar != null) {
            this.floorCalendars = MapView.of(
                    calendar.getSupplementalMap().values().stream()
                            .map(SimpleCalendarSupView::new)
                            .collect(Collectors.toMap(SimpleCalendarSupView::getVersion, scsv -> scsv, (a, b) -> b, TreeMap::new))
            );
            this.activeLists = MapView.of(
                    calendar.getActiveListMap().values().stream()
                            .map(SimpleActiveListView::new)
                            .collect(Collectors.toMap(SimpleActiveListView::getSequenceNumber, salv -> salv, (a, b) -> b, TreeMap::new))
            );

            this.calDate = calendar.getCalDate();
        }
    }

    public MapView<String, SimpleCalendarSupView> getFloorCalendars() {
        return floorCalendars;
    }

    public MapView<Integer, SimpleActiveListView> getActiveLists() {
        return activeLists;
    }

    public LocalDate getCalDate() {
        return calDate;
    }

    @Override
    public String getViewType() {
        return "calendar-simple";
    }
}
