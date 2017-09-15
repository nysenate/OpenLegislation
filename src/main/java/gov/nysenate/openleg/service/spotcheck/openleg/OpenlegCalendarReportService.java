package gov.nysenate.openleg.service.spotcheck.openleg;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.client.view.calendar.ActiveListView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupEntryView;
import gov.nysenate.openleg.client.view.calendar.CalendarSupView;
import gov.nysenate.openleg.client.view.calendar.CalendarView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.reference.openleg.OpenlegCalenderDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.calendar.CalendarActiveList;
import gov.nysenate.openleg.model.calendar.CalendarSupplemental;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalEntry;
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
import java.util.stream.Collectors;

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
    private  String apiSecret;

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
     * @param start LocalDateTime - The reference data will be active after (or on) this date/time.
     * @param end LocalDateTime - The reference data will be active prior to (or on) this date/time.
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
        logger.info("The current session year is " + SessionYear.of( start.getYear() ) );

        //Retrieve Openleg Ref Calendar data
        List<CalendarView> referenceCalendarViews = openlegCalendarDao.getOpenlegCalendarView(String.valueOf(start.getYear()),apiSecret);
        if (referenceCalendarViews.isEmpty()) {
            throw new ReferenceDataNotFoundEx("The collection of sobi calendars with the given session year " + SessionYear.of( start.getYear() ) + " is empty");
        }

        //Sets of CalendarEntryListId's for both sources of openleg, and both true types of calendars
        Set<CalendarEntryListId> refFloorCalIdSet = new HashSet<>();
        Set<CalendarEntryListId> refActiveListCalIdSet = new HashSet<>();

        Set<CalendarEntryListId> sourceFloorCalIdSet = new HashSet<>();
        Set<CalendarEntryListId> sourceActiveListCalIdSet = new HashSet<>();

        //Collections of the actual data
        List<CalendarSupEntryView> refFloorCal = new ArrayList<>();
        Collection<ActiveListView> refActiveListCal= new ArrayList<>();

        List<CalendarSupEntryView> sourceFloorCal = new ArrayList<>();
        ArrayList<ActiveListView> sourceActiveListCal = new ArrayList<>();


        //********************************************
        // GET REFERENCE / SOURCE CALENDARS AND ID's
        for (CalendarView calendarView: referenceCalendarViews) {

            //**************************************
            //GET REFERENCE CALENDARS AND ID's

            //Get CalendarSupEntryViews from reference calendar
            refFloorCal.addAll( calendarView.getFloorCalendar()
                    .getEntriesBySection()
                    .getItems()
                    .values()
                    .stream()
                    .map(listViewOfCalendarSupEntryView -> listViewOfCalendarSupEntryView.getItems())
                    .flatMap(calendarSupEntryViews -> calendarSupEntryViews.stream())
                    .collect( Collectors.toList() ) );

            //Get CalendarSupViews from reference calendar
            Collection<CalendarSupView> refSupViewCal = calendarView.getSupplementalCalendars()
                    .getItems()
                    .values();

            //Get correct CalendarSupEntryViews of the supplemental calendars and add it to refFloorCal
            for (CalendarSupView calendarSupView:refSupViewCal) {
                refFloorCal.addAll(calendarSupView.getEntriesBySection()
                        .getItems()
                        .values()
                        .stream()
                        .map(listViewOfCalendarSupEntryView -> listViewOfCalendarSupEntryView.getItems())
                        .flatMap(calendarSupEntryViews -> calendarSupEntryViews.stream())
                        .collect( Collectors.toList() ) );
            }

            //Add ID's from CalendarSupEntryView to refFloorCalIdSet
            for (CalendarSupEntryView calendarSupEntryView: refFloorCal) {
                refFloorCalIdSet.add( calendarSupEntryView.getCalendarEntryListId() );
            }

            //Get Active list calendars from reference calendarView
            refActiveListCal.addAll(calendarView.getActiveLists()
                    .getItems()
                    .values());

            //Add ID's from activelistView to refActiveListCalIdSet
            for (ActiveListView activeListView: refActiveListCal) {
                refActiveListCalIdSet.add( activeListView.getCalendarEntryListId() );
            }

            //**********************************
            // GET SOURCE CALENDARS AND ID's

            List<CalendarSupplementalEntry> calendarSupplementalEntryList = new ArrayList<>();

            //Get CalendarSupplementals from the calendarDataService
            List<CalendarSupplemental> sourceCalSupplemental = calendarDataService.getCalendarSupplementals(start.getYear(),SortOrder.ASC, LimitOffset.ALL);

            //Retrieve the CalendarSupplementalEntries from the CalendarSupplementals, and add them to calendarSupplementalEntryList
            for (CalendarSupplemental calendarSupplemental: sourceCalSupplemental) {
                calendarSupplementalEntryList.addAll( calendarSupplemental.getAllEntries() );
            }

            //Convert CalendarSupplementalEntries to CalendarSupEntryViews and add them to sourceFloorCal
            for (CalendarSupplementalEntry calendarSupplementalEntry: calendarSupplementalEntryList) {
                sourceFloorCal.add( new CalendarSupEntryView( calendarSupplementalEntry, billDataService ) );
            }

            //Add CalendarEntryListID's to sourceFloorCalIdSet
            for (CalendarSupEntryView calendarSupEntryView: sourceFloorCal) {
                sourceFloorCalIdSet.add( calendarSupEntryView.getCalendarEntryListId() );
            }

            //Get CalendarActiveLists from the calendarDataService
            List<CalendarActiveList> sourceCalActiveLists = calendarDataService.getActiveLists( start.getYear() , SortOrder.ASC, LimitOffset.ALL);

            //Convert CalendarActiveList to ActiveListView
            for(CalendarActiveList calendarActiveList: sourceCalActiveLists) {
                sourceActiveListCal.add( new ActiveListView(calendarActiveList,billDataService) );
            }

            //Add CalendarEntryListId's to sourceActiveListCalIdSet
            for(ActiveListView activeListView: sourceActiveListCal) {
                sourceActiveListCalIdSet.add( activeListView.getCalendarEntryListId() );
            }
        }
        //ALL DATA HAS BEEN RETRIEVED
        //******************************

        logger.info("Check the symmetric diff...");

        //Create symmetric difference sets
        Set<CalendarEntryListId> symmDiffFloorCals;
        Set<CalendarEntryListId> symmDiffActiveLists;

        //Get the symmetric difference between ref and source floor calendars
        logger.info("Found " + refFloorCalIdSet.size()+" floor calendars in Openleg-ref(SOBI) and " + sourceFloorCalIdSet.size()+" floor calendars in local (XML)" );
        symmDiffFloorCals = getSymmetricDifference(refFloorCalIdSet, sourceFloorCalIdSet, report, reportId);

        //Get the symmetric difference between ref and source active lists
        logger.info("Found " + refActiveListCalIdSet.size()+" active lists in Openleg-ref(SOBI) and " + sourceActiveListCalIdSet.size()+" active lists in local (XML)" );
        symmDiffActiveLists = getSymmetricDifference(refActiveListCalIdSet, sourceActiveListCalIdSet, report, reportId);

        logger.info("Found " + report.getOpenMismatchCount(false) + "missing calendar entries");

        //*************************************************
        //PASS DATA SOURCE AND REF HAVE TO THE CHECK SERVICE

        //Pass floor calenders in symmetric difference to the calendar check service
        for(CalendarSupEntryView refCalendarSupEntryView: refFloorCal) {
            if ( symmDiffFloorCals.contains( refCalendarSupEntryView.getCalendarEntryListId() ) )
                continue;
            SpotCheckObservation<CalendarEntryListId> observation = checkService.checkFloorCals(refCalendarSupEntryView,
                    sourceFloorCal.get( sourceFloorCal.indexOf(refCalendarSupEntryView) ) );
            addObservationData(observation,report,reportId);
        }

        //Pass active lists in symmetric difference to the calendar check service
        for(ActiveListView refActiveListView: refActiveListCal) {
            if ( symmDiffActiveLists.contains( refActiveListView.getCalendarEntryListId() ) )
                continue;
            SpotCheckObservation<CalendarEntryListId> observation = checkService.checkActiveLists(refActiveListView,
                    sourceActiveListCal.get( sourceActiveListCal.indexOf(refActiveListView) ) );
            addObservationData(observation,report,reportId);
        }
        logger.info("Found total number of " + report.getOpenMismatchCount(false) + " mismatches");

        return report;
    }


    /**
     * This method adds ID data to obervations inside the spotcheck report,
     * and then adds the observation to the spot check report
     * @param observation errors from comparing two floor calendars or active lists
     * @param report The spotcheck report
     * @param reportId the ID of the spotcheck report
     */
    private void addObservationData(SpotCheckObservation<CalendarEntryListId> observation,
                                    SpotCheckReport<CalendarEntryListId> report, SpotCheckReportId reportId ) {
        SpotCheckReferenceId referenceId = reportId.getReferenceId();
        observation.setReferenceId(referenceId);
        observation.setObservedDateTime(LocalDateTime.now());
        report.addObservation(observation);
    }

    /**
     * This method determines and returns the symmetric difference between two sets
     * @param refIdSet - Set of reference CalendarEntryListsId's
     * @param sourceIdSet - Set of source CalendarEntryListId's
     * @param report - The spotcheck report itself
     * @param reportId - The Id of the spotcheck report
     * @return difference - The symmetric difference between refIdSet & sourceIdSet
     */
    private Set<CalendarEntryListId> getSymmetricDifference(Set<CalendarEntryListId> refIdSet, Set<CalendarEntryListId> sourceIdSet,
                                                            SpotCheckReport<CalendarEntryListId> report, SpotCheckReportId reportId ) {
        Set<CalendarEntryListId> difference = new HashSet<>();

        Sets.symmetricDifference(refIdSet, sourceIdSet).stream()
                .forEach(entryListId -> {
                    SpotCheckObservation<CalendarEntryListId> sourceMissingObs = new SpotCheckObservation<>(reportId.getReferenceId(), entryListId);
                    if (sourceIdSet.contains(entryListId)) {
                        sourceMissingObs.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING, entryListId, "Missing Data from Openleg Ref, ID:" + entryListId.toString() ));

                    } else {
                        sourceMissingObs.addMismatch(new SpotCheckMismatch(OBSERVE_DATA_MISSING, entryListId, "Missing Data from Openleg XML, ID:" + entryListId.toString() ));
                    }
                    difference.add(entryListId);
                    sourceMissingObs.setReferenceId(reportId.getReferenceId());
                    sourceMissingObs.setObservedDateTime(LocalDateTime.now());
                    report.addObservation(sourceMissingObs);
                });

        return difference;
    }
}
