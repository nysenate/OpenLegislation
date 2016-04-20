package gov.nysenate.openleg.service.spotcheck.senatesite.calendar;

import gov.nysenate.openleg.client.view.calendar.CalendarEntryView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupEntryView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupView;
import gov.nysenate.openleg.client.view.calendar.CalendarView;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.senatesite.calendar.SenateSiteCalendar;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by PKS on 2/25/16.
 */
@Service
public class CalendarCheckServices extends BaseSpotCheckService<CalendarEntryListId, Calendar, SenateSiteCalendar> {

    @Autowired
    BillDataService billDataService;

    @Override
    public SpotCheckObservation<CalendarEntryListId> check(Calendar content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<CalendarEntryListId> check(Calendar content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<CalendarEntryListId> check(Calendar content, SenateSiteCalendar reference) {
        SpotCheckObservation<CalendarEntryListId> observation = new SpotCheckObservation<>(reference.getReferenceId(), reference.getCalendarEntryListId());
        checkCalendarId(content,reference,observation);
        CalendarView newContent = getCalView(content);
        if(reference.getCalendarType() == CalendarType.ACTIVE_LIST){
            checkActiveList(newContent,reference,observation);
        }else if (reference.getCalendarType() == CalendarType.SUPPLEMENTAL_CALENDAR){
            checkSupplemental(newContent,reference,observation);
        }else {
            checkFloor(newContent,reference,observation);
        }
        return observation;
    }

    private void checkCalendarId(Calendar content, SenateSiteCalendar reference, SpotCheckObservation<CalendarEntryListId> observation) {
        checkString(content.getId().toString(),reference.getCalendarId().toString(),observation, SpotCheckMismatchType.CALENDAR_ID);
    }

    private void checkActiveList(CalendarView content, SenateSiteCalendar reference, SpotCheckObservation<CalendarEntryListId> observation) {
        List<CalendarEntryView> calendarEntryViews = content.getActiveLists().getItems()
                .get(reference.getSequenceNo())
                .getEntries()
                .getItems().asList();
        List<CalendarEntry> refCalEntries = getCalEntry(reference);
        List<CalendarEntryView> refCalEntryViews = getCalEntryView(refCalEntries);
        checkCollection(calendarEntryViews, refCalEntryViews, observation, SpotCheckMismatchType.ACTIVE_LIST_ENTRY, this::calEntryViewDiffString,"\n");
    }

    private void checkSupplemental(CalendarView content, SenateSiteCalendar reference, SpotCheckObservation<CalendarEntryListId> observation) {
        List<CalendarEntryView> calendarSupEntryViews = content
                                                    .getSupplementalCalendars()
                                                    .getItems().values().stream()
                .flatMap(calendarSupView -> calendarSupView.getEntriesBySection().getItems().values().stream())
                .flatMap(contents -> contents.getItems().stream())
                .map(calendarSupEntryView -> (CalendarEntryView) calendarSupEntryView).collect(Collectors.toList());

        List<CalendarEntry> refCalendarSupplementalEntries = getCalEntry(reference);
        List<CalendarEntryView> refcalendarEntryViews = getCalEntryView(refCalendarSupplementalEntries);
        checkCollection(calendarSupEntryViews,refcalendarEntryViews,observation,SpotCheckMismatchType.SUPPLEMENTAL_ENTRY,
                this::calEntryViewDiffString, "\n");
    }

    private void checkFloor(CalendarView content , SenateSiteCalendar reference, SpotCheckObservation<CalendarEntryListId> observation){
        List<CalendarEntryView> calendarSupEntryViews = content.getFloorCalendar().getEntriesBySection().getItems().values().stream()
                .flatMap(contents -> contents.getItems().stream())
                .map(calendarSupEntryView -> (CalendarEntryView) calendarSupEntryView).collect(Collectors.toList());

        List<CalendarEntry> refCalendarFloorEntries = getCalEntry(reference);
        List<CalendarEntryView> refcalendarEntryViews = getCalEntryView(refCalendarFloorEntries);
        checkCollection(calendarSupEntryViews,refcalendarEntryViews,observation,SpotCheckMismatchType.FLOOR_ENTRY,
                this::calEntryViewDiffString, "\n");
    }

    private List<CalendarEntry> getCalEntry(SenateSiteCalendar reference){
        List<Integer> billCalNumbers = reference.getBillCalNumbers();
        List<BillId> bill = reference.getBill();
        return  IntStream.range(0,billCalNumbers.size())
                .mapToObj(i -> new CalendarEntry(billCalNumbers.get(i),bill.get(i))).collect(Collectors.toList());
    }

    private List<CalendarEntryView> getCalEntryView(List<CalendarEntry> calendarEntries){
        return calendarEntries.stream()
                .map(calendarEntry ->
                        new CalendarEntryView(calendarEntry,billDataService))
                .collect(Collectors.toList());
    }

    private CalendarView getCalView(Calendar calendar){
        return new CalendarView(calendar, billDataService);
    }

    private String calEntryViewDiffString(CalendarEntryView entry) {
        return String.valueOf(entry.getBillCalNo()) + " " +
                (entry.getBasePrintNo() == null ? "" : entry.getBasePrintNo()) +
                (entry.getSelectedVersion() == null ? "" : entry.getSelectedVersion()) + "-" + String.valueOf(entry.getSession());
    }

    private String calEntryDiffString(CalendarEntry entry){
        return String.valueOf(entry.getBillCalNo()) + " " + (entry.getBillId() == null ? "" : entry.getBillId().toString());
    }
}
