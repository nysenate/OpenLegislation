package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.service.bill.data.BillDataService;

import java.time.LocalDate;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CalendarView extends CalendarIdView {

    private MapView<String, CalendarSupView> supplementalCalendars;
    private MapView<Integer, ActiveListView> activeLists;
    private LocalDate calDate;

    public CalendarView(Calendar calendar, BillDataService billDataService) {
        super(calendar !=null ? calendar.getId() : null);
        if (calendar != null) {
            this.supplementalCalendars = MapView.of(
                    calendar.getSupplementalMap().values().stream()
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
