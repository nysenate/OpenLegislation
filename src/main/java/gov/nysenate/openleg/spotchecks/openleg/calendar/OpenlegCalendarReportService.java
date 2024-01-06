package gov.nysenate.openleg.spotchecks.openleg.calendar;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarEntryList;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarSupView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarViewFactory;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarEntryListId;
import gov.nysenate.openleg.spotchecks.base.SpotCheckReportService;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReport;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReportId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Anthony Calabrese 2017/9/6
 * This service is used to perform a calendar spotcheck against calendars from another openleg instance.
 */
@Service("openlegCalendarReport")
public class OpenlegCalendarReportService implements SpotCheckReportService<CalendarEntryListId> {
    private final OpenlegCalendarDao openlegCalendarDao;
    private final CalendarDataService calendarDataService;
    private final CalendarViewFactory calendarViewFactory;
    private final OpenlegCalendarCheckService checkService;

    @Autowired
    public OpenlegCalendarReportService(OpenlegCalendarDao openlegCalendarDao,
                                        CalendarDataService calendarDataService,
                                        CalendarViewFactory calendarViewFactory,
                                        OpenlegCalendarCheckService checkService) {
        this.openlegCalendarDao = openlegCalendarDao;
        this.calendarDataService = calendarDataService;
        this.calendarViewFactory = calendarViewFactory;
        this.checkService = checkService;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.OPENLEG_CAL;
    }

    /**
     * Generate a spotcheck report with reference calendar data from another openleg instance.
     *
     * @param start LocalDateTime - The reference data will be active for the year of this date/time.
     * @param end   LocalDateTime - unused
     * @return report - A SpotcheckReport keyed with CalendarEntryListId
     * @throws Exception
     */
    @Override
    public SpotCheckReport<CalendarEntryListId> generateReport(LocalDateTime start, LocalDateTime end) {
        // Create New spotcheck report
        SpotCheckReport<CalendarEntryListId> report = new SpotCheckReport<>();
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.OPENLEG_CAL, LocalDateTime.now(), LocalDateTime.now());
        report.setReportId(reportId);

        int year = start.getYear();

        // Create Maps of content and source entry lists
        Map<CalendarEntryListId, CalendarEntryList> referenceEntryLists = new HashMap<>();
        Map<CalendarEntryListId, CalendarEntryList> contentEntryLists = new HashMap<>();

        // Populate contentEntryLists with ActiveListViews
        calendarDataService.getActiveLists(year, SortOrder.NONE, LimitOffset.ALL).stream()
                .map(calendarViewFactory::getActiveListView)
                .forEach(alv -> contentEntryLists.put(alv.getCalendarEntryListId(), alv));

        // Populate contentEntryLists with floor / supplemental calendars
        calendarDataService.getCalendarSupplementals(year, SortOrder.NONE, LimitOffset.ALL).stream()
                .map(calendarViewFactory::getCalendarSupView)
                .forEach(sup -> contentEntryLists.put(sup.getCalendarEntryListId(), sup));


        // Retrieve Openleg Ref Calendar data
        List<CalendarView> referenceCalendarViews = openlegCalendarDao.getCalendarViews(year);

        // Populate referenceEntryLists with active lists and floor / supplemental calendars
        for (CalendarView refCal : referenceCalendarViews) {
            refCal.getActiveLists().getItems().values()
                    .forEach(alv -> referenceEntryLists.put(alv.getCalendarEntryListId(), alv));
            CalendarSupView refFloor = refCal.getFloorCalendar();
            referenceEntryLists.put(refFloor.getCalendarEntryListId(), refFloor);

            refCal.getSupplementalCalendars().getItems().values().forEach(calendarSupView ->
                            referenceEntryLists.put(calendarSupView.getCalendarEntryListId(), calendarSupView));
        }

        Set<CalendarEntryListId> allIds = Sets.union(referenceEntryLists.keySet(), contentEntryLists.keySet());

        // Check calendar entry lists
        for (CalendarEntryListId id : allIds) {
            if (!contentEntryLists.containsKey(id)) {
                report.addObservedDataMissingObs(id);
            } else if (!referenceEntryLists.containsKey(id)) {
                report.addRefMissingObs(id);
            } else {
                CalendarEntryList contentEntryList = contentEntryLists.get(id);
                CalendarEntryList refEntryList = referenceEntryLists.get(id);
                SpotCheckObservation<CalendarEntryListId> obs = checkService.check(contentEntryList, refEntryList);
                report.addObservation(obs);
            }
        }

        return report;
    }
}