package gov.nysenate.openleg.spotchecks.sensite.calendar;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarEntryView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarSupView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarViewFactory;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarEntry;
import gov.nysenate.openleg.legislation.calendar.CalendarType;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarEntryListId;
import gov.nysenate.openleg.spotchecks.base.SpotCheckService;
import gov.nysenate.openleg.spotchecks.base.SpotCheckUtils;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
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

    private final SpotCheckUtils spotCheckUtils;
    private final CalendarViewFactory calendarViewFactory;

    @Autowired
    public SenateSiteCalendarCheckService(SpotCheckUtils spotCheckUtils, CalendarViewFactory calendarViewFactory) {
        this.spotCheckUtils = spotCheckUtils;
        this.calendarViewFactory = calendarViewFactory;
    }

    @Override
    public SpotCheckObservation<CalendarEntryListId> check(Calendar content, SenateSiteCalendar reference) {
        SpotCheckObservation<CalendarEntryListId> observation =
                new SpotCheckObservation<>(reference.getReferenceId(), reference.getCalendarEntryListId());
        checkCalendarId(content, reference, observation);
        CalendarView newContent = calendarViewFactory.getCalendarView(content);
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
                .getItems();
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
                .map(calendarViewFactory::getCalEntryView)
                .collect(Collectors.toList());
    }

    private String calEntryViewDiffString(CalendarEntryView entry) {
        return entry.getBillCalNo() + " " +
                (entry.getBasePrintNo() == null ? "" : entry.getBasePrintNo()) +
                (entry.getSelectedVersion() == null ? "" : entry.getSelectedVersion()) +
                "-" + entry.getSession();
    }

}
