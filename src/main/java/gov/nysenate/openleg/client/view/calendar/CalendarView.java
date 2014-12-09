package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.service.bill.data.BillDataService;

import java.time.LocalDate;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CalendarView extends CalendarIdView {

    protected CalendarSupView floorCalendar;
    protected MapView<String, CalendarSupView> supplementalCalendars;
    protected MapView<Integer, ActiveListView> activeLists;
    protected LocalDate calDate;

    public CalendarView(Calendar calendar, BillDataService billDataService) {
        super(calendar !=null ? calendar.getId() : null);
        if (calendar != null) {
            if (calendar.getSupplemental(Version.DEFAULT) != null) {
                this.floorCalendar = new CalendarSupView(calendar.getSupplemental(Version.DEFAULT), billDataService);
            }
            this.supplementalCalendars = MapView.of(
                    calendar.getSupplementalMap().values().stream()
                            .filter((calSup) -> !calSup.getVersion().equals(Version.DEFAULT))
                            .map(calSup -> new CalendarSupView(calSup, billDataService))
                            .collect(Collectors.toMap(SimpleCalendarSupView::getVersion, Function.identity(),
                                    (a, b) -> b, TreeMap::new))
            );
            this.activeLists = MapView.of(
                    calendar.getActiveListMap().values().stream()
                            .map(activeList -> new ActiveListView(activeList, billDataService))
                            .collect(Collectors.toMap(ActiveListView::getSequenceNumber, Function.identity(),
                                    (a, b) -> b, TreeMap::new))
            );
            calDate = calendar.getCalDate();
        }
    }

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
