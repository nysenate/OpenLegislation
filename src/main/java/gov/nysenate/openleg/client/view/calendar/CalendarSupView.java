package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.model.calendar.CalendarSupplemental;
import gov.nysenate.openleg.service.bill.data.BillDataService;

import java.util.function.Function;
import java.util.stream.Collectors;

public class CalendarSupView extends SimpleCalendarSupView {

    protected MapView<String, ListView<CalendarSupEntryView>> entriesBySection;

    public CalendarSupView(CalendarSupplemental calendarSupplemental, BillDataService billDataService) {
        super(calendarSupplemental);
        this.entriesBySection = MapView.of(
                calendarSupplemental.getSectionEntries().asMap().values().stream()
                    .map(entryList -> entryList.stream()
                            .map(entry -> new CalendarSupEntryView(entry, billDataService))
                            .sorted(CalendarSupEntryView.supEntryViewComparator)
                            .collect(Collectors.toList()))
                    .map(ListView::of)
                    .collect(Collectors.toMap(list -> list.getItems().get(0).getSectionType(), Function.identity()))
        );
    }

    public MapView<String, ListView<CalendarSupEntryView>> getEntriesBySection() {
        return entriesBySection;
    }

    @Override
    public String getViewType() {
        if (this.version.equals("floor")) {
            return "calendar-floor";
        }
        return "calendar-supplemental";
    }
}
