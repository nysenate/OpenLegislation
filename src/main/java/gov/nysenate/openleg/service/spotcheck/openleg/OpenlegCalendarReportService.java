package gov.nysenate.openleg.service.spotcheck.openleg;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.client.view.calendar.ActiveListView;
import gov.nysenate.openleg.client.view.calendar.CalendarEntryList;
import gov.nysenate.openleg.client.view.calendar.CalendarSupView;
import gov.nysenate.openleg.client.view.calendar.CalendarView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.reference.openleg.OpenlegCalenderDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarSupplemental;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.OBSERVE_DATA_MISSING;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.REFERENCE_DATA_MISSING;

/*
Created by Anthony Calabrese 2017/9/6
This service is used to report the difference between two calendars in two instances of Openleg
 */
@Service("openlegCalendarReport")
public class OpenlegCalendarReportService extends BaseSpotCheckReportService<CalendarEntryListId> {
    private static final Logger logger = LoggerFactory.getLogger(OpenlegCalendarReportService.class);

    @Value("api.secret")
    private String apiSecret;

    @Autowired
    private SpotCheckReportDao<CalendarEntryListId> reportDao;

    @Autowired
    private OpenlegCalenderDao openlegCalendarDao;

    @Autowired
    private CalendarDataService calendarDataService;

    @Autowired
    private BillDataService billDataService;

    @Autowired
    OpenlegCalendarCheckService checkService;

    @Override
    protected SpotCheckReportDao<CalendarEntryListId> getReportDao() {
        return reportDao;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.OPENLEG_CAL;
    }

    /**
     * This service finds discrepencies between two calendars in two different instances of Openleg
     *
     * @param start LocalDateTime - The reference data will be active after (or on) this date/time.
     * @param end   LocalDateTime - The reference data will be active prior to (or on) this date/time.
     * @return report - A SpotcheckReport keyed with CalendarEntryListId
     * @throws Exception
     */
    @Override
    public SpotCheckReport<CalendarEntryListId> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        logger.info("Start generating new calendar spot check report between Openleg Ref and XML Branch");

        //Create New spotcheck report
        SpotCheckReport<CalendarEntryListId> report = new SpotCheckReport<>();
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.OPENLEG_CAL, LocalDateTime.now(), LocalDateTime.now());
        report.setReportId(reportId);

        logger.info("Loading Calendars from Openleg Ref");
        logger.info("The current session year is " + SessionYear.of(start.getYear()));

        //Retrieve Openleg Ref Calendar data
        List<CalendarView> referenceCalendarViews = openlegCalendarDao.getOpenlegCalendarView(String.valueOf(start.getYear()), apiSecret);
        if (referenceCalendarViews.isEmpty()) {
            throw new ReferenceDataNotFoundEx("The collection of sobi calendars with the given session year " + SessionYear.of(start.getYear()) + " is empty");
        }

        //Create Maps of content and source entry lists
        Map<CalendarEntryListId, CalendarEntryList> referenceEntryLists = new HashMap<>();
        Map<CalendarEntryListId, CalendarEntryList> contentEntryLists = new HashMap<>();

        //Populate contentEntryLists with ActiveListViews
        calendarDataService.getActiveLists(start.getYear(), SortOrder.NONE, LimitOffset.ALL).stream()
                .map(al -> new ActiveListView(al, billDataService))
                .forEach(alv -> contentEntryLists.put(alv.getCalendarEntryListId(), alv));

        //Populate contentEntryLists with floor / supplemental calendars
        calendarDataService.getCalendarSupplementals(start.getYear(), SortOrder.NONE, LimitOffset.ALL).stream()
                .map(sup -> new CalendarSupView(sup, billDataService))
                .forEach(sup -> contentEntryLists.put(sup.getCalendarEntryListId(), sup));

        //Populate referenceEntryLists with active lists and floor / supplemental calendars
        for (CalendarView refCal : referenceCalendarViews) {
            refCal.getActiveLists().getItems().values()
                    .forEach(alv -> referenceEntryLists.put(alv.getCalendarEntryListId(), alv));
            CalendarSupView refFloor = refCal.getFloorCalendar();
            referenceEntryLists.put(refFloor.getCalendarEntryListId(), refFloor);

            refCal.getSupplementalCalendars()
                    .getItems()
                    .values()
                    .stream()
                    .forEach(calendarSupView ->
                            referenceEntryLists.put(calendarSupView.getCalendarEntryListId(), calendarSupView));
        }

        logger.info("Found " + referenceEntryLists.size()+" calendar entries in Openleg-ref(SOBI) and " + contentEntryLists.size()+" calendar entries in local (XML)" );
        logger.info("Check the symmetric diff...");

        //Put content Id's a set
        Set<CalendarEntryListId> remainingContentIds = new HashSet<>(contentEntryLists.keySet());

        //Find symmetric difference between content and reference id's
        referenceEntryLists.forEach((id, refEntryList) -> {
            if (contentEntryLists.containsKey(id)) {
                //Both Calendars have the same EntryList item
                CalendarEntryList contentEntryList = contentEntryLists.get(id);
                SpotCheckObservation<CalendarEntryListId> observation = checkService.check(contentEntryList, refEntryList);
                addObservationData(observation, report, reportId);
            } else {
                //add data missing
                SpotCheckObservation<CalendarEntryListId> sourceMissingObs = new SpotCheckObservation<>(reportId.getReferenceId(), id);
                sourceMissingObs.addMismatch(new SpotCheckMismatch(OBSERVE_DATA_MISSING, id, "Missing Data from Openleg XML, ID:" + id.toString()));
                addObservationData(sourceMissingObs,report,reportId);
            }
            remainingContentIds.remove(id);
        });

        remainingContentIds.forEach(id -> {
            // add ref missing
            SpotCheckObservation<CalendarEntryListId> refMissingObs = new SpotCheckObservation<>(reportId.getReferenceId(), id);
            refMissingObs.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING, id, "Missing Data from Openleg Ref, ID:" + id.toString()));
            addObservationData(refMissingObs,report,reportId);
        });

        logger.info("Found total number of " + report.getOpenMismatchCount(false) + " mismatches");

        return report;
    }

    /**
     * This method adds ID data to obervations inside the spotcheck report,
     * and then adds the observation to the spot check report
     *
     * @param observation errors from comparing two floor calendars or active lists
     * @param report      The spotcheck report
     * @param reportId    the ID of the spotcheck report
     */
    private void addObservationData(SpotCheckObservation<CalendarEntryListId> observation,
                                    SpotCheckReport<CalendarEntryListId> report, SpotCheckReportId reportId) {
        SpotCheckReferenceId referenceId = reportId.getReferenceId();
        observation.setReferenceId(referenceId);
        observation.setObservedDateTime(LocalDateTime.now());
        report.addObservation(observation);
    }
}