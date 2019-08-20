package gov.nysenate.openleg.service.spotcheck.openleg;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.client.view.calendar.ActiveListView;
import gov.nysenate.openleg.client.view.calendar.CalendarEntryList;
import gov.nysenate.openleg.client.view.calendar.CalendarSupView;
import gov.nysenate.openleg.client.view.calendar.CalendarView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.reference.openleg.OpenlegCalendarDao;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(OpenlegCalendarReportService.class);

    private final OpenlegCalendarDao openlegCalendarDao;
    private final CalendarDataService calendarDataService;
    private final BillDataService billDataService;
    private final OpenlegCalendarCheckService checkService;

    @Autowired
    public OpenlegCalendarReportService(OpenlegCalendarDao openlegCalendarDao,
                                        CalendarDataService calendarDataService,
                                        BillDataService billDataService,
                                        OpenlegCalendarCheckService checkService) {
        this.openlegCalendarDao = openlegCalendarDao;
        this.calendarDataService = calendarDataService;
        this.billDataService = billDataService;
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
                .map(al -> new ActiveListView(al, billDataService))
                .forEach(alv -> contentEntryLists.put(alv.getCalendarEntryListId(), alv));

        // Populate contentEntryLists with floor / supplemental calendars
        calendarDataService.getCalendarSupplementals(year, SortOrder.NONE, LimitOffset.ALL).stream()
                .map(sup -> new CalendarSupView(sup, billDataService))
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