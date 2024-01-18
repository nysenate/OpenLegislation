package gov.nysenate.openleg.api.legislation.calendar.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.MapView;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import gov.nysenate.openleg.legislation.calendar.CalendarSupplemental;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarEntryListId;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CalendarSupView extends SimpleCalendarSupView implements CalendarEntryList {
    private static final Comparator<CalendarSupEntryView> supEntryViewComparator =
            Comparator.comparingInt(CalendarSupEntryView::getBillCalNo);
    protected MapView<String, ListView<CalendarSupEntryView>> entriesBySection;

    public CalendarSupView(CalendarSupplemental calendarSupplemental, Map<BillId, BillInfo> infoMap) {
        super(calendarSupplemental);
        this.entriesBySection = MapView.of(
                calendarSupplemental.getSectionEntries().asMap().values().stream()
                        .map(entryList -> entryList.stream()
                                .map(entry -> new CalendarSupEntryView(entry, infoMap.get(BaseBillId.of(entry.getBillId()))))
                                .sorted(supEntryViewComparator).toList())
                        .map(ListView::of)
                        .collect(Collectors.toMap(list -> list.getItems().get(0).getSectionType(), Function.identity()))
        );
    }

    // Added for Json deserialization
    protected CalendarSupView() {}

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

    @JsonIgnore
    public CalendarEntryListId getCalendarEntryListId() {
        return this.toCalendarEntryListId();
    }
}
