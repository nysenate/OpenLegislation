package gov.nysenate.openleg.api.legislation.calendar.view;

import gov.nysenate.openleg.api.MapView;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.calendar.Calendar;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** A calendar view with a minimum of information */
public class SimpleCalendarView extends CalendarIdView {

    protected SimpleCalendarSupView floorCalendar;
    protected MapView<String, SimpleCalendarSupView> supplementalCalendars;
    protected MapView<Integer, SimpleActiveListView> activeLists;

    private final LocalDate calDate;

    public SimpleCalendarView(Calendar calendar) {
        super(calendar.getId());
        if (calendar.getSupplemental(Version.ORIGINAL) != null) {
            this.floorCalendar = new SimpleCalendarSupView(calendar.getSupplemental(Version.ORIGINAL));
        }
        this.supplementalCalendars = MapView.of((Map<String, SimpleCalendarSupView>)
                calendar.getSupplementalMap().values().stream()
                        .filter((calSup) -> !calSup.getVersion().equals(Version.ORIGINAL))
                        .map(SimpleCalendarSupView::new)
                        .collect(Collectors.toMap(SimpleCalendarSupView::getVersion, scsv -> scsv, (a, b) -> b, TreeMap::new))
        );
        this.activeLists = MapView.of((Map<Integer, SimpleActiveListView>)
                calendar.getActiveListMap().values().stream()
                        .map(SimpleActiveListView::new)
                        .collect(Collectors.toMap(SimpleActiveListView::getSequenceNumber, salv -> salv, (a, b) -> b, TreeMap::new))
        );

        this.calDate = calendar.getCalDate();
    }

    public SimpleCalendarSupView getFloorCalendar() {
        return floorCalendar;
    }

    public MapView<String, SimpleCalendarSupView> getSupplementalCalendars() {
        return supplementalCalendars;
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
