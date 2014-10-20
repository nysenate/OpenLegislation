package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.model.calendar.CalendarSupplemental;

import java.util.function.Function;
import java.util.stream.Collectors;

public class CalendarSupView extends SimpleCalendarSupView {

    protected MapView<String, ListView<CalendarSupEntryView>> entriesBySection;

    public CalendarSupView(CalendarSupplemental calendarSupplemental) {
        super(calendarSupplemental);
        this.entriesBySection = MapView.of(
                calendarSupplemental.getSectionEntries().asMap().values().parallelStream()
                    .map(entryList -> entryList.parallelStream()
                            .map(CalendarSupEntryView::new)
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
        return "calendar-floor";
    }
}
