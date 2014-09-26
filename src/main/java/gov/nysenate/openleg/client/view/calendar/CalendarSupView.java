package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.ViewList;
import gov.nysenate.openleg.client.view.base.ViewMap;
import gov.nysenate.openleg.client.view.bill.CalendarSupEntryView;
import gov.nysenate.openleg.model.calendar.CalendarSupplemental;

import java.util.List;
import java.util.stream.Collectors;

public class CalendarSupView extends SimpleCalendarSupView {

    protected ViewMap<String, ViewList<CalendarSupEntryView>> entriesBySection;

    public CalendarSupView(CalendarSupplemental calendarSupplemental) {
        super(calendarSupplemental);
        this.entriesBySection = new ViewMap<>(
                calendarSupplemental.getSectionEntries().asMap().values().parallelStream()
                    .map(entryList -> entryList.parallelStream()
                                        .map(CalendarSupEntryView::new)
                                        .collect(Collectors.toList()))
                    .map(ViewList<CalendarSupEntryView>::new)
                    .collect(Collectors.toMap(viewList -> viewList.getItems().get(0).getSectionType(), viewList -> viewList))
        );
    }

    public ViewMap<String, ViewList<CalendarSupEntryView>> getEntriesBySection() {
        return entriesBySection;
    }
}
