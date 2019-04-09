package gov.nysenate.openleg.service.spotcheck.senatesite.calendar;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.calendar.CalendarEntryView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupView;
import gov.nysenate.openleg.client.view.calendar.CalendarView;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.senatesite.calendar.SenateSiteCalendar;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by PKS on 2/25/16.
 *
 * Checks nysenate.gov calendar references against openleg calendars
 */
@Service
public class SenateSiteCalendarCheckService
        implements SpotCheckService<CalendarEntryListId, Calendar, SenateSiteCalendar> {

    @Autowired private SpotCheckUtils spotCheckUtils;
    @Autowired private BillDataService billDataService;

    @Override
    public SpotCheckObservation<CalendarEntryListId> check(Calendar content, SenateSiteCalendar reference) {
        SpotCheckObservation<CalendarEntryListId> observation =
                new SpotCheckObservation<>(reference.getReferenceId(), reference.getCalendarEntryListId());
        checkCalendarId(content, reference, observation);
        CalendarView newContent = getCalView(content);
        if (reference.getCalendarType() == CalendarType.ACTIVE_LIST) {
            checkActiveList(newContent, reference, observation);
        } else if (reference.getCalendarType() == CalendarType.SUPPLEMENTAL_CALENDAR) {
            checkSupplemental(newContent, reference, observation);
        } else {
            checkFloor(newContent, reference, observation);
        }
        return observation;
    }

    /* --- Check Methods --- */

    private void checkCalendarId(Calendar content, SenateSiteCalendar reference,
                                 SpotCheckObservation<CalendarEntryListId> observation) {
        spotCheckUtils.checkString(content.getId().toString(), reference.getCalendarId().toString(),
                observation, SpotCheckMismatchType.CALENDAR_ID);
    }

    private void checkActiveList(CalendarView content, SenateSiteCalendar reference,
                                 SpotCheckObservation<CalendarEntryListId> observation) {
        List<CalendarEntryView> calendarEntryViews = content.getActiveLists().getItems()
                .get(reference.getSequenceNo())
                .getEntries()
                .getItems().asList();
        List<CalendarEntryView> refCalEntryViews = getCalEntryViews(reference);
        spotCheckUtils.checkCollection(calendarEntryViews, refCalEntryViews,
                observation, SpotCheckMismatchType.ACTIVE_LIST_ENTRY, this::calEntryViewDiffString, "\n");
    }

    private void checkSupplemental(CalendarView content, SenateSiteCalendar reference,
                                   SpotCheckObservation<CalendarEntryListId> observation) {
        String version = reference.getVersion().toString();
        CalendarSupView calendarSupView = content.getSupplementalCalendars().getItems().get(version);
        List<CalendarEntryView> calendarEntryViews = calendarSupView.getEntriesBySection()
                .getItems().values().stream()
                .map(ListView::getItems)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<CalendarEntryView> refcalendarEntryViews = getCalEntryViews(reference);
        spotCheckUtils.checkCollection(calendarEntryViews, refcalendarEntryViews,
                observation, SpotCheckMismatchType.SUPPLEMENTAL_ENTRY,
                this::calEntryViewDiffString, "\n");
    }

    private void checkFloor(CalendarView content, SenateSiteCalendar reference,
                            SpotCheckObservation<CalendarEntryListId> observation) {
        List<CalendarEntryView> calendarSupEntryViews =
                content.getFloorCalendar().getEntriesBySection().getItems().values().stream()
                .flatMap(contents -> contents.getItems().stream())
                .map(calendarSupEntryView -> (CalendarEntryView) calendarSupEntryView).collect(Collectors.toList());

        List<CalendarEntryView> refcalendarEntryViews = getCalEntryViews(reference);
        spotCheckUtils.checkCollection(calendarSupEntryViews, refcalendarEntryViews,
                observation, SpotCheckMismatchType.FLOOR_ENTRY,
                this::calEntryViewDiffString, "\n");
    }

    /* --- Helper Methods --- */

    /**
     * Generates {@link CalendarEntryView} from data in {@link SenateSiteCalendar}
     */
    private List<CalendarEntryView> getCalEntryViews(SenateSiteCalendar reference) {
        List<Integer> billCalNumbers = reference.getBillCalNumbers();
        List<BillId> bill = reference.getBill();
        return IntStream.range(0, billCalNumbers.size())
                .mapToObj(i -> new CalendarEntry(billCalNumbers.get(i), bill.get(i)))
                .map(entry -> new CalendarEntryView(entry, billDataService))
                .collect(Collectors.toList());
    }

    private CalendarView getCalView(Calendar calendar) {
        return new CalendarView(calendar, billDataService);
    }

    private String calEntryViewDiffString(CalendarEntryView entry) {
        return entry.getBillCalNo() + " " +
                (entry.getBasePrintNo() == null ? "" : entry.getBasePrintNo()) +
                (entry.getSelectedVersion() == null ? "" : entry.getSelectedVersion()) +
                "-" + entry.getSession();
    }

}
