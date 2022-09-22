package gov.nysenate.openleg.api.legislation.calendar.view;

import gov.nysenate.openleg.api.MapView;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import gov.nysenate.openleg.legislation.calendar.Calendar;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CalendarView extends CalendarIdView {

    protected CalendarSupView floorCalendar;
    protected MapView<String, CalendarSupView> supplementalCalendars;
    protected MapView<Integer, ActiveListView> activeLists;
    protected LocalDate calDate;

    public CalendarView(Calendar calendar, Map<BillId, BillInfo> infoMap) {
        super(calendar.getId());
        var calendars = calendar.getSupplementalMap().values().stream()
                        .map(calSup -> new CalendarSupView(calSup, infoMap))
                        .collect(Collectors.toMap(SimpleCalendarSupView::getVersion, Function.identity(),
                                (a, b) -> b, TreeMap::new));
        this.floorCalendar = calendars.remove("floor");
        this.supplementalCalendars = MapView.of(calendars);

        this.activeLists = MapView.of(
                calendar.getActiveListMap().values().stream()
                        .map(activeList -> new ActiveListView(activeList, infoMap))
                        .collect(Collectors.toMap(ActiveListView::getSequenceNumber, Function.identity(),
                                (a, b) -> b, TreeMap::new))
        );
        calDate = calendar.getCalDate();
    }

    // Added for Json deserialization
    protected CalendarView() {}

    public CalendarSupView getFloorCalendar() {
        return floorCalendar;
    }

    public MapView<String, CalendarSupView> getSupplementalCalendars() {
        return supplementalCalendars;
    }

    public MapView<Integer, ActiveListView> getActiveLists() {
        return activeLists;
    }

    public LocalDate getCalDate() {
        return calDate;
    }

    @Override
    public String getViewType() {
        return "calendar";
    }
}
