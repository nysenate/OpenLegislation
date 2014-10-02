package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.model.calendar.Calendar;

import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CalendarView extends CalendarIdView {

    private MapView<String, CalendarSupView> floorCalendars;
    private MapView<Integer, ActiveListView> activeLists;

    public CalendarView(Calendar calendar) {
        super(calendar.getId());
        this.floorCalendars = MapView.of(
                calendar.getSupplementalMap().values().stream()
                        .map(CalendarSupView::new)
                        .collect(Collectors.toMap(SimpleCalendarSupView::getVersion, Function.identity() , (a, b) -> b, TreeMap::new))
        );
        this.activeLists = MapView.of(
                calendar.getActiveListMap().values().stream()
                        .map(ActiveListView::new)
                        .collect(Collectors.toMap(ActiveListView::getSequenceNumber, Function.identity(), (a, b) -> b, TreeMap::new))
        );
    }

    public MapView<String, CalendarSupView> getFloorCalendars() {
        return floorCalendars;
    }

    public MapView<Integer, ActiveListView> getActiveLists() {
        return activeLists;
    }

    @Override
    public String getViewType() {
        return "calendar";
    }
}
